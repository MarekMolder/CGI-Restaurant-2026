import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getStoredToken, setStoredToken } from '../api/client';

const AuthContext = createContext(null);

function syncFromStorage(setToken, setUserEmail) {
  const stored = getStoredToken();
  setToken(stored);
  if (stored) {
    try {
      const payload = JSON.parse(atob(stored.split('.')[1]));
      setUserEmail(payload.sub ?? null);
    } catch {
      setUserEmail(null);
    }
  } else {
    setUserEmail(null);
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getStoredToken);
  const [userEmail, setUserEmail] = useState(null);

  // Sync with localStorage on mount and when tab gets focus (e.g. logged in in another tab)
  useEffect(() => {
    syncFromStorage(setToken, setUserEmail);
    const onFocus = () => syncFromStorage(setToken, setUserEmail);
    window.addEventListener('focus', onFocus);
    return () => window.removeEventListener('focus', onFocus);
  }, []);

  useEffect(() => {
    const handleLogout = () => {
      setStoredToken(null);
      setToken(null);
      setUserEmail(null);
    };
    window.addEventListener('auth:logout', handleLogout);
    return () => window.removeEventListener('auth:logout', handleLogout);
  }, []);

  function setUserEmailFromToken(jwt) {
    try {
      const payload = JSON.parse(atob(jwt.split('.')[1]));
      setUserEmail(payload.sub ?? null);
    } catch {
      setUserEmail(null);
    }
  }

  const loginSuccess = useCallback((newToken) => {
    setStoredToken(newToken);
    setToken(newToken);
    setUserEmailFromToken(newToken);
  }, []);

  const logout = useCallback(() => {
    setStoredToken(null);
    setToken(null);
    setUserEmail(null);
  }, []);

  const value = {
    token,
    userEmail,
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
