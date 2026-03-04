# CGI Restaurant – Code logic and architecture

*Eesti keelne versioon: [ARCHITECTURE.md](ARCHITECTURE-EST.md)*

Short overview of the domain model (entities and their purpose) and key service logic (including the recommendation system, booking creation, and adjacent tables).

---

## 1. Domain entities

### Restaurant and spatial structure

| Entity | Purpose |
|--------|--------|
| **Restaurant** | The restaurant itself: name, address, contact, timezone. One restaurant; associated with seating plan and menu. |
| **SeatingPlan** | A single floor plan (e.g. “Main plan”, type FLOOR_1). Contains dimensions (width, height), background image (backgroundSVG) and references **Zone**s and **TableEntity**s. Allows for multiple floors or plans in the future. |
| **Zone** | A zone within the floor plan (e.g. Indoor, Terrace, Private room). Each zone has a name, type (INDOOR / TERRACE / PRIVATE), color and optional **Feature**s (at zone level). A zone contains tables (**TableEntity**). |
| **TableEntity** | A physical table in a zone: label (T1, T2), capacity, min party size, shape (CIRCLE, OVAL, RECT), position (x, y), size (width, height), rotation (rotationDegree), active. References **Zone** and **SeatingPlan**. Has **Feature**s (at table level: “by window”, “quiet” etc.) and **adjacent tables** (adjacentTables) for combining two tables in a booking. |

### Features and preferences

| Entity | Purpose |
|--------|--------|
| **Feature** | A “feature” (e.g. by window, quiet corner, accessible, near bar). Used in two ways: **Zone** can have features (general); **TableEntity** has its own features – these determine whether the table matches customer preferences (recommendation score). |
| **BookingPreference** | Booking preference: link **Booking** → **Feature** + priority (HIGH etc.). Customer selects e.g. “by window” when booking – stored as BookingPreference and used for table recommendation. |

### Booking and user

| Entity | Purpose |
|--------|--------|
| **User** | Application user: name, email, password hash, role (CUSTOMER / ADMIN). Bookings are linked to the user (who made the booking). |
| **Booking** | A single booking: guest name and email, start and end time, party size, status, special requests. References **User**; has **BookingPreference**s, **BookingTable**s and **QrCode**s. |
| **BookingTable** | Link “booking – table”. One booking can cover one or more tables (combined table); each link is one BookingTable. |
| **QrCode** | QR code generated for the booking (value e.g. as base64 image). Sent with confirmation email; customer sees it under “My bookings”. |

### Menu

| Entity | Purpose |
|--------|--------|
| **MenuItem** | A menu item (dish or drink): name, description, price, category. Belongs to **Restaurant**. |

---

## 2. Key services and logic

### TableEntityService – available tables and recommendation system

**Method:** `findTablesWithAvailability(zoneId, partySize, startAt, endAt, preferredFeatureIds)`.

- **Input:** zone (or seatingPlanId), party size, time range, customer preferences (feature IDs).
- **Logic:**
  1. Check **opening hours** (`RestaurantHoursService.isWithinOpeningHours`) – if the time is outside, return an empty list.
  2. Load all active tables in the zone (**TableEntity** + adjacent tables).
  3. Determine **booked tables** in that time range (query based on **BookingTable**).
  4. **Single tables:** filter by capacity (capacity ≥ partySize, minPartySize ≤ partySize) and limit on empty seats (e.g. max 2 – `MAX_EMPTY_SEATS`). For each free table, compute **recommendation score**:
     - base: `100 - (capacity - partySize)`;
     - **feature bonus:** if the customer gave preferences (`preferredFeatureIds`) and the table has a matching feature (e.g. by window), add +20 points per match (`FEATURE_MATCH_BONUS`).
  5. **Combined tables:** for each pair of adjacent tables (adjacentTables) where both are free and combined capacity matches partySize (including MAX_EMPTY_SEATS), add as an option; score = base + feature bonus for both tables.
  6. **Sort** results: free first, then by score (highest first). The frontend gets both single and combined options with recommendation score (e.g. “best match” in yellow).

### TableEntityService – adjacent tables (adjacency)

**Method:** `validateTablesAdjacent(tableIds)`.

- If a booking has **more than one table**, they must be **adjacent** (connected in a graph). Adjacency is stored in **TableEntity.adjacentTables** (many-to-many).
- Logic: load all tables with the given IDs including adjacentTables; use BFS/DFS to check that all given tables are connected in the adjacency graph. If not, throw `RestaurantBookingException` (“Chosen tables must be nearby to each other.”).

### BookingService – creating a booking

**Method:** `createBooking(CreateBookingRequest)`.

- **Checks:**
  1. Time must fall within **opening hours** (`RestaurantHoursService.isWithinOpeningHours`).
  2. Duration must be **exactly** the configured booking length (e.g. 2 hours).
  3. If tables are selected: for **multiple tables** call `validateTablesAdjacent`; then ensure **none of the selected tables are already booked** in that time range.
- **Actions:** create **Booking**, then **BookingPreference**s and **BookingTable**s; generate **QrCode** and send **confirmation email** (`EmailService.sendBookingConfirmation`).

### RestaurantHoursService

- **isWithinOpeningHours(start, end)** – whether the time range falls within weekday or weekend opening hours (from configuration).
- **getBookingDurationHours()** – booking duration in hours (e.g. 2).
- **getOpeningHours()** – opening hours and bookingDurationHours for the frontend (for generating time slots).

### QrCodeService / EmailService

- **QrCodeService:** generates a QR code for the booking (value stored in QrCode.value; frontend displays as base64 image).
- **EmailService:** sends booking confirmation by email (including QR if needed). Uses mail settings from `application-local.properties` (Gmail etc.).

---

## 3. Brief flow overview

1. **Booking flow:** Customer chooses date, time, zone, party size, preferences → frontend calls `GET /table-entities/available` (zoneId, partySize, startAt, endAt, preferredFeatureIds) → gets tables with recommendation scores → selects table(s) and enters name/special requests → `POST /bookings` → backend validates time and tables, creates Booking + BookingTable + BookingPreference, generates QR, sends email.
2. **Floor plan editor (admin):** Tables loaded by zone (`GET /table-entities?zoneId=...`); position/size/rotation updated via `PATCH .../position` and `PATCH .../layout`.
3. **Table management (admin):** CRUD for table-entities; each table is linked to a zone (zoneId) and features (featureIds).

All entities and the services mentioned exist in the backend; this document explains their **purpose** and **logic**, not every method signature. For API usage details see [API.md](API-EST.md).
