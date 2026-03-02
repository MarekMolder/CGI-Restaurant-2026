import { apiRequest } from './client';

/**
 * Lahtiolekuaegade ja broneerimise kestuse andmed. Backend: GET /api/v1/restaurant-hours.
 * @returns {Promise<{ weekdayOpen: string, weekdayClose: string, weekendOpen: string, weekendClose: string, bookingDurationHours: number }>}
 */
export async function getOpeningHours() {
  const res = await apiRequest('/api/v1/restaurant-hours');
  if (!res.ok) throw new Error('Lahtiolekuaegu ei saadud');
  return res.json();
}
