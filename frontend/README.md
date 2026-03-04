# CGI Restaurant – frontend

React + Vite + Tailwind. Logimine ja registreerimine JWT-ga; token hoitakse `localStorage`-is ja lisatakse automaatselt päringutele.

## Käivitamine

1. Paigalda sõltuvused: `npm install`
2. Käivita backend (Spring Boot) pordil 8080
3. Käivita frontend: `npm run dev` — avab http://localhost:5173

Vite proxy suunab `/api` päringud backendile (localhost:8080), seega CORS pole vaja.

## Build

`npm run build` — väljund `dist/`.
