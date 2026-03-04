# CGI Restoran – koodi loogika ja arhitektuur

*English version: [ARCHITECTURE-EN.md](ARCHITECTURE-EN.md)*

Lühike ülevaade domeeni mudelist (entity’d ja nende eesmärk) ning olulistest teenuste loogikatest (sh soovitussüsteem, broneeringu loomine, naaberlauad).

---

## 1. Domeeni entiteedid

### Restoran ja ruumiline struktuur

| Entity | Eesmärk                                                                                                                                                                                                                                                                                                                                                            |
|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Restaurant** | Restoran ise: nimi, aadress, kontakt, ajavöönd. Üks restoran; sellega on seotud seating plan ja menüü.                                                                                                                                                                                                                                                       |
| **SeatingPlan** | Üks põhiplaan (nt „Põhiplaan“, tüüp FLOOR_1). Sisaldab mõõtmeid (width, height), taustapilti (backgroundSVG) ja viitab **Zone**’idele ja **TableEntity**’dele. Võimaldab tulevikus mitut korrust või plaani.                                                                                                                                                       |
| **Zone** | Tsoon põhiplaani sees (nt Sisesaal, Terrass, Privaatruum). Igal tsoonil on nimi, tüüp (INDOOR / TERRACE / PRIVATE), värv ja võimalikud **Feature**’id (tsooni tasemel). Zone sisaldab laudu (**TableEntity**).                                                                                                                                                     |
| **TableEntity** | Füüsiline laud ühes tsoonis: nimetus (T1, T2), mahutavus, min party size, kuju (CIRCLE, OVAL), asukoht (x, y), suurus (width, height), pööre (rotationDegree), aktiivne. Viitab **Zone**’ile ja **SeatingPlan**’ile. Omab **Feature**’eid (laua tasemel: „akna all“, „vaikne“ jms) ja **naaberlauad** (adjacentTables) – kahe laua ühendamiseks broneeringus. |

### Funktsioonid ja eelistused

| Entity | Eesmärk |
|--------|--------|
| **Feature** | Üks „funktsioon“ (nt akna all, vaikne nurk, ligipääsetav, baari lähedal). Kasutatakse kahel viisil: **Zone** võib omada feature’eid (üldine); **TableEntity** omab oma feature’eid – need määravad, kas laud sobib klienti sooviga (soovitusskoor). |
| **BookingPreference** | Broneeringu eelistus: link **Booking** → **Feature** + prioriteet (HIGH jms). Klient valib broneerimisel „soovin akna all“ – see salvestub BookingPreference’ina ja kasutatakse laua soovitamisel. |

### Broneering ja kasutaja

| Entity | Eesmärk                                                                                                                                                                                                      |
|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **User** | Rakenduse kasutaja: nimi, e-mail, parooli räsi, roll (CUSTOMER / ADMIN). Broneeringud on seotud useriga (kes broneeris).                                                                                     |
| **Booking** | Üks broneering: külalise nimi ja e-mail, algus- ja lõppaeg, osalejate arv, staatus, lisasoovid (specialRequests). Viitab **User**’ile; omab **BookingPreference**’eid, **BookingTable**’eid ja **QrCode**’e. |
| **BookingTable** | Seos „broneering – laud“. Üks broneering võib hõlmata üht või mitut lauda (kombineeritud laud); iga seos on üks BookingTable.                                                                                |
| **QrCode** | Broneeringule genereeritud QR-kood (väärtus nt base64 pildina). Saadetakse kinnitusmeiliga; klient näeb „Minu broneeringutes“.                                                                               |

### Menüü

| Entity | Eesmärk |
|--------|--------|
| **MenuItem** | Üks menüüpunkt (roog või jook): nimi, kirjeldus, hind, kategooria. Kuulub **Restaurant**’ile. |

---

## 2. Olulised teenused ja loogika

### TableEntityService – saadaval olevad lauad ja soovitussüsteem

**Meetod:** `findTablesWithAvailability(zoneId, partySize, startAt, endAt, preferredFeatureIds)`.

