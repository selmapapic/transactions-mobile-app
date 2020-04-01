## Napomene:
- Amount-i za sve transakcije se mogu unositi kao pozitivni ili negativni ali će se prikazivati kao pozitivni, te će se računati na osnovu tipa transakcije a ne predznaka
- Global amount predstavlja zbir svih troškova i dobitaka
- Pri editovanju ili dodavanju nove transakcije, mijenjanjem tipa se ne validiraju svi podaci (npr da li određeni tip ima interval ili end date) nego se tek validiraju klikom na Save button
- Dopušteno je da sve transakcije, osim onih sa tipom "income", ne moraju imati description
- Za računanje budget i totalLimit-a uzeto je u obzir čitavo trajanje transakcije (od date do endDate) 
- Pri sortiranju po nazivu, ne pravi se razlika između transakcija sa velikim i malim slovima 
- Min SDK je postavljen na 23 a Target SDK na 26 jer su korištene određene pogodnosti koje nisu podržane u nižem SDK-u