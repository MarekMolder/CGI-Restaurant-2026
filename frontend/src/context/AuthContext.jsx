import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getStoredToken, setStoredToken } from '../api/client';

const AuthContext = createContext(null);

function parseJwtPayload(token) {
  if (!token) return { sub: null, role: null };
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return {
      sub: payload.sub ?? null,
      role: payload.role ?? null,
    };
  } catch {
    return { sub: null, role: null };
  }
}

function syncFromStorage(setToken, setUserEmail, setUserRole) {
  const stored = getStoredToken();
  setToken(stored);
  if (stored) {
    const { sub, role } = parseJwtPayload(stored);
    setUserEmail(sub);
    setUserRole(role);
  } else {
    setUserEmail(null);
    setUserRole(null);
  }
}

export function AuthProvider({ children }) {
  const initialToken = getStoredToken();
  const initialPayload = parseJwtPayload(initialToken);
  const [token, setToken] = useState(initialToken);
  const [userEmail, setUserEmail] = useState(initialPayload.sub);
  const [userRole, setUserRole] = useState(initialPayload.role);

  useEffect(() => {
    syncFromStorage(setToken, setUserEmail, setUserRole);
    const onFocus = () => syncFromStorage(setToken, setUserEmail, setUserRole);
    window.addEventListener('focus', onFocus);
    return () => window.removeEventListener('focus', onFocus);
  }, []);

  useEffect(() => {
    const handleLogout = () => {
      setStoredToken(null);
      setToken(null);
      setUserEmail(null);
      setUserRole(null);
    };
    window.addEventListener('auth:logout', handleLogout);
    return () => window.removeEventListener('auth:logout', handleLogout);
  }, []);

  function setUserFromToken(jwt) {
    const { sub, role } = parseJwtPayload(jwt);
    setUserEmail(sub);
    setUserRole(role);
  }

  const loginSuccess = useCallback((newToken) => {
    setStoredToken(newToken);
    setToken(newToken);
    setUserFromToken(newToken);
  }, []);

  const logout = useCallback(() => {
    setStoredToken(null);
    setToken(null);
    setUserEmail(null);
    setUserRole(null);
  }, []);

  const value = {
    token,
    userEmail,
    userRole,
    isAdmin: userRole === 'ADMIN',
    loginSuccess,
    logout,
    isAuthenticated: !!token,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
