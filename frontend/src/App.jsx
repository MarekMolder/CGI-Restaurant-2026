import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { getStoredToken } from './api/client';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import Menu from './pages/Menu';
import Booking from './pages/Booking';
import Info from './pages/Info';
import MyBookings from './pages/MyBookings';
import TableDrag from './pages/TableDrag';

function ProtectedRoute({ children }) {
  const { token } = useAuth();
  const storedToken = getStoredToken();
  if (!storedToken && !token) return <Navigate to="/login" replace />;
  return children;
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
      <Route path="/menu" element={<ProtectedRoute><Menu /></ProtectedRoute>} />
      <Route path="/booking" element={<ProtectedRoute><Booking /></ProtectedRoute>} />
      <Route path="/info" element={<ProtectedRoute><Info /></ProtectedRoute>} />
      <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
      <Route path="/admin/table-drag" element={<ProtectedRoute><TableDrag /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
