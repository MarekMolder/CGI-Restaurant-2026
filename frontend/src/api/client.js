const STORAGE_KEY = 'cgi_restaurant_token';

export function getStoredToken() {
  return localStorage.getItem(STORAGE_KEY);
}

export function setStoredToken(token) {
  if (token) localStorage.setItem(STORAGE_KEY, token);
  else localStorage.removeItem(STORAGE_KEY);
}

/** Returns true if token exists and is not expired (with 60s buffer). */
export function isTokenValid(token) {
  if (!token) return false;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp;
    if (!exp) return true;
    return Date.now() / 1000 < exp - 60;
  } catch {
    return false;
  }
}

/** Returns decoded JWT payload (subject, role, exp, ...) or null. */
export function getTokenPayload(token) {
  if (!token) return null;
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch {
    return null;
  }
}

/** Returns true if current user has ADMIN role (from JWT). */
export function isAdmin() {
  const token = getStoredToken();
  if (!token || !isTokenValid(token)) return false;
  const payload = getTokenPayload(token);
  return payload?.role === 'ADMIN';
}

/**
 * Fetch wrapper that adds JWT from localStorage to requests.
 * Use for all authenticated API calls.
 * Clears token if expired or on 401 so UI stays in sync.
 */
export async function apiRequest(path, options = {}) {
  let token = getStoredToken();
  if (token && !isTokenValid(token)) {
    setStoredToken(null);
    window.dispatchEvent(new Event('auth:logout'));
    token = null;
  }
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  if (token) headers.Authorization = `Bearer ${token}`;

  const base = import.meta.env.VITE_API_URL ?? '';
  const res = await fetch(`${base}${path}`, { ...options, headers });

  if (res.status === 401) {
    setStoredToken(null);
    window.dispatchEvent(new Event('auth:logout'));
  }

  return res;
}
