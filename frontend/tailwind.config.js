/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        restaurant: {
          gold: '#c9a227',
          goldLight: '#e5c76b',
          dark: '#0d0d0d',
          card: '#1a1a1a',
          border: '#2a2a2a',
        },
      },
      fontFamily: {
        display: ['Playfair Display', 'serif'],
        body: ['Source Sans 3', 'sans-serif'],
        menu: ['Roboto', 'sans-serif'],
      },
      backgroundImage: {
        'stars': 'radial-gradient(1.5px 1.5px at 20px 30px, rgba(255,255,255,0.4), transparent), radial-gradient(1.5px 1.5px at 40px 70px, rgba(255,255,255,0.25), transparent), radial-gradient(1px 1px at 90px 40px, rgba(255,255,255,0.35), transparent)',
      },
      animation: {
        'fade-in': 'fadeIn 0.6s ease-out forwards',
        'fade-in-up': 'fadeInUp 0.5s ease-out forwards',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(12px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
      },
    },
  },
  plugins: [],
};
