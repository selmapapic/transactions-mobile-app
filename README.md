## Napomene:

Tokom rada na aplikaciji korišten je uređaj u Genymotion emulatoru - Google pixel, 1080x1920, 420dpi, 9.0 - API 28

Min SDK je postavljen na 23 a Target SDK na 26 jer su korištene određene pogodnosti koje nisu podržane u nižem SDK-u

### 1. spirala:
- Amount-i za sve transakcije se mogu unositi kao pozitivni ili negativni ali će se prikazivati kao pozitivni, te će se računati na osnovu tipa transakcije a ne predznaka
- Global amount predstavlja zbir svih troškova i dobitaka
- Pri editovanju ili dodavanju nove transakcije, mijenjanjem tipa se ne validiraju svi podaci (npr da li određeni tip ima interval ili end date) nego se tek validiraju klikom na Save button
- Dopušteno je da sve transakcije, osim onih sa tipom "income", ne moraju imati description
- Za računanje budget i totalLimit-a uzeto je u obzir čitavo trajanje transakcije (od date do endDate)
- Pri sortiranju po nazivu, ne pravi se razlika između transakcija sa velikim i malim slovima

### 2. spirala:
- U GraphFragment-u omogućen je prikaz sljedećih podataka:


  1. Za mjesečne grafove prikazane su vrijednosti svih mjeseci u trenutnoj godini


  2. Za sedmične grafove prikazane su vrijednosti svih sedmica, počevši od prvog do posljednjeg dana u mjesecu. (mjesec april 2020. godine će imati sljedeće sedmice ->  1.- 8. (prva sedmica), 8.- 15. (druga sedmica), 15. - 22. (treća sedmica), 22. - 29. (četvrta sedmica), 29. - 30. (peta sedmica)). Ovime je osigurano da će se uzeti u obzir svi dani trenutnog mjeseca, te da će svaka sedmica imati po 7 dana.


  3. Za grafova po danu prikazane su vrijednosti svih dana u trenutnoj sedmici (od ponedjeljka do nedjelje)

- Swipe je realizovan pomoću onFling metode iz klase GestureDetector
- Pored swipe-a, omogućen je prelazak između fragmenata pomoću dugmadi kako bi se omogućilo korisniku da izabere pristup koji želi
- Pri brisanju transakcije u landscape mode-u potrebno je odselektovati transakciju, koja se nalazila nakon obrisane, kako bi se omogućilo dodavanje nove transakcije

### 3. spirala:

S obzirom da nije bilo naglašeno da je brzina dobavljanja informacija sa web servisa bitna, postoje određena kašnjenja, a to su:

- Pri dodavanju, brisanju ili editovanju transakcije, prije klika na save ili delete button, potrebno je sačekati oko 10 sekundi (razlog tome je što se u tom fragmentu dobavlja account sa web servisa kako bi se ažurirao budžet, pa je potrebno sačekati nekoliko sekundi dok se dobavi jer će se u protivnom krahirati). Da bi se spriječilo krahiranje, sačekati da se ispiše u konzoli "DOBAVLJENE TRANSAKCIJE U DETAIL PRESENTER" i
     "DOBAVLJEN ACCOUNT U DETAIL PRESENTER".
- Pri prikazu grafova, potrebno je sačekati oko 10-15 sekundi dok se ne prikaže graf; u međuvremenu će pisati "No chart data available"
- Pri promjeni mjeseca na glavnom fragmentu, potrebno je sačekati oko 2 sekunde dok se osvježe transakcije
- Povratkom na glavni fragment iz detail fragmenta, potrebno je sačekati par sekundi da se osvježe informacije o accountu i da se osvježe transakcije
