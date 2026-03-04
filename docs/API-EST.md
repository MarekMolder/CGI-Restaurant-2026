# CGI Restoran – API dokumentatsioon

*English version: [API-EN.md](API-EN.md)*

REST API ülevaade. Base URL: `http://localhost:8080` (või vastav server).

## Autentimine

Enamik endpoint’eid (sh broneeringud) nõuab **JWT**-tokenit. Päringute puhul lisa header:

```
Authorization: Bearer <token>
```

**Avalikud** (ilma tokenita): `POST /api/v1/auth/login`, `POST /api/v1/auth/register`, kõik `GET /api/v1/**`.

**Admin** (roll `ADMIN`): tabelite, tsoonide, restoranide, menüü, feature’ite loomine/uuendamine/kustutamine; PATCH table-entities.

---

## 1. Autentimine (`/api/v1/auth`)

### POST `/api/v1/auth/login`

Sisselogimine. Tagastab JWT.

**Body (JSON):**
```json
{
  "email": "kasutaja@example.com",
  "password": "parool"
}
```

**Vastus 200:** `{ "token": "<JWT>", "expiresIn": 86400 }`

---

### POST `/api/v1/auth/register`

Registreerimine. Tagastab JWT.

**Body (JSON):**
```json
{
  "email": "kasutaja@example.com",
  "password": "parool",
  "name": "Nimi"
}
```

**Vastus 201:** `{ "token": "<JWT>", "expiresIn": 86400 }`  
**409:** e-mail juba olemas.

---

## 2. Broneeringud (`/api/v1/bookings`)

Kõik nõuavad **JWT**-t. Klient näeb ainult oma broneeringuid; admin näeb kõiki.

### POST `/api/v1/bookings`

Loob broneeringu (sisselogitud kasutaja jaoks). Vastuses võib olla QR-kood (base64).

**Body (JSON):**
```json
{
  "guestName": "Külalise nimi",
  "guestEmail": "kylaline@example.com",
  "startAt": "2025-03-15T14:00:00",
  "endAt": "2025-03-15T16:00:00",
  "partySize": 4,
  "status": "CONFIRMED",
  "specialRequests": "Lisasoovid (valikuline, max 2000 tähemärki)",
  "bookingPreferences": [
    { "featureId": "<UUID>", "priority": "HIGH" }
  ],
  "bookingTables": [
    { "tableEntityId": "<UUID>" }
  ]
}
```

**Vastus 201:** Broneering (sh `qrCodeImageBase64` vajadusel).  
**400:** vale ajavahemik, lauad pole vabad või reeglid ei täitu.

---

### GET `/api/v1/bookings`

Nimekiri broneeringutest (leheküljega). Admin: kõik; klient: ainult oma.

**Query:** `page`, `size`, `sort` (Pageable).

**Vastus 200:** `{ "content": [ ... ], "totalElements": N, ... }`

---

### GET `/api/v1/bookings/{bookingId}`

Üks broneering ID järgi. Klient saab ainult oma broneeringuid.

**Vastus 200:** Broneering (sh `qrCodeImageBase64`).  
**404:** broneeringut pole või puudub õigus.

---

### PUT `/api/v1/bookings/{bookingId}`

Broneeringu uuendamine. Klient saab uuendada ainult oma broneeringuid.

**Body (JSON):** Samad väljad mis create’il (vajadusel osaliselt).

**Vastus 200:** Uuendatud broneering.

---

### DELETE `/api/v1/bookings/{bookingId}`

Broneeringu kustutamine. Klient saab kustutada ainult oma broneeringuid.

**Vastus 204:** No content.

---

## 3. Lauad – table entities (`/api/v1/table-entities`)

### GET `/api/v1/table-entities`

Lauade nimekiri.

**Query:**

- **zoneId** (UUID, valikuline) – lauad ühes tsoonis; vastus on list (mitte lehekülg).
- **q** (string, valikuline) – otsing nimetuse järgi; tagastab leheküljega.
- **page**, **size**, **sort** – kui pole `zoneId` ega `q`, tagastatakse leheküljega (Page).

**Vastus 200:** Kas `List<ListTableEntityResponseDto>` (zoneId puhul) või `Page<ListTableEntityResponseDto>`. Iga element sisaldab nt `id`, `label`, `capacity`, `minPartySize`, `shape`, `x`, `y`, `width`, `height`, `rotationDegree`, `active`, `zoneId`, `featureIds`.

---

### GET `/api/v1/table-entities/{id}`

Üks laud ID järgi.

**Vastus 200:** Laud (detailid, sh `zoneId`, `featureIds`).  
**404:** laud puudub.

---

### GET `/api/v1/table-entities/available`

Vabad lauad antud ajavahemikus ja osalejate arvu jaoks. Kasutatakse broneerimise lehel.

**Query (kohustuslikud):**

- **zoneId** (UUID) või **seatingPlanId** (UUID) – vähemalt üks neist.
- **partySize** (int)
- **startAt** (ISO 8601, nt `2025-03-15T14:00:00`)
- **endAt** (ISO 8601)
- **preferredFeatureIds** (valikuline, korduv) – eelistatud feature’ite ID-d (soovituse skoor).

