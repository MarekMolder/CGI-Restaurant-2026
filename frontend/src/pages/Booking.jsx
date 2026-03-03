import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { listFeatures } from '../api/features';
import { listZones } from '../api/zones';
import { getOpeningHours } from '../api/restaurantHours';
import { listByZone, getAvailableTables } from '../api/tableEntities';
import { createBooking } from '../api/bookings';
import { useAuth } from '../context/AuthContext';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';
import indoorImage from '../assets/indoor.png';
import outsideImage from '../assets/outside.png';
import privateImage from '../assets/private.png';

const ZONE_TYPE_IMAGE = {
  INDOOR: indoorImage,
  TERRACE: outsideImage,
  PRIVATE: privateImage,
};
const DEFAULT_ZONE_IMAGE = indoorImage;
const PLAN_WIDTH = 800;
const PLAN_HEIGHT = 600;

function parseTime(str) {
  const [h, m] = str.split(':').map(Number);
  return h * 60 + m;
}

function formatTime(minutes) {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
}

function getTimeSlotsForDate(dateStr, openingHours) {
  if (!dateStr || !openingHours) return [];
  const date = new Date(dateStr + 'T12:00:00');
  const day = date.getDay();
  const isWeekend = day === 0 || day === 6;
  const open = parseTime(isWeekend ? openingHours.weekendOpen : openingHours.weekdayOpen);
  const close = parseTime(isWeekend ? openingHours.weekendClose : openingHours.weekdayClose);
  const step = openingHours.bookingDurationHours * 60;
  const slots = [];
  for (let t = open; t + step <= close; t += step) {
    slots.push(formatTime(t));
  }
  return slots;
}

function getMinDate() {
  const d = new Date();
  return d.toISOString().slice(0, 10);
}

/** Tänase päeva puhul jätame välja möödunud kellaajad (kohalik aeg). */
function filterSlotsForToday(slots, dateStr) {
  if (!dateStr || !slots.length) return slots;
  const today = getMinDate();
  if (dateStr !== today) return slots;
  const now = new Date();
  return slots.filter((slot) => {
    const [h, m] = slot.split(':').map(Number);
    const slotStart = new Date(now.getFullYear(), now.getMonth(), now.getDate(), h, m, 0, 0);
    return slotStart > now;
  });
}

/** Table status for floor plan: booked | available | bestMatch | noMatch | selected */
function tableColorClass(status) {
  switch (status) {
    case 'booked':
      return 'bg-red-600/90 border-red-400 text-white';
    case 'selected':
      return 'bg-violet-600/95 border-violet-300 text-white ring-2 ring-violet-300';
    case 'bestMatch':
      return 'bg-amber-500/90 border-amber-300 text-stone-900';
    case 'available':
      return 'bg-green-600/90 border-green-400 text-white';
    default:
      return 'bg-stone-600/80 border-stone-500 text-stone-300';
  }
}

function tableShapeClass(shape) {
  if (shape === 'CIRCLE') return 'rounded-full';
  if (shape === 'OVAL') return 'rounded-[50%]';
  return 'rounded-lg';
}

function BookingTable({ table, status, isPartOfCombined, onClick }) {
  const leftPct = (table.x / PLAN_WIDTH) * 100;
  const topPct = (table.y / PLAN_HEIGHT) * 100;
  const widthPct = (table.width / PLAN_WIDTH) * 100;
  const heightPct = (table.height / PLAN_HEIGHT) * 100;
  const isBooked = status === 'booked';
  return (
    <button
      type="button"
      onClick={() => !isBooked && onClick(table)}
      disabled={isBooked}
      className={`absolute border-2 flex flex-col items-center justify-center text-xs font-medium select-none min-w-[20px] min-h-[20px] ${tableColorClass(status)} ${tableShapeClass(table.shape)} transition-opacity ${isBooked ? 'cursor-not-allowed' : 'cursor-pointer hover:opacity-95'}`}
      style={{
        left: `${leftPct}%`,
        top: `${topPct}%`,
        width: `${widthPct}%`,
        height: `${heightPct}%`,
        fontSize: 'clamp(6px, 1vw, 11px)',
      }}
      title={isPartOfCombined ? `${table.label} (kombineeritud)` : table.label}
    >
      <span className="truncate w-full text-center px-0.5">{table.label}</span>
      <span className="opacity-90">{table.capacity}</span>
    </button>
  );
}

