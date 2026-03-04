# CGI Restaurant

*Eesti keelne versioon: [README.md](README.md)*

Restaurant booking system: customers can book tables, view the menu and their bookings (including QR codes). Administrators can manage tables and their positions.

## Quick overview

- **Stack:** Spring Boot (Java), React + Vite + Tailwind, PostgreSQL
- **Authentication:** JWT (registration, login)
- **Database:** PostgreSQL (Docker); optionally seeded with initial data (DataSeeder)

## Quick start

1. **Database:** `docker compose up -d`
2. **Backend:** `./mvnw spring-boot:run` (data is seeded by default)
3. **Frontend:** `cd frontend && npm install && npm run dev` → http://localhost:5173

For booking confirmation emails (Gmail), the **local** profile and mail settings are required – see [Setup](docs/SETUP-EST.md).

## Documentation

| Document | Content |
|----------|---------|
| [**docs/Setup (ET)**](docs/SETUP-EST.md) | Detailed setup: Docker, Spring Boot profiles, Gmail, DataSeeder, npm |
| [**docs/Setup (EN)**](docs/SETUP-EN.md) | Same in English |
| [**docs/API (ET)**](docs/API-EST.md) | REST API overview: endpoints, requests, bodies, permissions |
| [**docs/API (EN)**](docs/API-EN.md) | Same in English |
| [**docs/Architecture (ET)**](docs/ARCHITECTURE-EST.md) | Code logic: entities, key services (recommendation, adjacency, booking) |
| [**docs/Architecture (EN)**](docs/ARCHITECTURE-EN.md) | Same in English |
| [**docs/Features (ET)**](docs/FEATURES-EST.md) | What the app does: booking, menu, admin (tables, settings) |
| [**docs/Features (EN)**](docs/FEATURES-EN.md) | Same in English |
| [**docs/Reflection (ET)**](docs/REFLECTION-EST.md) | Time spent, challenges, learnings, AI role |
| [**docs/Reflection (EN)**](docs/REFLECTION-EN.md) | Same in English |
| [**docs/AI usage (ET)**](docs/AI-USAGE-EST.md) | How AI was used in the project (Estonian) |
| [**docs/AI usage (EN)**](docs/AI-USAGE-EN.MD) | Same in English |

## About the project

- Backend: `src/main/java` – every class is commented
- Frontend: `frontend/` – React, Vite, Tailwind; short description in [frontend/README.md](frontend/README.md)
