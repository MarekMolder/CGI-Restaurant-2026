import { apiRequest } from './client';

export async function listByZone(zoneId) {
  const res = await apiRequest(`/api/v1/table-entities?zoneId=${encodeURIComponent(zoneId)}`);
  if (!res.ok) throw new Error('Laudu ei saadud');
  return res.json();
}

export async function updatePosition(id, x, y) {
  const res = await apiRequest(`/api/v1/table-entities/${id}/position`, {
    method: 'PATCH',
    body: JSON.stringify({ x, y }),
  });
  if (!res.ok) throw new Error('Positsiooni uuendamine ebaõnnestus');
  return res.json();
}
