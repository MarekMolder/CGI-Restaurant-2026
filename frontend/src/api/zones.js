import { apiRequest } from './client';

/**
 * List zones. Backend: GET /api/v1/zones (public).
 */
export async function listZones() {
  const res = await apiRequest('/api/v1/zones?size=100');
  if (!res.ok) throw new Error('Tsoone ei saadud');
  const page = await res.json();
  return page?.content ?? [];
}
