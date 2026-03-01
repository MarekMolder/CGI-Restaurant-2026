import { apiRequest } from './client';

/**
 * Returns menu items for a restaurant. Backend: GET /api/v1/restaurants/{restaurantId}/menu (public).
 * Each item: { id, name, description, priceEur, category, imageUrl }.
 */
export async function getMenuByRestaurantId(restaurantId) {
  const res = await apiRequest(`/api/v1/restaurants/${restaurantId}/menu`);
  if (!res.ok) {
    if (res.status === 404) return [];
    throw new Error('Menüü andmeid ei saadud');
  }
  return res.json();
}
