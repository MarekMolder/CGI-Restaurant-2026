import { apiRequest } from './client';

/**
 * Returns the first restaurant from the list (for single-restaurant app).
 * Backend: GET /api/v1/restaurants (public).
 */
export async function getFirstRestaurant() {
  const res = await apiRequest('/api/v1/restaurants?size=1');
  if (!res.ok) throw new Error('Restorani andmeid ei saadud');
  const page = await res.json();
  const list = page?.content ?? [];
  if (list.length === 0) return null;
  return list[0];
}

/**
 * Returns one restaurant by ID. Backend: GET /api/v1/restaurants/{id} (public).
 */
export async function getRestaurantById(id) {
  const res = await apiRequest(`/api/v1/restaurants/${id}`);
  if (!res.ok) {
    if (res.status === 404) return null;
    throw new Error('Restorani andmeid ei saadud');
  }
  return res.json();
}
