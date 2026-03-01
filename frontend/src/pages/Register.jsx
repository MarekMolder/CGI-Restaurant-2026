import { useState } from 'react';
import { Link, useNavigate, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { register as apiRegister } from '../api/auth';
import Button from '../components/Button';
import Input from '../components/Input';
import Card from '../components/Card';

const MIN_PASSWORD_LENGTH = 6;

export default function Register() {
  const navigate = useNavigate();
  const { token, loginSuccess } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (token) return <Navigate to="/" replace />;

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    if (password.length < MIN_PASSWORD_LENGTH) {
      setError(`Parool peab olema vähemalt ${MIN_PASSWORD_LENGTH} tähemärki`);
      return;
    }
    if (password !== confirmPassword) {
      setError('Paroolid ei ühti');
      return;
    }
    setLoading(true);
    try {
      const data = await apiRegister(name, email, password);
      loginSuccess(data.token);
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.message || 'Registreerimine ebaõnnestus');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen starfield flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="font-display text-3xl text-restaurant-gold mb-1">Loo konto</h1>
          <p className="text-stone-500">Loo konto</p>
        </div>

        <Card>
          <form onSubmit={handleSubmit} className="space-y-5">
            {error && (
              <div className="p-3 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
                {error}
              </div>
            )}
            <Input
              label="Nimi"
              name="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Juhan Tamm"
              autoComplete="name"
              required
            />
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
              placeholder="Vähemalt 6 tähemärki"
              autoComplete="new-password"
              required
            />
            <Input
              label="Korda parooli"
              name="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="new-password"
              required
            />
            <Button type="submit" disabled={loading} className="w-full">
              {loading ? 'Loome kontot…' : 'Registreeri'}
            </Button>
          </form>
        </Card>

        <p className="mt-6 text-center text-stone-500 text-sm">
          Juba konto olemas?{' '}
          <Link to="/login" className="text-restaurant-gold hover:text-restaurant-goldLight transition">
            Logi sisse
          </Link>
        </p>
      </div>
    </div>
  );
}
