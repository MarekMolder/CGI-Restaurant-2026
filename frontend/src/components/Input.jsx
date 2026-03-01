/**
 * Text input with label and optional error. Supports type="email", "password", "text".
 */
export default function Input({
  label,
  name,
  type = 'text',
  value,
  onChange,
  error,
  placeholder,
  autoComplete,
  required = false,
  className = '',
  ...props
}) {
  const id = props.id ?? name;
  return (
    <div className={className}>
      {label && (
        <label htmlFor={id} className="block text-sm font-medium text-stone-400 mb-1">
          {label}
          {required && <span className="text-restaurant-gold ml-0.5">*</span>}
        </label>
      )}
      <input
        id={id}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete={autoComplete}
        required={required}
        className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 placeholder-stone-500 focus:outline-none focus:ring-2 focus:ring-restaurant-gold focus:border-transparent"
        {...props}
      />
      {error && <p className="mt-1 text-sm text-amber-400">{error}</p>}
    </div>
  );
}
