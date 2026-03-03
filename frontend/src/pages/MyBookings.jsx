import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { listMyBookings, getBooking } from '../api/bookings';

function formatDate(iso) {
  if (!iso) return '–';
  const d = new Date(iso);
  return d.toLocaleDateString('et-EE', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });
}

function formatTime(iso) {
  if (!iso) return '–';
  const d = new Date(iso);
  return d.toLocaleTimeString('et-EE', { hour: '2-digit', minute: '2-digit' });
}

function statusLabel(status) {
  const map = { CONFIRMED: 'Kinnitatud', PENDING: 'Ootab', CANCELLED: 'Tühistatud', NO_SHOW: 'Ilmumata', COMPLETED: 'Tehtud' };
  return map[status] ?? status;
}

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    setError('');
    listMyBookings(0, 50)
      .then((page) => {
        const list = page?.content ?? [];
        if (!cancelled && list.length === 0) {
          setBookings([]);
          setLoading(false);
          return;
        }
        Promise.all(list.map((b) => getBooking(b.id)))
          .then((details) => {
            if (!cancelled) setBookings(details);
          })
          .catch((err) => {
            if (!cancelled) setError(err.message || 'Broneeringute laadimine ebaõnnestus');
          })
          .finally(() => {
            if (!cancelled) setLoading(false);
          });
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err.message || 'Broneeringuid ei saadud');
          setLoading(false);
        }
      });
    return () => { cancelled = true; };
  }, []);

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

      <main className="flex-1 py-8 px-4 max-w-2xl mx-auto w-full">
        <h1 className="font-display text-3xl text-restaurant-gold mb-2">Minu broneeringud</h1>
        <p className="text-stone-500 mb-6">Broneeringu info ja QR-kood – sama mis kinnitusmeilis</p>

        {loading && <p className="text-stone-400">Laen broneeringuid…</p>}
        {error && (
          <div className="mb-6 p-4 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
            {error}
          </div>
        )}

        {!loading && !error && bookings.length === 0 && (
          <p className="text-stone-500">Broneeringuid pole. Broneeri laud avalehelt.</p>
        )}

        {!loading && bookings.length > 0 && (
          <ul className="space-y-6">
            {bookings.map((b) => (
              <li
                key={b.id}
                className="rounded-2xl border border-restaurant-border bg-restaurant-card/80 p-5 shadow-lg"
              >
                <div className="flex flex-col sm:flex-row sm:items-start gap-4">
                  <div className="flex-1 space-y-1">
                    <p className="font-display text-restaurant-gold text-lg">
                      {formatDate(b.startAt)} · {formatTime(b.startAt)} – {formatTime(b.endAt)}
                    </p>
                    <p className="text-stone-300">
                      {b.guestName} · {b.partySize} inimest
                    </p>
                    <p className="text-stone-500 text-sm">{statusLabel(b.status)}</p>
                    {b.specialRequests && (
                      <p className="text-stone-500 text-sm mt-2">Märkused: {b.specialRequests}</p>
                    )}
                  </div>
                  {b.qrCodeImageBase64 && (
                    <div className="shrink-0 flex flex-col items-center">
                      <img
                        src={`data:image/png;base64,${b.qrCodeImageBase64}`}
                        alt="Broneeringu QR-kood"
                        className="w-28 h-28 object-contain bg-white rounded-lg border border-restaurant-border"
                      />
                      <span className="text-stone-500 text-xs mt-1">Näita restoranis</span>
                    </div>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </main>
    </div>
  );
}
