## AI kasutamine

*English version: [AI-USAGE.MD](AI-USAGE-EN.MD)*

Selles projektis on AI-d kasutatud arendustoena peamiselt korduva koodi ja boilerplate’i kiiremaks loomiseks.

### AI-ga loodud või märkimisväärselt toetatud komponendid

Järgmised klassid on teostatud AI abiga:

- `BookingPdfServiceImpl`
- `EmailServiceImpl`
- `TheMealDBServiceImpl`

### Frontend

Ai kasutus:

- Disainimis mõte on minu oma, ai aitas teostada
- Pildid on genereeritud chatGPT-ga
- Laudade kuvamine floor planil on teostatud ai ja sõbra toega, kuid põhiloogika on minu loodud

### Debugimine

- Keeruliste bugide juures kasutasin AI tuge, kuid enne otsisin ise vigu ja üritasin neid ise lahendada.

### Testide strateegia AI toel

Testide kirjutamisel kasutati **mallipõhist lähenemist**.  
Iga rakenduse kihi jaoks kirjutasin käsitsi üks-kaks esindusliku testiklassi, et määratleda struktuur, stiil ja oodatav katvus.  
Nende käsitsi kirjutatud näidete põhjal kasutasin AI-d täiendavate testide genereerimiseks sama mustri järgi.

#### Käsitsi kirjutatud alustestid

Järgmised testiklassid on kirjutatud täielikult minu poolt:

- **Domeen / entiteedid**
    - `BookingTest`
    - `UserTest`

- **Mapperid**
    - `BookingMapperTest`
    - `UserMapperTest`

- **Repository**
    - `BookingRepositoryTest`
    - `UserRepositoryTest`

- **Teenus**
    - `BookingServiceImplTest`

- **Controller**
    - `BookingControllerTest`
    - `AuthControllerTest`

### Vastutus ja kontroll

- Kõik arhitektuurilised otsused, äriloogika ja lõplikud implementatsioonid on minu oma.  
- AI-d kasutati rangelt tootlikkuse tööriistana, mitte iseseisva otsustajana.
- Usun et AI kasutamine on pigem tugevus kui nõrkus, kui seda kasutada õigesti (juhendada AI täpselt, kontrollida tema loodud koodi ning saada ise aru, mida ja kuidas ta tegi)
