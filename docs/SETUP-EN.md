# CGI Restaurant – Setup

*Eesti keelne versioon: [SETUP.md](SETUP-EST.md)*

This document describes how to run the project locally.

## Setup Video
- Click on the picture and then opens youtube link.

[![Setup Video](https://img.youtube.com/vi/vVNMMTfCWwI/0.jpg)](https://www.youtube.com/watch?v=vVNMMTfCWwI)

## Prerequisites

- **Java 17+** (backend)
- **Node.js 18+** and **npm** (frontend)
- **Docker** (PostgreSQL)

## 1. Database (PostgreSQL)

Start the database with Docker:

```bash
docker compose up -d
```

PostgreSQL runs on port **5432**; password is `password` (see `docker-compose.yml`). Default settings are in `application.properties`.

## 2. Backend (Spring Boot)

### Without confirmation emails (default profile)

```bash
./mvnw spring-boot:run
```

- The database is seeded with initial data (DataSeeder) when no restaurant exists yet (depends on `cgi.seed.enabled`).
- Default is `cgi.seed.enabled=true` – on first run, zones, tables, menu, demo users etc. are created.
- See `src/main/resources/application.properties`.

### Confirmation emails with Gmail (local profile)

To send booking confirmation emails, use the **local** profile and provide mail settings in a local config file.

1. **Create local config** (file is in `.gitignore`, not committed):

   File: `src/main/resources/application-local.properties`

   Example (replace with your Gmail details; for Gmail use an [app password](https://support.google.com/accounts/answer/185833)):

   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your.email@gmail.com
   spring.mail.password=app-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

2. **Run backend with local profile:**

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

   **How profiles work:** Spring Boot always loads `application.properties` (database, JWT, DataSeeder, etc.). With the `local` profile, `application-local.properties` is loaded *in addition* – it only **adds or overrides** the keys defined there (mail settings).

## 3. DataSeeder (initial data)

- **Enabled:** `cgi.seed.enabled=true` (default in `application.properties`).
- The seeder runs only when no restaurant exists in the database; it creates the restaurant, zones, tables, menu, demo users (including admin) and sample bookings.
- To disable seeding, set `cgi.seed.enabled=false`.

## 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

The app usually opens at **http://localhost:5173**. The Vite proxy forwards `/api` requests to the backend (localhost:8080).

## 5. Register + Login

Register a new user with your email and a password (so you can receive booking info by email).

To log in you can:
1) Use your newly created account (role: CUSTOMER)
2) Use the system account (Username: admin@test.com, Password: password) (Role: ADMIN)

## 6. Tests

```bash
./mvnw test
open target/site/jacoco/index.html
```

These commands run all project tests and let you view test coverage.

## Summary – full startup (with Gmail)

1. `docker compose up -d`
2. Create `src/main/resources/application-local.properties` with mail settings (see above).
3. `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
4. `cd frontend && npm install && npm run dev`
5. Admin user: email- admin@test.com & password- password

After that, booking, menu, confirmation emails and admin features are available (log in with the demo admin account).
