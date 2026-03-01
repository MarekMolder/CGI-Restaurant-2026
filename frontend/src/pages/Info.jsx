import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getFirstRestaurant } from '../api/restaurants';

function mapsSearchUrl(address) {
  return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`;
}

function mapsEmbedUrl(address) {
  return (
    import.meta.env.VITE_GOOGLE_MAPS_EMBED_URL ||
    `https://www.google.com/maps?q=${encodeURIComponent(address)}&output=embed`
  );
}

function mapsDirectionsUrl(address) {
  return `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(address)}`;
}

export default function Info() {
  const [restaurant, setRestaurant] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    getFirstRestaurant()
      .then((data) => {
        if (!cancelled) {
          setRestaurant(data);
          setError(null);
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err.message || 'Andmete laadimine ebaõnnestus');
          setRestaurant(null);
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen starfield flex flex-col">
        <header className="border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md shrink-0">
          <div className="max-w-3xl mx-auto px-4 py-3">
            <Link to="/" className="text-restaurant-gold hover:text-restaurant-goldLight transition font-display">
              ← Avaleht
            </Link>
          </div>
        </header>
        <main className="flex-1 flex items-center justify-center px-4">
          <p className="text-stone-500">Laen restorani infot…</p>
        </main>
      </div>
    );
  }

  if (error || !restaurant) {
    return (
      <div className="min-h-screen starfield flex flex-col">
        <header className="border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md shrink-0">
          <div className="max-w-3xl mx-auto px-4 py-3">
            <Link to="/" className="text-restaurant-gold hover:text-restaurant-goldLight transition font-display">
              ← Avaleht
            </Link>
          </div>
        </header>
        <main className="flex-1 flex items-center justify-center px-4">
          <p className="text-amber-400">{error || 'Restorani infot ei leitud.'}</p>
        </main>
      </div>
    );
  }

  const address = restaurant.address || '';
  const phone = restaurant.phone || '';
  const email = restaurant.email || '';

  return (
    <div className="min-h-screen starfield">
      <header className="border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md sticky top-0 z-10">
        <div className="max-w-3xl mx-auto px-4 py-3">
          <Link
            to="/"
            className="text-restaurant-gold hover:text-restaurant-goldLight transition font-display inline-flex items-center gap-2"
          >
            ← Avaleht
          </Link>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 py-8 pb-12">
        <h1 className="font-display text-2xl sm:text-3xl text-restaurant-gold mb-2">
          Restorani info
        </h1>
        <p className="text-stone-500 text-sm mb-8">{restaurant.name}</p>

        <div className="grid gap-6 sm:grid-cols-2 mb-10">
          <a
            href={address ? mapsDirectionsUrl(address) : '#'}
            target="_blank"
            rel="noopener noreferrer"
            className="flex gap-4 items-start p-4 rounded-xl border border-restaurant-border bg-restaurant-card/80 hover:bg-restaurant-gold/10 hover:border-restaurant-gold/40 transition"
          >
            <span className="text-restaurant-gold text-2xl" aria-hidden>📍</span>
            <div className="min-w-0">
              <p className="text-stone-500 text-xs uppercase tracking-wider mb-1">Aadress</p>
              <p className="text-stone-200 font-medium">{address || '—'}</p>
              {address && (
                <span className="text-restaurant-gold text-sm mt-1 inline-block">Vaata kaardil →</span>
              )}
            </div>
          </a>

          <a
            href={phone ? `tel:${phone.replace(/\s/g, '')}` : '#'}
            className="flex gap-4 items-start p-4 rounded-xl border border-restaurant-border bg-restaurant-card/80 hover:bg-restaurant-gold/10 hover:border-restaurant-gold/40 transition"
          >
            <span className="text-restaurant-gold text-2xl" aria-hidden>📞</span>
            <div>
              <p className="text-stone-500 text-xs uppercase tracking-wider mb-1">Telefon</p>
              <p className="text-stone-200 font-medium">{phone || '—'}</p>
              {phone && (
                <span className="text-restaurant-gold text-sm mt-1 inline-block">Helista →</span>
              )}
            </div>
          </a>

          <a
            href={email ? `mailto:${email}` : '#'}
            className="flex gap-4 items-start p-4 rounded-xl border border-restaurant-border bg-restaurant-card/80 hover:bg-restaurant-gold/10 hover:border-restaurant-gold/40 transition sm:col-span-2"
          >
            <span className="text-restaurant-gold text-2xl" aria-hidden>✉️</span>
            <div>
              <p className="text-stone-500 text-xs uppercase tracking-wider mb-1">E-mail</p>
              <p className="text-stone-200 font-medium">{email || '—'}</p>
              {email && (
                <span className="text-restaurant-gold text-sm mt-1 inline-block">Saada e-kiri →</span>
              )}
            </div>
          </a>
        </div>

        {address && (
          <>
            <section className="rounded-2xl overflow-hidden border border-restaurant-border bg-restaurant-card/50">
              <div className="flex items-center justify-between px-4 py-3 border-b border-restaurant-border">
                <h2 className="font-display text-restaurant-gold text-lg">Asukoht</h2>
                <a
                  href={mapsSearchUrl(address)}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-sm text-restaurant-gold hover:text-restaurant-goldLight transition"
                >
                  Ava Google Mapsis →
                </a>
              </div>
              <div className="relative w-full aspect-[16/10] sm:aspect-[2/1] bg-restaurant-border">
                <iframe
                  title={`${restaurant.name} Google Mapsis`}
                  src={mapsEmbedUrl(address)}
                  width="100%"
                  height="100%"
                  style={{ border: 0 }}
                  allowFullScreen
                  loading="lazy"
                  referrerPolicy="no-referrer-when-downgrade"
                  className="absolute inset-0 w-full h-full"
                />
              </div>
            </section>

            <p className="text-stone-500 text-xs mt-4 text-center">
              Kaart: Google Maps. Kui kaart ei ilmu,{' '}
              <a
                href={mapsSearchUrl(address)}
                target="_blank"
                rel="noopener noreferrer"
                className="text-restaurant-gold hover:underline"
              >
                ava asukoht uues aknas
              </a>.
            </p>
          </>
        )}
      </main>
    </div>
  );
}
