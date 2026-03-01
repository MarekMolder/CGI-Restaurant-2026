import { apiRequest, setStoredToken } from './client';

const AUTH_BASE = '/api/v1/auth';

/**
 * Login with email and password. Returns { token, expiresIn } or throws.
 */
export async function login(email, password) {
  const res = await apiRequest(`${AUTH_BASE}/login`, {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Login failed');
  }
  const data = await res.json();
  setStoredToken(data.token);
  return data;
}

/**
 * Register and log in. Returns { token, expiresIn } or throws.
 */
export async function register(name, email, password) {
  const res = await apiRequest(`${AUTH_BASE}/register`, {
    method: 'POST',
    body: JSON.stringify({ name, email, password }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Register failed');
  }
  const data = await res.json();
  setStoredToken(data.token);
  return data;
}
