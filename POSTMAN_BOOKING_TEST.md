# Postmaniga broneeringu ja e-kirja testimine

Asenda `BASE_URL` oma rakenduse aadressiga (nt `http://localhost:8080`).

---

## 1. Registreeri või logi sisse (vaja tokenit)

### Variant A: Registreeri uus kasutaja  
**POST** `{{BASE_URL}}/api/v1/auth/register`

**Body (raw JSON):**
```json
{
  "name": "Test Tester",
  "email": "test@example.com",
  "password": "testpass123"
}
```

### Variant B: Logi sisse olemasoleva kasutajaga  
**POST** `{{BASE_URL}}/api/v1/auth/login`

**Body (raw JSON):**
```json
{
  "email": "test@example.com",
  "password": "testpass123"
}
```

**Vastusest võta `token`** ja kasuta seda järgmiste päringute puhul:
- **Headers:** `Authorization` = `Bearer <sinu_token>`

---

## 2. Broneeringu loomine (kinnitusmeil + PDF)

**POST** `{{BASE_URL}}/api/v1/bookings`

**Headers:**
- `Content-Type` = `application/json`
- `Authorization` = `Bearer <sinu_token>`

**Body (raw JSON)** – asenda `guestEmail` oma päris e-mailiga, et kinnitus tuleks sinuni:

```json
{
  "guestName": "Mari Maasikas",
  "guestEmail": "SINU_EMAIL@example.com",
  "startAt": "2025-03-15T18:00:00",
  "endAt": "2025-03-15T20:00:00",
  "partySize": 2,
  "status": "PENDING",
  "specialRequests": "Aknaäärne laud oleks tore",
  "bookingTables": [],
  "bookingPreferences": []
}
```

- **Ilma laudadeta:** `bookingTables: []` – broneering luuakse ja **e-kiri (+ PDF) saadetakse**; lauda ei valita.
- **Ühe lauaga:** kui sul on lauad andmebaasis, võta laua ID:  
  **GET** `{{BASE_URL}}/api/v1/table-entities` (või `/table-entities/available?zoneId=...&partySize=2&startAt=2025-03-15T18:00:00&endAt=2025-03-15T20:00:00`), võta ühe laua `id` ja lisa:

```json
"bookingTables": [
  { "tableEntityId": "SIIN_LAUA_UUID" }
]
```

**Õige aja formaat:** `yyyy-MM-ddTHH:mm:ss` (nt `2025-03-15T18:00:00`). `endAt` peab olema pärast `startAt`.

Kui meil on seadistatud (`MAIL_USERNAME` / `MAIL_PASSWORD` või `application-local.properties`), peaks kinnitus tulema aadressile `guestEmail` (tekst + manus `broneeringu-kinnitus.pdf`).