export default function Booking() {
  const { userEmail } = useAuth();
  const [zones, setZones] = useState([]);
  const [features, setFeatures] = useState([]);
  const [openingHours, setOpeningHours] = useState(null);
  const [tables, setTables] = useState([]);
  const [availabilityResults, setAvailabilityResults] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searching, setSearching] = useState(false);
  const [bookingSubmitting, setBookingSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [bookingSuccess, setBookingSuccess] = useState(null);

  const [date, setDate] = useState('');
  const [time, setTime] = useState('');
  const [selectedFeatureIds, setSelectedFeatureIds] = useState([]);
  const [zoneId, setZoneId] = useState('');
  const [partySize, setPartySize] = useState(2);
  const [selectedOption, setSelectedOption] = useState(null);
  const [guestName, setGuestName] = useState('');
  const [specialRequests, setSpecialRequests] = useState('');

  const timeSlotsRaw = getTimeSlotsForDate(date, openingHours);
  const timeSlots = filterSlotsForToday(timeSlotsRaw, date);

  useEffect(() => {
    let cancelled = false;
    setError('');
    Promise.all([listZones(), listFeatures(), getOpeningHours()])
      .then(([z, f, hours]) => {
        if (!cancelled) {
          setZones(z);
          setFeatures(f);
          setOpeningHours(hours);
          if (z.length > 0 && !zoneId) setZoneId(z[0].id);
        }
      })
      .catch((err) => {
        if (!cancelled) setError(err.message || 'Andmeid ei saadud');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    if (time && timeSlots.length && !timeSlots.includes(time)) {
      setTime(timeSlots[0] || '');
    }
  }, [date, time, timeSlots]);

  useEffect(() => {
    if (!timeSlots.includes(time)) setTime(timeSlots[0] || '');
  }, [date, timeSlots]);

  useEffect(() => {
    if (!zoneId) {
      setTables([]);
      return;
    }
    let cancelled = false;
    listByZone(zoneId)
      .then((list) => {
        if (!cancelled) setTables(Array.isArray(list) ? list : []);
      })
      .catch(() => {
        if (!cancelled) setTables([]);
      });
    return () => { cancelled = true; };
  }, [zoneId]);

  const canSearch = date && time && zoneId && partySize >= 1 && partySize <= 50;

  const runSearch = useCallback(async () => {
    if (!canSearch || !openingHours) return;
    setError('');
    setSearching(true);
    setSelectedOption(null);
    setBookingSuccess(null);
    const durationHours = openingHours.bookingDurationHours || 2;
    const startAt = `${date}T${time}:00`;
    const endAt = `${date}T${String(Number(time.slice(0, 2)) + durationHours).padStart(2, '0')}:${time.slice(3, 5)}:00`;
    try {
      const list = await getAvailableTables(zoneId, partySize, startAt, endAt, selectedFeatureIds);
      setAvailabilityResults(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(err.message || 'Vabu laudu ei saadud');
      setAvailabilityResults([]);
    } finally {
      setSearching(false);
    }
  }, [canSearch, openingHours, date, time, zoneId, partySize, selectedFeatureIds]);

  const tableStatusMap = useCallback(() => {
    if (!availabilityResults || availabilityResults.length === 0) {
      return {};
    }
    const maxScore = Math.max(
      ...availabilityResults.filter((a) => a.available).map((a) => a.recommendationScore ?? 0),
      0
    );
    const map = {};
    for (const opt of availabilityResults) {
      const isBest = opt.available && (opt.recommendationScore ?? 0) >= maxScore - 15;
      for (const tid of opt.tableIds || []) {
        const id = typeof tid === 'string' ? tid : tid;
        if (!map[id]) map[id] = { isBooked: false, isAvailable: false, bestMatch: false };
        if (!opt.available) map[id].isBooked = true;
        else {
          map[id].isAvailable = true;
          if (isBest) map[id].bestMatch = true;
        }
      }
    }
    return map;
  }, [availabilityResults]);

  const statusMap = tableStatusMap();
  const getStatus = (tableId) => {
    if (selectedOption && selectedOption.tableIds && selectedOption.tableIds.includes(tableId))
      return 'selected';
    const s = statusMap[tableId];
    if (!s) return 'noMatch';
    if (s.isBooked) return 'booked';
    if (s.bestMatch) return 'bestMatch';
    if (s.isAvailable) return 'available';
    return 'noMatch';
  };

  const handleTableClick = (table) => {
    if (!availabilityResults) return;
    const option = availabilityResults.find((o) => o.tableIds && o.tableIds.includes(table.id));
    if (option && option.available) setSelectedOption(option);
  };

  const handleBookingSubmit = async (e) => {
    e.preventDefault();
    if (!selectedOption || !canSearch || !openingHours || !userEmail) return;
    if (!guestName.trim()) {
      setError('Sisesta oma nimi');
      return;
    }
    setError('');
    setBookingSubmitting(true);
    const durationHours = openingHours.bookingDurationHours || 2;
    const startAt = `${date}T${time}:00`;
    const endAt = `${date}T${String(Number(time.slice(0, 2)) + durationHours).padStart(2, '0')}:${time.slice(3, 5)}:00`;
    try {
      await createBooking({
        guestName: guestName.trim(),
        guestEmail: userEmail,
        startAt,
        endAt,
        partySize,
        status: 'CONFIRMED',
        specialRequests: specialRequests.trim() || undefined,
        bookingPreferences: (selectedFeatureIds || []).map((featureId) => ({
          featureId,
          priority: 'HIGH',
        })),
        bookingTables: (selectedOption.tableIds || []).map((id) => ({ tableEntityId: id })),
      });
      setBookingSuccess(true);
      setSelectedOption(null);
      setAvailabilityResults(null);
    } catch (err) {
      setError(err.message || 'Broneering ebaõnnestus');
    } finally {
      setBookingSubmitting(false);
    }
  };

  function handleFeatureToggle(featureId) {
    setSelectedFeatureIds((prev) =>
      prev.includes(featureId) ? prev.filter((id) => id !== featureId) : [...prev, featureId]
    );
  }

  const selectedZone = zones.find((z) => z.id === zoneId);
  const zoneImage = selectedZone ? (ZONE_TYPE_IMAGE[selectedZone.type] ?? DEFAULT_ZONE_IMAGE) : null;
  const displayImage = zoneImage ?? DEFAULT_ZONE_IMAGE;

  const floorPlanSection = (
    <div className="rounded-2xl overflow-hidden border border-restaurant-border shadow-2xl bg-restaurant-card w-fit max-w-full relative min-h-[200px]">
      <img
        src={displayImage}
        alt={selectedZone ? selectedZone.name : 'Tsooni pilt'}
        className="block max-h-[90vh] max-w-full w-auto h-auto object-contain"
      />
      <div className="absolute inset-0">
        {tables.map((t) => (
          <BookingTable
            key={t.id}
            table={t}
            status={getStatus(t.id)}
            isPartOfCombined={
              selectedOption?.combined && selectedOption?.tableIds?.includes(t.id)
            }
            onClick={handleTableClick}
          />
        ))}
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-restaurant-dark flex flex-col">
      <header className="border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md shrink-0 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 py-3">
          <Link
            to="/"
            className="text-restaurant-gold hover:text-restaurant-goldLight transition font-display inline-flex items-center gap-2"
          >
            ← Avaleht
          </Link>
        </div>
      </header>
      <div className="flex-1 flex flex-col lg:flex-row min-h-0">
        <div className="flex-1 lg:flex-[1.6] lg:min-w-0 lg:min-h-0 lg:overflow-auto py-8 px-6 lg:px-16 lg:border-r lg:border-restaurant-border">
          <h1 className="font-display text-3xl text-restaurant-gold mb-2">Broneeri laud</h1>
          <p className="text-stone-500 mb-6">Vali kuupäev, kellaaeg, tsoon ja eelistused</p>

          {loading && <p className="text-stone-400">Laen filtreid…</p>}
          {error && (
            <div className="mb-6 p-4 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
              {error}
            </div>
          )}
          {bookingSuccess && (
            <div className="mb-6 p-4 rounded-lg bg-green-500/10 border border-green-500/30 text-green-400 text-sm">
              Broneering on tehtud.
            </div>
          )}

          {!loading && (
            <Card title="Filtrid">
              <form className="space-y-6" onSubmit={(e) => { e.preventDefault(); runSearch(); }}>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <Input
                    label="Kuupäev"
                    name="date"
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    min={getMinDate()}
                    required
                  />
                  <div>
                    <label className="block text-sm font-medium text-stone-400 mb-1">
                      Kellaaeg <span className="text-restaurant-gold">*</span>
                    </label>
                    <select
                      name="time"
                      value={time}
                      onChange={(e) => setTime(e.target.value)}
                      required
                      className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:outline-none focus:ring-2 focus:ring-restaurant-gold focus:border-transparent"
                    >
                      <option value="">Vali kellaaeg</option>
                      {timeSlots.map((slot) => (
                        <option key={slot} value={slot}>
                          {slot} (kestus {openingHours?.bookingDurationHours ?? 2} h)
                        </option>
                      ))}
                    </select>
                    {date && timeSlots.length === 0 && (
                      <p className="mt-1 text-sm text-amber-400">Sellel kuupäeval pole vabu aegu.</p>
                    )}
                  </div>
                </div>

                <Input
                  label="Osalejate arv"
                  name="partySize"
                  type="number"
                  min={1}
                  max={50}
                  value={partySize}
                  onChange={(e) => setPartySize(Number(e.target.value) || 1)}
                  required
                />

                <div>
                  <label className="block text-sm font-medium text-stone-400 mb-2">Tsoon</label>
                  <select
                    name="zoneId"
                    value={zoneId}
                    onChange={(e) => setZoneId(e.target.value)}
                    className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:outline-none focus:ring-2 focus:ring-restaurant-gold focus:border-transparent"
                  >
                    <option value="">Vali tsoon</option>
                    {zones.map((z) => (
                      <option key={z.id} value={z.id}>
                        {z.name}
                      </option>
                    ))}
                  </select>
                </div>

                {features.length > 0 && (
                  <div>
                    <label className="block text-sm font-medium text-stone-400 mb-2">
                      Eelistused (feature'd)
                    </label>
                    <div className="flex flex-wrap gap-3">
                      {features.map((f) => (
                        <label key={f.id} className="inline-flex items-center gap-2 cursor-pointer">
                          <input
                            type="checkbox"
                            checked={selectedFeatureIds.includes(f.id)}
                            onChange={() => handleFeatureToggle(f.id)}
                            className="rounded border-restaurant-border bg-restaurant-card text-restaurant-gold focus:ring-restaurant-gold"
                          />
                          <span className="text-stone-300">{f.name}</span>
                        </label>
                      ))}
                    </div>
                  </div>
                )}

                <Button type="submit" disabled={!canSearch || searching} className="w-full">
                  {searching ? 'Otsin…' : 'Otsi vabu laudu'}
                </Button>
              </form>

              {selectedOption && (
                <div className="mt-6 pt-6 border-t border-restaurant-border">
                  <p className="text-stone-400 text-sm mb-2">
                    Valitud: {selectedOption.label}
                    {selectedOption.combined && ' (2 lauda kokku)'}
                  </p>
                  <form onSubmit={handleBookingSubmit} className="space-y-4">
                    <Input
                      label="Broneerija nimi"
                      value={guestName}
                      onChange={(e) => setGuestName(e.target.value)}
                      required
                      placeholder="Sinu nimi"
                    />
                    <div>
                      <label className="block text-sm font-medium text-stone-400 mb-1">
                        Lisasoovid (valikuline)
                      </label>
                      <textarea
                        value={specialRequests}
                        onChange={(e) => setSpecialRequests(e.target.value)}
                        placeholder="Erimärkused, allergiad, tähtpäev jms"
                        className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:outline-none focus:ring-2 focus:ring-restaurant-gold focus:border-transparent resize-y min-h-[80px]"
                        rows={3}
                        maxLength={2000}
                      />
                    </div>
                    <Button type="submit" disabled={bookingSubmitting} className="w-full">
                      {bookingSubmitting ? 'Broneerin…' : 'Broneeri laud'}
                    </Button>
                  </form>
                </div>
              )}
            </Card>
          )}

          {availabilityResults && (
            <div className="mt-4 text-stone-500 text-xs">
              <span className="inline-block w-3 h-3 rounded bg-red-600/90 mr-1" /> Kinni
              <span className="inline-block w-3 h-3 rounded bg-green-600/90 ml-3 mr-1" /> Vaba
              <span className="inline-block w-3 h-3 rounded bg-amber-500/90 ml-3 mr-1" /> Parim sobivus
              <span className="inline-block w-3 h-3 rounded bg-stone-600/80 ml-3 mr-1" /> Ei sobi
            </div>
          )}
        </div>

        <div className="hidden lg:flex lg:flex-1 lg:min-h-0 lg:items-center lg:justify-end lg:py-8 lg:pl-6 lg:pr-6 xl:pr-8">
          <div className="max-h-[90vh] max-w-full [&>div]:max-h-[90vh] [&>div]:max-w-full">
            {floorPlanSection}
          </div>
        </div>

        <div className="lg:hidden mt-6 rounded-xl overflow-hidden border border-restaurant-border mx-4 bg-restaurant-card flex items-center justify-center min-h-[220px] py-4">
          <div className="w-full max-w-full max-h-80 [&>div]:max-h-80">{floorPlanSection}</div>
        </div>
      </div>
    </div>
  );
}
