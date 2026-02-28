# Feature: Lahtiolekuajad, 2h broneering, laudade kokkulükkamine ja menüü API

## Ülevaade
See PR lisab restorani lahtiolekuaja API, kinnitab 2-tunnise broneeringu reegli, toetab laudade kokkulükkamist (kõrvuti lauad ühe broneeringu jaoks) ja lisab restorani menüü API koos TheMealDB integratsiooniga.

---

## 1. Restorani lahtiolekuajad
- **Konfiguratsioon:** `application.properties` – `restaurant.hours.weekday.open/close`, `restaurant.hours.weekend.open/close` (nädalavahetus = laupäev, pühapäev).
- **Loogika:** `RestaurantHoursService` / `RestaurantHoursServiceImpl` – `isWithinOpeningHours(start, end)`, `getBookingDurationHours()`, getterid tundide kohta.
- **Broneering:** Broneeringu loomisel kontrollitakse, et `startAt` ja `endAt` jäävad lahtiolekuajade sisse; vastasel korral `RestaurantBookingException`: *"Booking time must fall within the restaurant's opening hours."*
- **Volitused:** Lahtiolekuaja seadeid saab lugeda (kui on lisatud endpoint); broneeringu API muutumata.

---

## 2. Broneeringu kestus 2 tundi
- **Konfiguratsioon:** `restaurant.booking.duration-hours=2` (`application.properties`).
- **Loogika:** `BookingServiceImpl.createBooking()` – `Duration.between(startAt, endAt)` peab olema täpselt 2 tundi.
- **Viga:** Kui kestus erineb → `RestaurantBookingException`: *"The booking duration must be exactly 2 hours."*

---

## 3. Laudade kokkulükkamine (dünaamiline laudade liitmine)
- **Andmemudel:** `TableEntity` – `@ManyToMany adjacentTables`; lauad võib seostada teiste laudadega (`adjacentTableIds` create/update DTO-des).
- **API:**  
  - `POST/PUT /api/v1/table-entities` – võimaldab määrata `adjacentTableIds` (admin).  
  - `GET /api/v1/table-entities/available?seatingPlanId|zoneId&partySize&startAt&endAt&preferredFeatureIds` – tagastab nii üksikud lauad kui ka **kombineeritud** võimalused (`combined: true`, `tableIds: [id1, id2]`), kui kaks kõrvuti olevat lauda kokku sobivad seltskonna suuruse ja tühjade kohtade piiridega.
- **Broneering:** Broneeringul võib olla mitu lauda (`bookingTables`). `TableEntityService.validateTablesAdjacent(tableIds)` kontrollib, et kõik valitud lauad on üksteisega kõrvuti; vastasel korral `RestaurantBookingException`: *"Chosen tables must be nearby to each other."*
- **Soovituse loogika:** Üksikute laudade ja “kokku lükatud” paaride jaoks arvutatakse `recommendationScore` (mahutavus + eelistuste boonus); tulemused sorteeritakse (saadaval olevad ees, siis skoori järgi).

---

## 4. Menüü API ja TheMealDB
- **Menüü:**  
  - `MenuItem` entity (restaurant, name, description, priceEur, category, imageUrl, themealdbId).  
  - **GET** `/api/v1/restaurants/{restaurantId}/menu` – avalik, restorani menüü koos hindadega.  
  - **POST** `/api/v1/restaurants/{restaurantId}/menu` – admin, uue menüüpunkti loomine.  
  - **PUT** `/api/v1/restaurants/{restaurantId}/menu/{menuItemId}` – admin, uuendamine.  
  - **DELETE** `/api/v1/restaurants/{restaurantId}/menu/{menuItemId}` – admin, kustutamine.
- **TheMealDB:**  
  - `RestTemplateConfig`, `TheMealDBService` / `TheMealDBServiceImpl` – kutsed `categories.php`, `filter.php`, `lookup.php`; `importMealAsMenuItem(restaurant, mealId, priceEur)`.  
  - **GET** `/api/v1/themealdb/categories` – kategooriad.  
  - **GET** `/api/v1/themealdb/meals?category=...` – retseptid kategooria järgi.  
  - **GET** `/api/v1/themealdb/meals/{id}` – ühe retsepti detailid.  
  - **POST** `/api/v1/restaurants/{restaurantId}/menu/from-themealdb` – admin, lisamine menüüsse (keha: `mealId`, `priceEur`).
- **Turvalisus:** `SecurityConfig` – GET menüü ja themealdb avalikud; POST/PUT/DELETE menüü jaoks nõutakse ADMIN.
- **Vead:** `GlobalExceptionHandler` – `MenuItemNotFoundException` → 404.

---

## Muud muudatused
- **SecurityConfig:** Reeglid menüü endpointide jaoks (POST/PUT/DELETE `.../restaurants/*/menu`, `.../menu/**` → ADMIN).
- **GlobalExceptionHandler:** Handler `MenuItemNotFoundException` jaoks.
- **application.properties:** Lahtiolekuaja ja broneeringu kestuse parameetrid (kui need on failis).

---

## Testimine
- Postman: `POSTMAN_TESTID.md` (menüü, lahtiolekuajad, 2h broneering, laudade kokkulükkamine).
- Broneering: `POSTMAN_BOOKING_TEST.md`.

---

## Checklist
- [x] Lahtiolekuajad konfis ja broneeringu kontrollis
- [x] 2h broneeringu kestus
- [x] Laudade külgnevus ja kokkulükkamine (available + broneering)
- [x] Menüü CRUD ja avalik GET
- [x] TheMealDB integratsioon ja “add to menu”
- [x] Turvalisus ja veakäsitlus
