# CGI Restoran

*English version: [README-EN.md](README-EN.md)*

Broneerimis süsteem restoranile: klient saab broneerida laudu, vaadata menüüd ja oma broneeringuid (sh QR-koode). Administraator saab hallata laudu ja nende asukohti.

## Kiirülevaade

- **Stack:** Spring Boot (Java), React + Vite + Tailwind, PostgreSQL
- **Autentimine:** JWT (registreerimine, sisselogimine)
- **Andmebaas:** PostgreSQL (Docker); vajadusel täidetakse algandmetega (DataSeeder)

## Kiire käivitamine

1. **Andmebaas:** `docker compose up -d`
2. **Backend:** `./mvnw spring-boot:run` (vaikimisi andmed seeditakse automaatselt)
3. **Frontend:** `cd frontend && npm install && npm run dev` → http://localhost:5173

Broneeringute kinnitusmeilide saatmiseks (Gmail) on vaja **local** profiili ja meili seadeid – vt [Setup](docs/SETUP-EST.md).

## Dokumentatsioon

| Dokument                                     | Sisukord                                                                                     |
|----------------------------------------------|----------------------------------------------------------------------------------------------|
| [**docs/Setup**](docs/SETUP-EST.md) / [EN](docs/SETUP-EN.md) | Täpne käivitamine: Docker, Spring Boot, Gmail, DataSeeder, npm |
| [**docs/API**](docs/API-EST.md) / [EN](docs/API-EN.md) | REST API ülevaade: endpointid, päringud, kehad, õigused |
| [**docs/Arhitektuur**](docs/ARCHITECTURE-EST.md) / [EN](docs/ARCHITECTURE-EN.md) | Koodi loogika: entity'd, olulised teenused |
| [**docs/Funktsionaalsus**](docs/FEATURES-EST.md) / [EN](docs/FEATURES-EN.md) | Mida rakendus teeb: broneering, menüü, admin |
| [**docs/Refleksioon**](docs/REFLECTION-EST.md) / [EN](docs/REFLECTION-EN.md) | Kulunud aeg, raskused, õppetunnid, AI roll |
| [**docs/AI kasutamine**](docs/AI-USAGE-EST.md) / [EN](docs/AI-USAGE-EN.MD) | Kuidas projekti arendamisel on kasutatud AI-t |
| [**README (EN)**](README-EN.md) | Project overview in English |

## Projektist

- Backend: `src/main/java` – iga klass kommenteeritud
- Frontend: `frontend/` – React, Vite, Tailwind; lühike kirjeldus on [frontend/README.md](frontend/README.md)
