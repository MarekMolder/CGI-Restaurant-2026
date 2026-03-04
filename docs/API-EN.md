# CGI Restaurant – API documentation

*Eesti keelne versioon: [API.md](API-EST.md)*

REST API overview. Base URL: `http://localhost:8080` (or your server).

## Authentication

Most endpoints (including bookings) require a **JWT** token. Add the header:

```
Authorization: Bearer <token>
```

**Public** (no token): `POST /api/v1/auth/login`, `POST /api/v1/auth/register`, all `GET /api/v1/**`.

**Admin** (role `ADMIN`): create/update/delete for tables, zones, restaurants, menu, features; PATCH table-entities.

---

## 1. Authentication (`/api/v1/auth`)

### POST `/api/v1/auth/login`

Login. Returns a JWT.

**Body (JSON):**
```json
{
  "email": "user@example.com",
  "password": "password"
}
```

**Response 200:** `{ "token": "<JWT>", "expiresIn": 86400 }`

---

### POST `/api/v1/auth/register`

Register. Returns a JWT.

**Body (JSON):**
```json
{
  "email": "user@example.com",
  "password": "password",
  "name": "Name"
}
```

**Response 201:** `{ "token": "<JWT>", "expiresIn": 86400 }`  
**409:** email already exists.

---

## 2. Bookings (`/api/v1/bookings`)

All require **JWT**. Customers see only their own bookings; admins see all.

### POST `/api/v1/bookings`

Creates a booking (for the logged-in user). Response may include QR code (base64).

**Body (JSON):**
```json
{
  "guestName": "Guest name",
  "guestEmail": "guest@example.com",
  "startAt": "2025-03-15T14:00:00",
  "endAt": "2025-03-15T16:00:00",
  "partySize": 4,
  "status": "CONFIRMED",
  "specialRequests": "Optional special requests (max 2000 chars)",
  "bookingPreferences": [
    { "featureId": "<UUID>", "priority": "HIGH" }
  ],
  "bookingTables": [
    { "tableEntityId": "<UUID>" }
  ]
}
```

**Response 201:** Booking (including `qrCodeImageBase64` if applicable).  
**400:** invalid time range, tables not free or rules not satisfied.

---

### GET `/api/v1/bookings`

List bookings (paginated). Admin: all; customer: own only.

**Query:** `page`, `size`, `sort` (Pageable).

**Response 200:** `{ "content": [ ... ], "totalElements": N, ... }`

---

### GET `/api/v1/bookings/{bookingId}`

Single booking by ID. Customers can only access their own bookings.

**Response 200:** Booking (including `qrCodeImageBase64`).  
**404:** booking not found or no access.

---

### PUT `/api/v1/bookings/{bookingId}`

Update booking. Customers can only update their own.

**Body (JSON):** Same fields as create (partial updates allowed).

**Response 200:** Updated booking.

---

### DELETE `/api/v1/bookings/{bookingId}`

Delete booking. Customers can only delete their own.

**Response 204:** No content.

---

## 3. Tables – table entities (`/api/v1/table-entities`)

### GET `/api/v1/table-entities`

List tables.

**Query:**

- **zoneId** (UUID, optional) – tables in one zone; response is a list (not a page).
- **q** (string, optional) – search by label; returns a page.
- **page**, **size**, **sort** – when neither zoneId nor q is set, response is a page (Page).

**Response 200:** Either `List<ListTableEntityResponseDto>` (when zoneId) or `Page<ListTableEntityResponseDto>`. Each element includes e.g. `id`, `label`, `capacity`, `minPartySize`, `shape`, `x`, `y`, `width`, `height`, `rotationDegree`, `active`, `zoneId`, `featureIds`.

---

### GET `/api/v1/table-entities/{id}`

Single table by ID.

**Response 200:** Table (details, including `zoneId`, `featureIds`).  
**404:** table not found.

---

### GET `/api/v1/table-entities/available`

Available tables for a given time range and party size. Used on the booking page.

**Query (required):**

- **zoneId** (UUID) or **seatingPlanId** (UUID) – at least one.
- **partySize** (int)
- **startAt** (ISO 8601, e.g. `2025-03-15T14:00:00`)
- **endAt** (ISO 8601)
- **preferredFeatureIds** (optional, repeatable) – preferred feature IDs (affects recommendation score).

**Response 200:** `List<TableAvailabilityItemDto>` – each element includes e.g. `tableIds`, `combined`, `label`, `capacity`, `zoneId`, `zoneName`, `zoneType`, `available`, `recommendationScore`, `x`, `y`, `width`, `height`, `rotationDegree`.

