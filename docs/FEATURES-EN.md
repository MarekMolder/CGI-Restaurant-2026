# CGI Restaurant – Functionality

*Eesti keelne versioon: [FEATURES.md](FEATURES-EST.md)*

Overview of application features.

## Admin
- email- admin@test.com
- password- password

## Authentication

- **Registration** – create a new account (email, password).
- **Login** – JWT token; stored in the browser (localStorage) and automatically added to API requests.
- **Roles** – regular customer and administrator; admin sees additional menu items (Table locations, Settings).

## Customer features

### Book a table

- Select date and time (within restaurant opening hours).
- Party size and optional preferences (e.g. by the window, quiet corner).
- Zone: Indoor, Terrace or Private room.
- Available tables are searched for the selected time slot; floor plan shows free / booked / recommended tables.
- Select a table and confirm the booking (guest name, optional special requests).

### Bookings

- The “Bookings” link takes the user to their list of bookings.
- QR code for each booking (for scanning at the restaurant).

### Menu

- List of dishes and drinks (meals from TheMealDB + local drinks).
- Categories and prices.

### Restaurant info

- Address and contact details.

## Administrator features

Visible only to administrators (after login).

### Table locations

- Select zone (Indoor / Terrace / Private room) and the corresponding floor plan image.
- Tables shown on the plan; **drag** (x, y), **resize** (width, height) and **rotate** (degrees).
- Changes are saved to the backend (PATCH layout).

### Settings (table management)

- **All tables** – list with zone and features.
- **Add new table** – label, capacity, min. party size, shape (rectangle / circle / oval), **zone**, **features** (multi-select), position (x, y), width, height, rotation, active.
- **Edit** – same form with existing table data.
- **Delete** – delete table with confirmation.

## Technical details

- **Backend:** REST API (Spring Boot); every class is commented.
- **Database:** PostgreSQL; tables created with Hibernate `ddl-auto=update`; initial data from DataSeeder when `cgi.seed.enabled=true`.
- **Confirmation emails:** Sent when the backend is run with the `local` profile and `application-local.properties` contains mail settings (see [Setup](SETUP-EST.md)).
