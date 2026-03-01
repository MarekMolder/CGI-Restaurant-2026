import { apiRequest } from './client';

/**
 * List features. Backend: GET /api/v1/features (public).
 */
export async function listFeatures() {
  const res = await apiRequest('/api/v1/features?size=100');
  if (!res.ok) throw new Error('Filtreid ei saadud');
  const page = await res.json();
  return page?.content ?? [];
}
