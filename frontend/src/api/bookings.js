import { apiRequest } from './client';

/**
 * Create a booking. Backend: POST /api/v1/bookings
 */
export async function createBooking(body) {
  const res = await apiRequest('/api/v1/bookings', {
    method: 'POST',
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Broneering ebaõnnestus');
  }
  return res.json();
}

/**
 * List current user's bookings. Backend: GET /api/v1/bookings
 * Returns { content: [...], totalElements, ... } (page).
 */
export async function listMyBookings(page = 0, size = 50) {
  const res = await apiRequest(`/api/v1/bookings?page=${page}&size=${size}`);
  if (!res.ok) throw new Error('Broneeringuid ei saadud');
  return res.json();
}

/**
 * Get one booking by ID (with QR code). Backend: GET /api/v1/bookings/:id
 */
export async function getBooking(id) {
  const res = await apiRequest(`/api/v1/bookings/${id}`);
  if (!res.ok) throw new Error('Broneeringut ei leitud');
  return res.json();
}
