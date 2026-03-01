/**
 * Card container for forms and content. Restaurant-style dark with border.
 */
export default function Card({ title, children, className = '' }) {
  return (
    <div
      className={`bg-restaurant-card border border-restaurant-border rounded-xl shadow-xl overflow-hidden ${className}`}
    >
      {title && (
        <div className="px-6 py-4 border-b border-restaurant-border">
          <h2 className="font-display text-xl text-restaurant-gold">{title}</h2>
        </div>
      )}
      <div className="p-6">{children}</div>
    </div>
  );
}
