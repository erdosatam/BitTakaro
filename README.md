# BitTakaro
`(c) BP18Project, erdos, v.1.8`

>Alkalmzás: [Letöltés](deploy/BitTakaro.jar)

## Leírás
Az alkalmazás szöveg titkosítására való. AES 128 bit-es titkosítást használ Base64 encoder segítségével.


## Használat:

####Menü struktúra:
>Fájl (Fájl műveletek)
- Új titkosított fájl - Új fájl létrehozása. Először bekéri a titkosításhoz szükséges kulcsot.
- Fájl betöltése Fájl -  kiválasztása, titkosító kulcs megadása
- Mentés -  Fájl mentése.
- Legutóbb használt - Almenü, melyben az 5 legutóbbi megnyitott fájl található
- Kilép
>Szöveg (Szöveggel kapcsolatos lehetőségek)
>
- Módosítás -  Betöltött fájl szerkesztése, titkosító kulcs megadása kötelező
- Keresés - Szöveg részlet keresése.
>Beállítás (Egyéb beállítások)
- [] Sor törés - A szöveg doboz méretéhez igazítja a szöveget.

> Súgó (dokumentáció+névjegy)

## Titkosítás
A titkosítás módja: AES 128 bit, AES/ECB/PKCS5PADDING

...
