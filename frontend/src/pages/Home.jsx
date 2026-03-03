import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import restaurantImg from '../assets/restaurant.png';

export default function Home() {
  const { userEmail, logout, isAdmin } = useAuth();

  const actions = [
    { to: '/menu', label: 'Menüü', sub: 'Praed ja joogid', icon: '◆' },
    { to: '/booking', label: 'Broneeri laud', sub: 'Vali aeg ja koht', icon: '◇' },
    { to: '/info', label: 'Restorani info', sub: 'Aadress ja kontakt', icon: '◈' },
  ];

  const adminActions = isAdmin
    ? [{ to: '/admin/table-drag', label: 'Laudade asukohad', sub: 'Floor plan', icon: '▣' }]
    : [];

  return (
    <div className="h-screen flex flex-col starfield overflow-hidden">
      <header className="shrink-0 border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md z-20">
        <div className="max-w-5xl mx-auto px-4 py-2.5 flex items-center justify-between">
          <span className="font-display text-restaurant-gold text-lg tracking-wide">CGI Restoran</span>
          <div className="flex items-center gap-3">
            <span className="text-stone-500 text-sm hidden sm:inline truncate max-w-[160px]">{userEmail}</span>
            <button
              type="button"
              onClick={logout}
              className="text-stone-400 hover:text-restaurant-gold text-sm transition"
            >
              Logi välja
            </button>
          </div>
        </div>
      </header>

      <section className="relative w-full shrink-0 overflow-hidden" style={{ height: '38vh', minHeight: '200px' }}>
        <img
          src={restaurantImg}
          alt="CGI Restoran – välisvaade"
          className="absolute inset-0 w-full h-full object-cover object-center"
        />
        <div
          className="absolute inset-0 bg-gradient-to-t from-restaurant-dark via-restaurant-dark/30 to-transparent"
          aria-hidden
        />
        <div className="absolute bottom-0 left-0 right-0 px-4 pb-4 md:px-6 md:pb-5 text-white">
          <h1 className="font-display text-2xl sm:text-3xl md:text-4xl text-restaurant-gold animate-fade-in-up">
            Tere tulemast
          </h1>
          <p className="mt-1 text-stone-300 text-base sm:text-lg animate-fade-in-up [animation-delay:100ms]">
            Maitseid kõigile
          </p>
        </div>
      </section>

      <main className="flex-1 min-h-0 flex flex-col justify-center px-4 py-4 -mt-1 relative z-10">
        <div className="max-w-3xl mx-auto w-full animate-fade-in [animation-delay:150ms]">
          <p className="font-display text-restaurant-gold/90 text-center text-lg sm:text-xl italic mb-6">
            Menüü, broneering või kontakt – vali oma tee.
          </p>

          <div className="grid grid-cols-3 gap-2 sm:gap-4">
            {actions.map(({ to, label, sub, icon }) => (
              <Link
                key={to}
                to={to}
                className="group flex flex-col items-center text-center p-4 sm:p-5 rounded-2xl border border-restaurant-border bg-restaurant-card/80 hover:bg-restaurant-gold/10 hover:border-restaurant-gold/40 transition duration-200"
              >
                <span className="text-restaurant-gold text-2xl sm:text-3xl mb-2 opacity-90 group-hover:opacity-100 group-hover:scale-110 transition">
                  {icon}
                </span>
                <span className="font-medium text-stone-200 text-sm sm:text-base">{label}</span>
                <span className="text-stone-500 text-xs mt-0.5">{sub}</span>
              </Link>
            ))}
          </div>
          {adminActions.length > 0 && (
            <div className="mt-4 pt-4 border-t border-restaurant-border/60">
              <p className="text-stone-500 text-center text-xs mb-3">Administraator</p>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2 sm:gap-4">
                {adminActions.map(({ to, label, sub, icon }) => (
                  <Link
                    key={to}
                    to={to}
                    className="group flex flex-col items-center text-center p-4 sm:p-5 rounded-2xl border border-restaurant-gold/30 bg-restaurant-card/80 hover:bg-restaurant-gold/10 hover:border-restaurant-gold/40 transition duration-200"
                  >
                    <span className="text-restaurant-gold text-2xl sm:text-3xl mb-2 opacity-90 group-hover:opacity-100 group-hover:scale-110 transition">
                      {icon}
                    </span>
                    <span className="font-medium text-stone-200 text-sm sm:text-base">{label}</span>
                    <span className="text-stone-500 text-xs mt-0.5">{sub}</span>
                  </Link>
                ))}
              </div>
            </div>
          )}

          <p className="text-stone-500 text-center text-xs mt-5 max-w-md mx-auto">
            Broneeri laud, vaata menüüd või leia meie aadress ja kontaktandmed.
          </p>
        </div>
      </main>
    </div>
  );
}
