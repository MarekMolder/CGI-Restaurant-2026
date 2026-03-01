/**
 * Primary and secondary buttons. Use variant="secondary" for outline style.
 */
export default function Button({
  children,
  type = 'button',
  variant = 'primary',
  disabled = false,
  className = '',
  ...props
}) {
  const base =
    'inline-flex items-center justify-center font-semibold rounded-lg transition focus:outline-none focus:ring-2 focus:ring-restaurant-gold focus:ring-offset-2 focus:ring-offset-restaurant-dark disabled:opacity-50 disabled:pointer-events-none';
  const variants = {
    primary:
      'bg-restaurant-gold text-restaurant-dark hover:bg-restaurant-goldLight px-5 py-2.5',
    secondary:
      'border border-restaurant-gold text-restaurant-gold hover:bg-restaurant-gold/10 px-5 py-2.5',
    ghost:
      'text-stone-300 hover:bg-white/5 hover:text-stone-200 px-4 py-2',
  };
  return (
    <button
      type={type}
      disabled={disabled}
      className={`${base} ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
