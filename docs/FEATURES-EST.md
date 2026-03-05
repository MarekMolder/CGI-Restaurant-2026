# CGI Restoran – Funktsionaalsus

*English version: [FEATURES-EN.md](FEATURES-EN.md)*

Ülevaade rakenduse funktsionaalsusest.

## Admin
- email- admin@test.com
- parool- password

## Autentimine

- **Registreerimine** – uue konto loomine (e-mail, parool).
- **Sisselogimine** – JWT-token; hoitakse brauseris (localStorage) ja lisatakse automaatselt API-päringutele.
- **Rollid** – tavaklient ja administraator; admin näeb täiendavaid menüüpunkte (Laudade asukohad, Seaded).

## Klientide funktsioonid

### Broneeri laud

- Kuupäeva ja kellaaja valik (restorani lahtiolekuaegade piires).
- Osalejate arv ja valikulised eelistused (nt akna all, vaikne nurk).
- Tsoon: Sisesaal, Terrass või Privaatruum.
- Vabu laudu otsitakse valitud ajapiirile; kuvatakse põhiplaan (vabad / broneeritud / soovitusega lauad).
- Laua valimine ja broneeringu kinnitamine (broneerija nimi, valikulised lisasoovid).

### Broneeringud

- Link „Broneeringud” viib kasutaja oma broneeringute nimekirja.
- QR-kood iga broneeringu kohta (skaneerimiseks restoranis).

### Menüü

- Praadide ja jookide nimekiri (TheMealDB-st toodud road + kohalikud joogid).
- Kategooriad ja hinnad.

### Restorani info

- Aadress ja kontaktandmed.

## Administraatori funktsioonid

Nähtavad ainult administraatorile (pärast sisselogimist).

### Laudade asukohad

- Tsooni valik (Sisesaal / Terrass / Privaatruum) ja vastav põhiplaani pilt.
- Laudade kuvamine põhiplaanil; **lohistamine** (x, y), **suuruse muutmine** (laius, kõrgus) ja **pööramine** (kraad).
- Muudatused salvestatakse backendi (PATCH layout).

### Seaded (laudade haldus)

- **Kõik lauad** – nimekiri koos tsooni ja funktsioonidega.
- **Lisa uus laud** – nimetus, mahutavus, min. inimest, kuju (ristkülik / ring / ovaal), **tsoon**, **funktsioonid** (mitmikvalik), asukoht (x, y), laius, kõrgus, pööre, aktiivne.
- **Muuda** – sama vorm olemasoleva laua andmetega.
- **Kustuta** – laua kustutamine kinnitusega.

## Tehnilised detailid

- **Backend:** REST API (Spring Boot); iga klass on kommenteeritud.
- **Andmebaas:** PostgreSQL; tabelid luuakse Hibernate `ddl-auto=update`-ga; algandmed DataSeederist (kui `cgi.seed.enabled=true`).
- **Kinnitusmeilid:** Saadetakse siis, kui backend käivitatakse `local` profiiliga ja `application-local.properties` sisaldab meili andmeid (vt [Setup](SETUP-EST.md)).
