import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { listFeatures } from '../api/features';
import { listZones } from '../api/zones';
import { getOpeningHours } from '../api/restaurantHours';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';
import indoorImage from '../assets/indoor.png';
import outsideImage from '../assets/outside.png';
import privateImage from '../assets/private.png';

/** Tsooni tüüp → pilt (backendi ZoneTypeEnum: INDOOR, TERRACE, PRIVATE jms). */
const ZONE_TYPE_IMAGE = {
  INDOOR: indoorImage,
  TERRACE: outsideImage,
  PRIVATE: privateImage,
};
const DEFAULT_ZONE_IMAGE = indoorImage;

function parseTime(str) {
  const [h, m] = str.split(':').map(Number);
  return h * 60 + m;
}

function formatTime(minutes) {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
}

/** Genereerib võimalikud broneerimise alguskellaajad valitud kuupäeva järgi, kasutades backendi lahtiolekuaegu. */
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

export default function Booking() {
  const [zones, setZones] = useState([]);
  const [features, setFeatures] = useState([]);
  const [openingHours, setOpeningHours] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [date, setDate] = useState('');
  const [time, setTime] = useState('');
  const [selectedFeatureIds, setSelectedFeatureIds] = useState([]);
  const [zoneId, setZoneId] = useState('');
  const [partySize, setPartySize] = useState(2);

  const timeSlots = getTimeSlotsForDate(date, openingHours);

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
    if (!timeSlots.includes(time)) setTime(timeSlots[0] || '');
  }, [date, timeSlots]);

  function handleFeatureToggle(featureId) {
    setSelectedFeatureIds((prev) =>
      prev.includes(featureId)
        ? prev.filter((id) => id !== featureId)
        : [...prev, featureId]
    );
  }

  const canSearch = date && time && zoneId && partySize >= 1 && partySize <= 50;
  const selectedZone = zones.find((z) => z.id === zoneId);
  const zoneImage = selectedZone
    ? (ZONE_TYPE_IMAGE[selectedZone.type] ?? DEFAULT_ZONE_IMAGE)
    : null;

  const displayImage = zoneImage ?? DEFAULT_ZONE_IMAGE;

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
        {/* Vasak: filtrid — enamus ala (ca 62%), laiem aknasuurus */}
        <div className="flex-1 lg:flex-[1.6] lg:min-w-0 lg:min-h-0 lg:overflow-auto py-8 px-6 lg:px-16 lg:border-r lg:border-restaurant-border">
          <h1 className="font-display text-3xl text-restaurant-gold mb-2">Broneeri laud</h1>
        <p className="text-stone-500 mb-6">Vali kuupäev, kellaaeg, tsoon ja eelistused</p>

        {loading && (
          <p className="text-stone-400">Laen filtreid…</p>
        )}
        {error && (
          <div className="mb-6 p-4 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
            {error}
          </div>
        )}

        {!loading && (
          <Card title="Filtrid">
            <form
              className="space-y-6"
              onSubmit={(e) => {
                e.preventDefault();
                if (!canSearch) return;
                // Järgmine samm: vabu laudade päring ja valik (teha eraldi)
              }}
            >
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
                  <label className="block text-sm font-medium text-stone-400 mb-2">Eelistused (feature’d)</label>
                  <div className="flex flex-wrap gap-3">
                    {features.map((f) => (
                      <label
                        key={f.id}
                        className="inline-flex items-center gap-2 cursor-pointer"
                      >
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

              <Button type="submit" disabled={!canSearch} className="w-full">
                Otsi vabu laudu
              </Button>
            </form>
          </Card>
        )}
      </div>

        {/* Parem: pilt surutud paremale, kast pildiga samasuur, suurem kõrgus */}
        <div className="hidden lg:flex lg:flex-1 lg:min-h-0 lg:items-center lg:justify-end lg:py-8 lg:pl-6 lg:pr-6 xl:pr-8">
          <div className="rounded-2xl overflow-hidden border border-restaurant-border shadow-2xl bg-restaurant-card w-fit max-w-full">
            <img
              src={displayImage}
              alt={selectedZone ? selectedZone.name : 'Tsooni pilt'}
              className="block max-h-[90vh] max-w-full w-auto h-auto object-contain"
            />
          </div>
        </div>

        {/* Mobiilil: pilt all, tervikuna */}
        <div className="lg:hidden mt-6 rounded-xl overflow-hidden border border-restaurant-border mx-4 bg-restaurant-card flex items-center justify-center min-h-[220px] py-4">
          <img
            src={displayImage}
            alt={selectedZone ? selectedZone.name : 'Tsooni pilt'}
            className="max-w-full max-h-80 w-auto h-auto object-contain"
          />
        </div>
      </div>
    </div>
  );
}
