import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getFirstRestaurant } from '../api/restaurants';
import { getMenuByRestaurantId } from '../api/menu';

function formatPrice(priceEur) {
  if (priceEur == null) return '—';
  return new Intl.NumberFormat('et-EE', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2,
  }).format(priceEur);
}

function groupByCategory(items) {
  const byCategory = {};
  for (const item of items) {
    const cat = item.category || 'Muud';
    if (!byCategory[cat]) byCategory[cat] = [];
    byCategory[cat].push(item);
  }
  return Object.entries(byCategory).sort(([a], [b]) => a.localeCompare(b));
}

function truncate(str, maxLen = 120) {
  if (!str) return '';
  if (str.length <= maxLen) return str;
  return str.slice(0, maxLen).trim() + '…';
}

const ALL_KEY = '__all__';

export default function Menu() {
  const [restaurant, setRestaurant] = useState(null);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(ALL_KEY);

  useEffect(() => {
    let cancelled = false;
    getFirstRestaurant()
      .then((rest) => {
        if (cancelled || !rest) return;
        setRestaurant(rest);
        return getMenuByRestaurantId(rest.id);
      })
      .then((menuItems) => {
        if (!cancelled && menuItems) setItems(menuItems);
      })
      .catch((err) => {
        if (!cancelled) setError(err.message || 'Menüü laadimine ebaõnnestus');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, []);

  const grouped = groupByCategory(items);
  const categories = [ALL_KEY, ...grouped.map(([cat]) => cat)];
  const displayItems =
    selectedCategory === ALL_KEY
      ? items
      : (grouped.find(([c]) => c === selectedCategory)?.[1] ?? []);

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
          <p className="text-stone-500">Laen menüüd…</p>
        </main>
      </div>
    );
  }

  if (error) {
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
          <p className="text-amber-400">{error}</p>
        </main>
      </div>
    );
  }

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

      <main className="max-w-3xl mx-auto px-4 py-6 pb-12">
        <h1 className="font-display text-2xl text-restaurant-gold mb-1">Menüü</h1>
        {restaurant && (
          <p className="text-stone-500 text-sm mb-6 font-menu">{restaurant.name}</p>
        )}

        {grouped.length === 0 ? (
          <p className="text-stone-500 font-menu">Menüüs pole veel ühtegi toodet.</p>
        ) : (
          <>
            {/* Category tabs – Gmail-style, Roboto; selected = gold + line, others = gray */}
            <nav
              className="font-menu border-b border-restaurant-border mb-6 -mx-4 overflow-x-auto flex justify-center"
              aria-label="Kategooriad"
            >
              <div className="flex gap-0 min-w-max px-4">
                {categories.map((cat) => {
                  const label = cat === ALL_KEY ? 'Kõik' : cat;
                  const isSelected = selectedCategory === cat;
                  return (
                    <button
                      key={cat}
                      type="button"
                      onClick={() => setSelectedCategory(cat)}
                      className={`relative px-4 py-3 text-sm font-medium whitespace-nowrap transition ${
                        isSelected
                          ? 'text-restaurant-gold'
                          : 'text-stone-500 hover:text-stone-300'
                      }`}
                    >
                      {label}
                      {isSelected && (
                        <span
                          className="absolute bottom-0 left-0 right-0 h-0.5 bg-restaurant-gold"
                          aria-hidden
                        />
                      )}
                    </button>
                  );
                })}
              </div>
            </nav>

            {/* Items for selected category */}
            <ul className="space-y-4">
              {displayItems.map((item) => (
                <li
                  key={item.id}
                  className="flex gap-4 p-4 rounded-xl border border-restaurant-border bg-restaurant-card/60 hover:border-restaurant-gold/30 transition font-menu"
                >
                  {item.imageUrl && (
                    <img
                      src={item.imageUrl}
                      alt=""
                      className="w-24 h-24 rounded-lg object-cover shrink-0"
                    />
                  )}
                  <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-baseline justify-between gap-2">
                      <h3 className="font-medium text-stone-200">{item.name}</h3>
                      <span className="text-restaurant-gold font-medium shrink-0">
                        {formatPrice(item.priceEur)}
                      </span>
                    </div>
                    {item.description && (
                      <p className="text-stone-500 text-sm mt-1 leading-relaxed">
                        {truncate(item.description)}
                      </p>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          </>
        )}
      </main>
    </div>
  );
}