**400:** if neither zoneId nor seatingPlanId is provided.

---

### POST `/api/v1/table-entities`

**Admin.** Create a new table.

**Body (JSON):**
```json
{
  "label": "T1",
  "capacity": 4,
  "minPartySize": 2,
  "shape": "RECT",
  "x": 100, "y": 100, "width": 80, "height": 70,
  "rotationDegree": 0,
  "active": true,
  "zoneId": "<UUID>",
  "featureIds": ["<UUID>", ...],
  "adjacentTableIds": []
}
```

**shape:** `RECT` | `CIRCLE` | `OVAL`

**Response 201:** Created table.

---

### PUT `/api/v1/table-entities/{id}`

**Admin.** Full update of table (including zoneId, featureIds, dimensions).

**Body (JSON):** Same structure as create; may include `id`.

**Response 200:** Updated table.

---

### PATCH `/api/v1/table-entities/{id}/position`

**Admin.** Position only (x, y) – for floor plan drag.

**Body (JSON):** `{ "x": 150, "y": 200 }`

**Response 200:** Updated table.

---

### PATCH `/api/v1/table-entities/{id}/layout`

**Admin.** Position, size and rotation – for floor plan editor.

**Body (JSON):** `{ "x": 150, "y": 200, "width": 90, "height": 80, "rotationDegree": 45 }`

**Response 200:** Updated table.

---

### DELETE `/api/v1/table-entities/{id}`

**Admin.** Delete table.

**Response 204:** No content.

---

## 4. Zones (`/api/v1/zones`)

### GET `/api/v1/zones`

List zones (paginated). **Public.**

**Query:** `page`, `size`, `sort`.

**Response 200:** `Page<ListZoneResponseDto>` (e.g. `id`, `name`, `type`, `color`).

---

### GET `/api/v1/zones/{id}`

Single zone by ID. **Public.**

**Response 200:** Zone (details). **404:** zone not found.

---

*POST / PUT / DELETE zones – admin only; used in management.*

---

## 5. Features (`/api/v1/features`)

### GET `/api/v1/features`

List features (e.g. “By window”, “Quiet corner”). **Public.** Used for booking preferences and table features.

**Query:** `page`, `size`, `sort`.

**Response 200:** `Page<ListFeatureResponseDto>` (e.g. `id`, `name`, `code`).

---

*POST / PUT / DELETE features – admin only.*

---

## 6. Restaurant opening hours (`/api/v1/restaurant-hours`)

### GET `/api/v1/restaurant-hours`

Opening hours and booking slot duration. **Public.**

**Response 200:**
```json
{
  "weekdayOpen": "10:00",
  "weekdayClose": "18:00",
  "weekendOpen": "10:00",
  "weekendClose": "22:00",
  "bookingDurationHours": 2
}
```

Times in `HH:mm` format; frontend uses these to compute available slots.

---

## 7. Menu (`/api/v1/restaurants/{restaurantId}/menu`)

### GET `/api/v1/restaurants/{restaurantId}/menu`

Restaurant menu (list of items). **Public.**

**Response 200:** `List<MenuItemResponseDto>` (e.g. `id`, `name`, `description`, `priceEur`, `category`).

---

*POST / PUT / DELETE menu – admin only.*

---

## 8. Other resources (brief)

- **Restaurants** – `GET /api/v1/restaurants`, `GET /api/v1/restaurants/{id}` (public).
- **Seating plans** – `GET /api/v1/seating-plans`, `GET /api/v1/seating-plans/{id}` (public).
- **Booking preferences** – booking preferences (e.g. feature + priority); tied to booking.
- **Booking tables** – booking–table links; managed via booking.
- **TheMealDB** – `GET /api/v1/themealdb/categories`, `/meals`, `/meals/{id}` (external API proxy).

All **GET** requests to `/api/v1/**` are public. For write operations (POST/PUT/PATCH/DELETE): auth endpoints are public; bookings require a logged-in user; tables, zones, restaurants, menu, features – admin.

---

## Error codes

- **400** – invalid request (e.g. validation, rule violation).
- **401** – missing or invalid token.
- **403** – no permission (e.g. customer calling admin endpoint).
- **404** – resource not found (e.g. invalid ID).
- **409** – conflict (e.g. email already exists on register).

Errors are typically returned as JSON (e.g. `ApiErrorResponse`: `message`, `timestamp`).
