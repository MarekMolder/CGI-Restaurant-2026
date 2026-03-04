# CGI Restoran – Setup

*English version: [SETUP-EN.md](SETUP-EN.md)*

Selle dokumendi järgi saad projekti kohalikult käivitada

## Eeldused

- **Java 17+** (backend)
- **Node.js 18+** ja **npm** (frontend)
- **Docker** (PostgreSQL)

## 1. Andmebaas (PostgreSQL)

Käivita andmebaas Dockeriga:

```bash
docker compose up -d
```

PostgreSQL töötab pordil **5432**; parool on `password` (vt `docker-compose.yml`). 
Vaikeseaded on juba `application.properties`-is.

## 2. Backend (Spring Boot)

### Ilma kinnitusmeilideta (vaikeprofiil)

```bash
./mvnw spring-boot:run
```

- Andmebaas täitub algandmetega (DataSeeder), kui restorane veel pole (sõltub `cgi.seed.enabled` väärtusest).
- Vaikimisi `cgi.seed.enabled=true` – esimesel käivitamisel tekivad tsoonid, lauad, menüü, demo-kasutajad jms. 
- `src/main/resources/application.properties`

### Kinnitusmeilid Gmailiga (local profiil)

Et saade broneeringute kinnitus meilile, pead kasutama **local** profiili ja sisestama meili andmed kohalikus konfiguratsioonis.

1. **Loo kohalik konfiguratsioon** (fail on `.gitignore`-is, repositooriumi ei committita):

   Fail: `src/main/resources/application-local.properties`

   Näidis (asenda oma Gmaili andmetega; Gmaili puhul kasuta [rakenduse parooli](https://support.google.com/accounts/answer/185833)):

   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=sinu.email@gmail.com
   spring.mail.password=rakenduse-parool
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

2. **Käivita backend local profiiliga:**

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

   **Profiilide tööpõhimõte:** Spring Boot laeb alati `application.properties` (andmebaas, JWT, DataSeeder jms). Profiiliga `local` laetakse *lisaks* fail `application-local.properties` – see **lisab** ainult seal olevad võtmed (meili seaded). 
## 3. DataSeeder (algandmed)

- **Sisse lülitatud:** `cgi.seed.enabled=true` (vaikimisi `application.properties`-is).
- Seeder töötab ainult siis, kui andmebaasis pole ühtegi restorani; see loob restorani, tsoonid, lauad, menüü, demo-kasutajad (sh admin) ja võimalikud broneeringud.
- Kui soovid seedi välja lülitada, sea `cgi.seed.enabled=false`.

## 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

Rakendus avaneb tavaliselt aadressil **http://localhost:5173**. Vite proxy suunab `/api` päringud backendile (localhost:8080).

## 5. Register + Login

```bash
Registreeri uus kasutaja: Enda email + suvaline password
(et saada broneeringu info meilile)
```

Logimiseks saad kasutada:
1) Enda uut loodud kontot (roll on CUSTOMER)
2) Kasutada süsteemi kontot (Username: admin@test.com, Password: password) (Roll on ADMIN)

## 5. Tests

```bash
./mvnw test
open target/site/jacoco/index.html
```
Nende commandidega saab jooksutada kõik projekti koodid + 
lisaks vaadata testide kattuvust.

## Kokkuvõte – täielik käivitamine (sh Gmail)

1. `docker compose up -d`
2. Loo `src/main/resources/application-local.properties` meili andmetega (vt ülal).
3. `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
4. `cd frontend && npm install && npm run dev`

Pärast seda on saadaval broneering, menüü, broneeringute kinnitusmeilid ja admin funktsioonid (kui logid sisse demo adminiga).
