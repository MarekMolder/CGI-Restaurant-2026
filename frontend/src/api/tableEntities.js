import { apiRequest } from './client';

export async function listByZone(zoneId) {
  const res = await apiRequest(`/api/v1/table-entities?zoneId=${encodeURIComponent(zoneId)}`);
  if (!res.ok) throw new Error('Laudu ei saadud');
  return res.json();
}

/**
 * Get available table options for a slot (single or combined tables).
 * Backend: GET /api/v1/table-entities/available
 * startAt, endAt: ISO strings e.g. "2025-03-15T14:00:00"
 */
export async function getAvailableTables(zoneId, partySize, startAt, endAt, preferredFeatureIds = []) {
  const params = new URLSearchParams({
    zoneId,
    partySize: String(partySize),
    startAt,
    endAt,
  });
  preferredFeatureIds.forEach((id) => params.append('preferredFeatureIds', id));
  const res = await apiRequest(`/api/v1/table-entities/available?${params.toString()}`);
  if (!res.ok) throw new Error('Vabu laudu ei saadud');
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

/** Update table layout (position, size, rotation). Admin only. */
export async function updateLayout(id, x, y, width, height, rotationDegree) {
  const res = await apiRequest(`/api/v1/table-entities/${id}/layout`, {
    method: 'PATCH',
    body: JSON.stringify({ x, y, width, height, rotationDegree }),
  });
  if (!res.ok) throw new Error('Layouti uuendamine ebaõnnestus');
  return res.json();
}
