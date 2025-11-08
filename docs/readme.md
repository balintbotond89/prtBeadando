Amőba Játék Projekt – Funkcionális Leírás

Projekt Áttekintés

Ez egy Amőba játék Java alkalmazás, amelyet Maven projektként fejlesztettem.
A játék célja, hogy két játékos 5 azonos szimbólumot helyezzen el egy sorban, oszlopban vagy átlósan egy 10×10-es táblán.

1. Főbb Jellemzők:

    Játékmenet:

        - Két játékmód: Ember vs Ember és Ember vs Számítógép

        - Tábla mérete: 10×10 rögzített méret

        - Győzelem feltétele: 5 azonos szimbólum egy vonalban (vízszintes, függőleges, átlós)

        - Játékos szimbólumok: "X" és "O"

    Speciális Szabályok:

        - Szomszédossági követelmény – minden új lépésnek érintkeznie kell korábbi lépéssel.

        - 5 egymás követő szimbólum győzelemhez

2. Projekt Struktúra:

    2.1 Model Réteg (Tartalmi Réteg)

        2.1.1 Board: Játéktábla reprezentáció

            - 10×10-es kétdimenziós tömb

            - Validációs metódusok (érvényes pozíció, üres mező)

            - Megjelenítési funkciók formázott kimenettel

         2.1.2 Player interfész - Játékosok közös viselkedése
           
         2.1.3 HumanPlayer - Emberi játékos implementáció
           
         2.1.4 AIPlayer - Mesterséges intelligencia
   
            - Nyerő lépés keresése
   
            - Blokkoló stratégia
   
            - Véletlenszerű lépés mechanizmus
   
        2.1.5 GameState enum - Játékállapotok (FOLYAMATBAN, NYERT, DÖNTETLEN)
   
            -Beállítás tároló
   
        2.1.6 GameMode enum - Játékmódok (EMBER\_VS\_EMBER, EMBER\_VS\_AI)
   
            -Beállítás tároló

    2.2 Service Réteg (Szolgáltatási Réteg)
   
        2.2.1 GameService - Fő játéklogika
   
                - Lépések érvényesítése és végrehajtása
   
                - Játékos váltás automatikus vezérlése
   
                - AI lépés kezelése
   
                - Játékállapot frissítése
   
        2.2.2 WinChecker; Győzelem ellenőrzés
   
               - 4 irányú ellenőrzés (vízszintes, függőleges, átlós, fordított átlós)
   
               - Dinamikus szomszéd számlálás mindkét irányba
   
        2.2.3 LoggerService - Naplózási megoldás
   
                - Súlyos hibák, figyelmeztetések, információs üzenetek

    2.3 UI Réteg - Felhasználói Felület
   
        2.3.1 GameController
   
                - Felhasználói interakció kezelése
   
                - Menürendszer játékmód választáshoz
   
                - Bemenet validáció és hiba kezelés
   
                - Játék ciklus vezérlése
   
        2.3.2 Main - Alkalmazás belépési pontja

3. Műszaki Jellemzők:


    AI Stratégia:
       - Nyerő lépés keresése – Ha az AI nyerhet a következő lépéssel, azt választja

       - Blokkolás – Ha az ellenfél nyerhet, blokkolja a nyerő pozíciót

       - Véletlenszerű lépés – Egyébként érvényes véletlenszerű mezőt választ

4. Validációk:


    - Pozíció érvényesség: 1-10 tartomány

    - Üres mező ellenőrzése

    - Játékos váltás automatikus vezérlése

    - Bemenet formátuma: "sor oszlop" (pl.: "5 3")

5. Hibakezelés:
   
        NumberFormatException kezelése
        
        Érvénytelen tartomány ellenőrzése
   
        Foglalt mező észlelése
        
        Átfogó naplózás minden fontos eseményre

6. Futtatás és Használat:

          Indítási folyamat:

          1. Program indítása

          2. Játékmód választása (1: Ember vs Ember, 2: Ember vs AI)

          3. Játékosok nevének bekérése

          4. Tábla megjelenítése formázott formában