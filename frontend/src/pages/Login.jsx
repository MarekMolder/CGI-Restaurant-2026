import { useState } from 'react';
import { Link, useNavigate, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login as apiLogin } from '../api/auth';
import Button from '../components/Button';
import Input from '../components/Input';
import Card from '../components/Card';

export default function Login() {
  const navigate = useNavigate();
  const { token, loginSuccess } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (token) return <Navigate to="/" replace />;

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await apiLogin(email, password);
      loginSuccess(data.token);
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.message || 'Sisselogimine ebaõnnestus');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen starfield flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="font-display text-3xl text-restaurant-gold mb-1">Tere tulemast</h1>
          <p className="text-stone-500">Logi sisse</p>
        </div>

        <Card>
          <form onSubmit={handleSubmit} className="space-y-5">
            {error && (
              <div className="p-3 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
                {error}
              </div>
            )}
            <Input
              label="E-mail"
              name="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="teie@email.ee"
              autoComplete="email"
              required
            />
            <Input
              label="Parool"
              name="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="current-password"
              required
            />
            <Button type="submit" disabled={loading} className="w-full">
              {loading ? 'Logime sisse…' : 'Logi sisse'}
            </Button>
          </form>
        </Card>

        <p className="mt-6 text-center text-stone-500 text-sm">
          Pole veel kontot?{' '}
          <Link to="/register" className="text-restaurant-gold hover:text-restaurant-goldLight transition">
            Registreeri
          </Link>
        </p>
      </div>
    </div>
  );
}