**Vastus 200:** `List<TableAvailabilityItemDto>` – iga element sisaldab nt `tableIds`, `combined`, `label`, `capacity`, `zoneId`, `zoneName`, `zoneType`, `available`, `recommendationScore`, `x`, `y`, `width`, `height`, `rotationDegree`.

**400:** kui pole antud ei `zoneId` ega `seatingPlanId`.

---

### POST `/api/v1/table-entities`

**Admin.** Uue laua loomine.

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

**Vastus 201:** Loodud laud.

---

### PUT `/api/v1/table-entities/{id}`

**Admin.** Laua täielik uuendamine (sh zoneId, featureIds, mõõtmed).

**Body (JSON):** Sama struktuur mis create’il; võib sisaldada ka `id`.

**Vastus 200:** Uuendatud laud.

---

### PATCH `/api/v1/table-entities/{id}/position`

**Admin.** Ainult asukoht (x, y) – põhiplaani lohistamiseks.

**Body (JSON):** `{ "x": 150, "y": 200 }`

**Vastus 200:** Uuendatud laud.

---

### PATCH `/api/v1/table-entities/{id}/layout`

**Admin.** Asukoht, suurus ja pööre – põhiplaani redaktor.

**Body (JSON):** `{ "x": 150, "y": 200, "width": 90, "height": 80, "rotationDegree": 45 }`

**Vastus 200:** Uuendatud laud.

---

### DELETE `/api/v1/table-entities/{id}`

**Admin.** Laua kustutamine.

**Vastus 204:** No content.

---

## 4. Tsoonid (`/api/v1/zones`)

### GET `/api/v1/zones`

Tsoonide nimekiri (leheküljega). **Avalik.**

**Query:** `page`, `size`, `sort`.

**Vastus 200:** `Page<ListZoneResponseDto>` (nt `id`, `name`, `type`, `color`).

---

### GET `/api/v1/zones/{id}`

Üks tsoon ID järgi. **Avalik.**

**Vastus 200:** Tsoon (detailid). **404:** tsooni pole.

---

*POST / PUT / DELETE zones – admin only; kasutatakse halduses.*

---

## 5. Funktsioonid – features (`/api/v1/features`)

### GET `/api/v1/features`

Funktsioonide nimekiri (nt „Akna all“, „Vaikne nurk“). **Avalik.** Kasutatakse broneerimise eelistuste ja laudade funktsioonide valimiseks.

**Query:** `page`, `size`, `sort`.

**Vastus 200:** `Page<ListFeatureResponseDto>` (nt `id`, `name`, `code`).

---

*POST / PUT / DELETE features – admin only.*

---

## 6. Restorani lahtiolekuajad (`/api/v1/restaurant-hours`)

### GET `/api/v1/restaurant-hours`

Lahtiolekuajad ja broneeringu slotti pikkus. **Avalik.**

**Vastus 200:**
```json
{
  "weekdayOpen": "10:00",
  "weekdayClose": "18:00",
  "weekendOpen": "10:00",
  "weekendClose": "22:00",
  "bookingDurationHours": 2
}
```

Ajad vormis `HH:mm`; frontend kasutab neid saadaval olevate aegade arvutamiseks.

---

## 7. Menüü (`/api/v1/restaurants/{restaurantId}/menu`)

### GET `/api/v1/restaurants/{restaurantId}/menu`

Restorani menüü (menüüpunktide nimekiri). **Avalik.**

**Vastus 200:** `List<MenuItemResponseDto>` (nt `id`, `name`, `description`, `priceEur`, `category`).

---

*POST / PUT / DELETE menu – admin only.*

---

## 8. Muud ressursid (lühidalt)

- **Restaurants** – `GET /api/v1/restaurants`, `GET /api/v1/restaurants/{id}` (avalikud).
- **Seating plans** – `GET /api/v1/seating-plans`, `GET /api/v1/seating-plans/{id}` (avalikud).
- **Booking preferences** – broneeringu eelistused (nt feature + priority); seotud broneeringuga.
- **Booking tables** – broneeringu ja laua seosed; hallatakse broneeringu kaudu.
- **TheMealDB** – `GET /api/v1/themealdb/categories`, `/meals`, `/meals/{id}` (välise API vahendamine).

Kõik **GET**-päringud `/api/v1/**` on avalikud. Kirjutamise (POST/PUT/PATCH/DELETE) puhul kehtib: auth endpoint’id avalikud; broneeringud nõuavad sisselogitud kasutajat; tabelid, tsoonid, restoranid, menüü, feature’id – admin.

---

## Veakoodid

- **400** – vale päring (nt validatsioon, reeglite rikkumine).
- **401** – puudub või kehtetu token.
- **403** – puudub õigus (nt klient proovib admin endpoint’i).
- **404** – ressursi pole (nt vale ID).
- **409** – konflikt (nt e-mail juba olemas registreerimisel).

Vead tagastatakse tavaliselt JSON-kujul (nt `ApiErrorResponse`: `message`, `timestamp`).