- **Sisend:** tsoon (või seatingPlanId), osalejate arv, ajavahemik, klienti eelistused (feature ID-d).
- **Loogika:**
  1. Kontrollitakse **lahtiolekuaegu** (`RestaurantHoursService.isWithinOpeningHours`) – kui aeg on väljaspool, tagastatakse tühi list.
  2. Võetakse kõik aktiivsed lauad tsoonis (**TableEntity** + naaberlauad).
  3. Määratakse **broneeritud lauad** selles ajavahemikus (`BookingTable`-põhine päring).
  4. **Üksikud lauad:** filtreeritakse mahutavuse järgi (capacity ≥ partySize, minPartySize ≤ partySize) ja tühjade kohtade piir (nt max 2 tühja kohta – `MAX_EMPTY_SEATS`). Iga vaba laua jaoks arvutatakse **soovitusskoor**:
     - alus: `100 - (capacity - partySize)`;
     - **feature bonus:** kui klient andis eelistused (`preferredFeatureIds`) ja laual on vastav feature (nt akna all), siis iga vastav feature +20 punkti (`FEATURE_MATCH_BONUS`).
  5. **Kombineeritud lauad:** iga naaberlaudade paar (adjacentTables), kus mõlemad on vabad ja koondmahutavus vastab partySize’ile (sh MAX_EMPTY_SEATS), lisatakse variandina; skoor = alus + mõlema laua feature bonus.
  6. Tulemused **sorditakse:** esmalt vabad, seejärel skoori järgi (kõrgeim esimesena). Frontend saab nii üksikud kui kombineeritud variandid koos soovitusskooriga (nt kollasega „parim sobivus“).

### TableEntityService – naaberlauad (adjacency)

**Meetod:** `validateTablesAdjacent(tableIds)`.

- Kui broneeringul on **rohkem kui üks laud**, peavad need olema **naaberlauad** (ühendatud graafina). Naabrused on **TableEntity.adjacentTables** (many-to-many).
- Loogika: laaditakse kõik antud ID-dega lauad koos adjacentTables’iga; BFS/DFS-ga kontrollitakse, kas kõik antud lauad on ühendatud naabrusgraafis. Kui mitte, visatakse `RestaurantBookingException` („Chosen tables must be nearby to each other.“).

### BookingService – broneeringu loomine

**Meetod:** `createBooking(CreateBookingRequest)`.

- **Kontrollid:**
  1. Aeg peab jääma **lahtiolekuaegadesse** (`RestaurantHoursService.isWithinOpeningHours`).
  2. Kestus peab olema **täpselt konfigureeritud broneeringu pikkus** (nt 2 tundi).
  3. Kui on valitud lauad: **mitme laua puhul** kutsutakse `validateTablesAdjacent`; seejärel kontrollitakse, et **ühtegi valitud lauda pole selles ajavahemikus juba broneeritud**.
- **Tegevused:** luuakse **Booking**, seejärel **BookingPreference**’id ja **BookingTable**’id; genereeritakse **QrCode** ja saadetakse **kinnitusmeil** (`EmailService.sendBookingConfirmation`).

### RestaurantHoursService

- **isWithinOpeningHours(start, end)** – kas ajavahemik on nädalapäeva või nädalavahetuse lahtiolekuaegade piires (konfiguratsioonist).
- **getBookingDurationHours()** – broneeringu pikkus tundides (nt 2).
- **getOpeningHours()** – frontendile lahtiolekuajad ja bookingDurationHours (aja slottide genereerimiseks).

### QrCodeService / EmailService

- **QrCodeService:** genereerib broneeringu jaoks QR-koodi (väärtus salvestatakse QrCode.value’sse; frontend kuvab base64 pildina).
- **EmailService:** saadab broneeringu kinnituse meilile (sh vajadusel QR). Kasutab `application-local.properties` meili seadeid (Gmail jms).

---

## 3. Lühike voodi ülevaade

1. **Broneerimise voog:** Klient valib kuupäeva, aja, tsooni, osalejate arvu, eelistused → frontend küsib `GET /table-entities/available` (zoneId, partySize, startAt, endAt, preferredFeatureIds) → saab lauad soovitusskooridega → valib laua(d) ja sisestab nime/lisasoovid → `POST /bookings` → backend kontrollib ajad ja lauad, loob Booking + BookingTable + BookingPreference, genereerib QR, saadab meili.
2. **Põhiplaani redaktor (admin):** Laaditakse lauad tsooni järgi (`GET /table-entities?zoneId=...`); asukoht/suurus/pööre uuendatakse `PATCH .../position` ja `PATCH .../layout` kaudu.
3. **Laudade haldus (admin):** CRUD table-entities; iga laud seostatakse tsooniga (zoneId) ja feature’idega (featureIds).

Kõik entity’d ja nimetatud teenused on olemas backendis; see dokument selgitab nende **eesmärki** ja **loogikat**, mitte iga meetodi signatuuri. Täpsemaks API kasutamiseks vaata [API.md](API-EST.md).
