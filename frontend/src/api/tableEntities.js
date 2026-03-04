import { apiRequest } from './client';

/** List all tables (paged). For admin settings. */
export async function listAll(page = 0, size = 100) {
  const res = await apiRequest(`/api/v1/table-entities?page=${page}&size=${size}`);
  if (!res.ok) throw new Error('Laudu ei saadud');
  return res.json();
}

/** Get single table by id. */
export async function getById(id) {
  const res = await apiRequest(`/api/v1/table-entities/${id}`);
  if (!res.ok) throw new Error('Lauda ei leitud');
  return res.json();
}

export async function listByZone(zoneId) {
  const res = await apiRequest(`/api/v1/table-entities?zoneId=${encodeURIComponent(zoneId)}`);
  if (!res.ok) throw new Error('Laudu ei saadud');
  return res.json();
}

/** Create table. Body: label, capacity, minPartySize, shape, x, y, width, height, rotationDegree, active, zoneId, featureIds?, adjacentTableIds? */
export async function createTable(body) {
  const res = await apiRequest('/api/v1/table-entities', {
    method: 'POST',
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Laua loomine ebaõnnestus');
  }
  return res.json();
}

/** Update table. Body: same as create + id. */
export async function updateTable(id, body) {
  const res = await apiRequest(`/api/v1/table-entities/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Laua uuendamine ebaõnnestus');
  }
  return res.json();
}

/** Delete table. */
export async function deleteTable(id) {
  const res = await apiRequest(`/api/v1/table-entities/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Laua kustutamine ebaõnnestus');
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
