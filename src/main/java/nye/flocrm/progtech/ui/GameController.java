package nye.flocrm.progtech.ui;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.GameState;
import nye.flocrm.progtech.model.HumanPlayer;
import nye.flocrm.progtech.model.Player;
import nye.flocrm.progtech.service.DatabaseService;
import nye.flocrm.progtech.service.GameLoader;
import nye.flocrm.progtech.service.GameService;
import nye.flocrm.progtech.service.LoggerService;

/**
 * A játék vezérléséért felelős osztály.
 * Kezeli a felhasználói interfészt és a játék folyamatát.
 */
public class GameController {
    private GameService gameService;
    private final Scanner scanner;
    private final GameLoader gameLoader;
    private final DatabaseService databaseService;

    /**
     * Konstruktor a játékvezérlő inicializálásához.
     * Inicializálja a szükséges szolgáltatásokat és ellenőrzi az adatbázis kapcsolatot.
     */
    public GameController() {
        this.scanner = new Scanner(System.in);
        this.gameLoader = new GameLoader();
        this.databaseService = new DatabaseService();

        // Kapcsolat ellenőrzése az adatbázissal:
        if (!databaseService.isConnectionAvailable()) {
            LoggerService.info("Figyelmeztetés: Nem sikerült csatlakozni az adatbázishoz!");
        }
    }

    /**
     * A játék fő indítási pontja és vezérlőciklusa.
     * Ez a metódus indítja el a játékot, és kezeli a főmenü megjelenítését
     * és a felhasználói interakciót. A metódus a következő feladatokat látja el:
     * <p>
     * - Kiírja az üdvözlő üzenetet és a játék alapvető szabályait
     * - Megjeleníti a főmenüt és kezeli a felhasználó választásait
     * - Biztosítja a scanner erőforrás megfelelő lezárását a program végén
     * - Kezeli a különböző menüpontok (új játék, betöltés, ranglista, kilépés) végrehajtását
     * <p>
     * A metódus a program futását a főmenü ciklusán keresztül irányítja, amíg a felhasználó
     * nem választja a kilépés opciót. Kilépéskor megfelelően lezárja a használt erőforrásokat.
     *
     * @see #showMainMenu() A főmenü megjelenítését és kezelését végző metódus
     */
    public void run() {
        System.out.println("\nIsten hozott az amőba játékban!\n");
        System.out.println("Rakj le 5 jelet egy sorban a győzelemhez!");

        showMainMenu();

        scanner.close();
    }

    /**
     * Megjeleníti és kezeli a játék főmenüjét.
     * A metódus egy folyamatos ciklusban jeleníti meg a főmenü opcióit és kezeli
     * a felhasználó választását. A menü a következő opciókat tartalmazza:
     * <p>
     * - 1. Új játék indítása
     * - 2. Játék betöltése fájlból
     * - 3. Mentett állás visszatöltése (automatikus mentésből)
     * - 4. Ranglista megjelenítése
     * - 5. Kilépés a játékból
     * <p>
     * A metódus a felhasználó választása alapján meghívja a megfelelő segédmetódusokat.
     * A ciklus addig fut, amíg a felhasználó nem választja a kilépés opciót (5).
     *
     * @see #startNewGame() Új játék indítását végző metódus
     * @see #loadGame() Játék betöltését végző metódus
     * @see #loadSavedGame() Automatikusan mentett játék betöltését végző metódus
     * @see #showRanking() Ranglista megjelenítését végző metódus
     */
    private void showMainMenu() {
        while (true) {
            System.out.println("\n********** Főmenü **********");
            System.out.println("1. Új játék");
            System.out.println("2. Játék betöltése");
            System.out.println("3. Mentett állás visszatöltése");
            System.out.println("4. Ranglista");
            System.out.println("5. Kilépés");
            System.out.println("****************************");
            System.out.print("\n---> Válassz opciót: ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    loadGame();
                    break;
                case 3:
                    if (gameLoader.saveFileExists()) {
                        loadSavedGame();
                    } else {
                        System.out.println("Nem található betölthető játék");
                    }
                    break;
                case 4:
                    showRanking();
                    break;
                case 5:
                    System.out.println("Kilépés...");
                    return; // itt return, mivel ki akarunk lépni
                default:
                    System.out.println("Érvénytelen választás!");
            }
        }
    }

    /**
     * Új játékot indít el a játékos által megadott nevekkel.
     *
     * <p>A metódus a következő feladatokat látja el:
     * <ul>
     *   <li>Inicializálja a játék szolgáltatást (GameService) a kiválasztott játékmóddal</li>
     *   <li>Interaktív módon bekéri a játékosok neveit a játékmódnak megfelelően</li>
     *   <li>Beállítja a játékosok neveit a játék szolgáltatásban</li>
     *   <li>Elindítja a játék fő ciklusát</li>
     *   <li>Kezeleli a játék indítás közben fellépő kivételeket</li>
     * </ul>
     *
     * @see GameService#GameService(GameMode) A játék szolgáltatás konstruktora
     * @see #getPlayerNames() A játékos nevek bekérését végző metódus
     * @see #gameLoop() A játékmenetet vezérlő metódus
     */
    private void startNewGame() {
        try {
            // Játékmód kiválasztása - ha visszalépnek, akkor kilépünk
            if (!selectGameMode()) {
                return;
            }

            // Játékos nevek bekérése
            getPlayerNames();

            // Játék indítása
            gameLoop();

        } catch (Exception e) {
            LoggerService.severe("Váratlan hiba történt a játék indítása során", e);
            System.err.println("Váratlan hiba történt. További részletek a naplófájlban.");
        }
    }

    /**
     * Megjeleníti a játék ranglistáját az adatbázisból.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Megjeleníti a ranglista fejlécét és leírását
     * - Ellenőrzi az adatbázis kapcsolat elérhetőségét
     * - Ha nem elérhető az adatbázis, hibát jelez és visszatér a menübe
     * - Lekéri az adatbázisból a legjobb 5 játékos összpontszám szerinti listáját
     * - Formázottan megjeleníti a ranglistát sorszámozva
     * - Kezeli az üres ranglista esetét, tájékoztató üzenettel
     * - Vár a felhasználó bemenetére a folytatás előtt
     * <p>
     * A ranglista formátuma:
     * 1. Játékosnév - 100 pont
     * 2. MásikJátékos - 80 pont
     * <p>
     * Adatbázis kapcsolat hiányában a metódus tájékoztatja a felhasználót
     * a kapcsolati problémáról és ajánlja a beállítások ellenőrzését.
     *
     * @see DatabaseService#getTopPlayers(int) A ranglista lekérdezését végző metódus
     * @see DatabaseService#isConnectionAvailable() Az adatbázis kapcsolat ellenőrzését végző metódus
     *
     * @implNote A metódus a ranglistát a scores táblából kérdezi le,
     *           ahol a játékosok pontszámai összeadódnak és csökkenő sorrendbe rendeződnek
     */
    private void showRanking() {
        System.out.println("\n******* Ranglista ********");

        System.out.println("A mindenkori legjobb öt játékos és összpontszáma: ");

        if (!databaseService.isConnectionAvailable()) {
            System.out.println("Hiba: Nem sikerült csatlakozni az adatbázishoz!");
            System.out.println("Kérlek ellenőrizd az adatbázis kapcsolat beállításait.");
            System.out.println("\n---> Nyomj Enter-t a folytatáshoz...");
            scanner.nextLine();
            return;
        }

        List<String> topPlayers = databaseService.getTopPlayers(5);

        if (topPlayers.isEmpty()) {
            System.out.println("Még nincs eredmény a ranglistán!");
            System.out.println("Játsz egy játékot, hogy felkerülj a ranglistára!");
        } else {
            for (String player : topPlayers) {
                System.out.println(player);
            }
        }

        System.out.println("****************************");

        System.out.println("\n---> Nyomj Enter-t a folytatáshoz...");
        scanner.nextLine();
    }

    /**
     * Betölti és beállítja a játék állapotát egy mentett GameState objektumból.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Létrehoz egy új GameService példányt a mentett játékmód alapján
     * - Másolja a mentett tábla állapotát az aktuális játék táblájába
     * - Beállítja a játékos adatait (nevet és pontszámot) a mentett állapotból
     * - Frissíti a győzelem ellenőrző állapotát az új tábla alapján
     * <p>
     * A metódus csak HumanPlayer típusú játékosok esetén másolja a pontszámot,
     * mivel az AI játékosoknak nincs pontszámuk.
     *
     * @param gameState a betöltendő játékállapot, amely tartalmazza:
     *                  - a mentett játéktáblát
     *                  - a játékos adatait
     *                  - a játékmódot
     *                  - az időbélyeget
     *
     * @see #copyBoard(Board, Board) A tábla másolását végző segédmetódus
     * @see GameService#GameService(GameMode) A játék szolgáltatás konstruktora
     * @see GameService#checkForWinner() A győzelem ellenőrzését végző metódus
     *
     * @implNote A metódus feltételezi, hogy a GameService player1 mindig HumanPlayer típusú
     *           a HUMAN_VS_HUMAN és HUMAN_VS_AI játékmódokban. Ez a jelenlegi implementációval
     *           konzisztens, mivel az AI mindig a második játékos.
     *
     * @throws NullPointerException ha a gameState paraméter null, vagy a benne lévő
     *         játékos nem HumanPlayer a név és pontszám másolásakor
     */
    private void loadGameState(GameLoader.GameState gameState) {
        // GameService létrehozása a mentett játékmóddal
        this.gameService = new GameService(gameState.gameMode());

        // Tábla másolása
        copyBoard(gameState.board(), gameService.getBoard());

        // Játékos adatok másolása
        if (gameState.player() instanceof HumanPlayer savedPlayer) {
            HumanPlayer currentPlayer = (HumanPlayer) gameService.getPlayer1();
            currentPlayer.setName(savedPlayer.getName());
            currentPlayer.setScore(savedPlayer.getScore());
        }

        // WinChecker állapot frissítése
        gameService.checkForWinner();
    }

    /**
     * Megjeleníti a betöltött játékállapot információit a felhasználó számára.
     * A metódus a következő információkat jeleníti meg formázott módon:
     * <p>
     * - A betöltött játékos nevét és a betöltés sikerességének megerősítését
     * - A mentés időbélyegét (timestamp)
     * - A játékmód típusát és megjelenítendő nevét
     * - A játékos aktuális pontszámát (csak HumanPlayer esetén)
     * <p>
     * A metódus elsősorban visszajelzést ad a felhasználónak a sikeres betöltésről
     * és áttekintést nyújt a betöltött játék alapvető paramétereiről.
     *
     * @param gameState a betöltött játékállapot, amelynek információit meg kell jeleníteni
     *                  tartalmazza a játékos adatait, időbélyeget és játékmódot
     *
     * @see GameLoader.GameState#player() A játékos adatainak lekérdezése
     * @see GameLoader.GameState#timestamp() A mentés időbélyegének lekérdezése
     * @see GameLoader.GameState#gameMode() A játékmód lekérdezése
     *
     * @implNote A metódus csak olvasási műveleteket végez, nem módosítja a gameState objektumot.
     *           A pontszám megjelenítése csak akkor történik, ha a játékos HumanPlayer típusú,
     *           mivel az AI játékosoknak nincs pontszámuk.
     *
     * @example
     * // Példa kimenet:
     * // SIKER: Betöltötted Kati játékát (Wed Nov 15 14:30:45 CET 2023)
     * // Játékmód: Ember vs Számítógép
     * // Pontszám: 15
     */
    private void printGameStateInfo(GameLoader.GameState gameState) {
        System.out.println("SIKER: Betöltötted " + gameState.player().getName() +
                " játékát (" + gameState.timestamp() + ")");
        System.out.println("Játékmód: " + gameState.gameMode().getDisplayName());

        if (gameState.player() instanceof HumanPlayer humanPlayer) {
            System.out.println("Pontszám: " + humanPlayer.getScore());
        }
    }

    /**
     * Betölti a játék állapotát egy fájlból.
     *
     * <p>A metódus interaktív módon kéri a felhasználótól a betöltendő fájl elérési útját.
     * A következő funkciókat tartalmazza:
     * <ul>
     *   <li>Felhasználói interfész a fájlnév megadásához</li>
     *   <li>"vissza" kulcsszóval lehetőség van a menübe való visszalépésre</li>
     *   <li>Ellenőrzi, hogy a megadott fájl létezik-e és nem üres-e</li>
     *   <li>Explicit tiltja a "game_save.txt" fájl betöltését</li>
     *   <li>Hibakezelés érvénytelen fájlformátum vagy I/O hibák esetén</li>
     *   <li>Sikeres betöltés esetén frissíti a játék állapotát és megjeleníti a játékot</li>
     * </ul>
     *
     * <p>A metódus addig ismétli a fájlnév bekérését, amíg a felhasználó nem ad meg érvényes fájlt
     * vagy nem lép vissza a menübe a "vissza" kulcsszóval.
     *
     * <p><strong>Fájlformátum elvárások:</strong>
     * <ul>
     *   <li>A fájlnak tartalmaznia kell a tábla állapotát és a játékos adatokat</li>
     *   <li>A fájl nem lehet üres</li>
     *   <li>A fájlnév nem lehet "game_save.txt"</li>
     * </ul>
     *
     * @see GameLoader#loadGame(String) A tényleges fájlbetöltést végző metódus
     * @see #loadGameState(GameLoader.GameState) A játékállapot betöltését végző metódus
     * @see #printGameStateInfo(GameLoader.GameState) A játék információk megjelenítését végző metódus
     * @see #gameLoop() A játékmenetet vezérlő metódus
     *
     */
    private void loadGame() {
        while (true) {
            System.out.println("\n****** Játék betöltése ******");
            System.out.println("Add meg a betöltendő fájl teljes elérési útját.");
            System.out.println("Példa: C:\\Temp\\game_save.txt vagy game_save.txt");
            System.out.println("****************************");
            System.out.print("\n---> Fájlnév (vagy 'vissza' a menühöz): ");

            String filename = scanner.nextLine().trim();

            // Vissza a menübe
            if (filename.equalsIgnoreCase("vissza")) {
                return;
            }

            try {
                // Fájl ellenőrzése
                File file = new File(filename);

                // Ha a fájlnév üres
                if (filename.isEmpty()) {
                    System.out.println("HIBA: A fájlnév nem lehet üres!");
                    System.out.println("Kérlek adj meg egy érvényes fájlnevet!");
                    continue;
                }

                // Tiltjuk a game_save.txt fájl betöltését, mivel az az idégelenes mentésre mutat
                if (filename.equalsIgnoreCase("game_save.txt")) {
                    System.out.println("HIBA: A fájl nem található: " + filename);
                    System.out.println("Kérlek adj meg egy érvényes elérési útat!");
                    continue;
                }

                //Ha a fájl nem létezik
                if (!file.exists()) {
                    System.out.println("HIBA: A fájl nem található: " + filename);
                    System.out.println("Kérlek ellenőrizd az elérési utat és próbáld újra!");
                    continue;
                }

                //Ha létezik a fájl de üres
                if (file.length() == 0) {
                    System.out.println("HIBA: A fájl üres: " + filename);
                    System.out.println("Kérlek válassz egy másik fájlt!");
                    continue;
                }

                System.out.println("Fájl betöltése: " + filename);

                // Játék betöltése - a paraméteres loadGame-t hívjuk
                GameLoader.GameState gameState = gameLoader.loadGame(filename);

                // Játék állapot betöltése
                loadGameState(gameState);

                // Információk kiírása
                printGameStateInfo(gameState);

                // Játék folytatása
                gameLoop();
                return;

            } catch (Exception e) {
                System.out.println("HIBA a fájl betöltésekor: " + e.getMessage());
                System.out.println("INFO: Kérlek próbálj meg egy másik fájlt, vagy írd be 'vissza' a menühöz.");
            }
        }
    }

    /**
     * Betölti az automatikusan mentett játékállapotot az alapértelmezett fájlból. (Idéglenes mentés)
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Megpróbálja betölteni a játékállapotot a GameLoader segítségével
     * - Sikeres betöltés esetén beállítja a játék állapotát a loadGameState() metódussal
     * - Megjeleníti a betöltött játék információit a felhasználó számára
     * - Folytatja a játékot a gameLoop() metódussal
     * - Hibakezelés: ha a betöltés sikertelen, hibát jelez és új játékot indít
     * <p>
     * A metódus az alapértelmezett "game_save.txt" fájlt használja a betöltéshez,
     * amely a GameLoader SAVE_FILE konstansában van definiálva.
     *
     * @see GameLoader#loadGame() Az alapértelmezett fájlból történő betöltést végző metódus
     * @see #loadGameState(GameLoader.GameState) A játékállapot betöltését végző metódus
     * @see #printGameStateInfo(GameLoader.GameState) A játék információk megjelenítését végző metódus
     * @see #gameLoop() A játékmenetet vezérlő metódus
     * @see #startNewGame() Az új játék indítását végző metódus
     *
     * @implNote A metódus kivételkezeléssel van ellátva, hogy biztosítsa a rendszer
     *           stabil működését még betöltési hibák esetén is. Ha a betöltés nem
     *           sikerül, a rendszer automatikusan új játékot indít a felhasználó
     *           számára, így biztosítva a folyamatos játékélményt.
     *
     * @example
     * // Sikeres betöltés esete:
     * // 1. Betölti a mentett állást a game_save.txt fájlból
     * // 2. Beállítja a játék állapotát
     * // 3. Megjeleníti a játék információit
     * // 4. Folytatja a játékot
     * <p>
     * // Sikertelen betöltés esete:
     * // 1. Kivételt dob a GameLoader.loadGame()
     * // 2. Elfogja a kivételt és hibát jelez
     * // 3. Új játékot indít a startNewGame() metódussal
     */
    private void loadSavedGame() {
        try {
            GameLoader.GameState gameState = gameLoader.loadGame();

            // Játék állapot betöltése
            loadGameState(gameState);

            // Információk kiírása
            printGameStateInfo(gameState);

            // Játék folytatása
            gameLoop();

        } catch (Exception e) {
            System.out.println("Hiba a mentett állás betöltésekor: " + e.getMessage());
            LoggerService.warning("Mentett játék betöltési hiba: " + e.getMessage());
            System.out.println("Új játékot indítok helyette...");
            startNewGame();
        }
    }

    /**
     * Egy játéktábla teljes állapotát másolja át egy másik táblába.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Kiüríti a cél táblát a target.clear() hívással
     * - Végigiterál a forrás tábla összes celláján (0-tól Board.SIZE-1-ig)
     * - Minden nem üres cellát (szimbólum != '.') átmásol a forrásból a célba
     * - Megőrzi a cellák pozícióját (sor és oszlop indexek)
     * - Csak érvényes szimbólumokat másol ('X' és 'O')
     * <p>
     * A metódus feltételezi, hogy a forrás és cél táblák mérete megegyezik (Board.SIZE).
     *
     * @param source a forrás tábla, amelyből másoljuk az állapotot
     * @param target a cél tábla, amelybe másoljuk az állapotot
     *
     * @see Board#clear() A cél tábla kiürítését végző metódus
     * @see Board#getSymbolAt(int, int) A cella értékének lekérdezését végző metódus
     * @see Board#placeSymbol(int, int, char) A szimbólum elhelyezését végző metódus
     *
     * @implNote A metódus csak olvasási műveleteket végez a forrás táblán, így az változatlan marad.
     *           A cél tábla teljesen újraírásra kerül, minden korábbi állapot elvész.
     *           A másolás során csak a nem üres mezők kerülnek átvitelre, optimalizálva a folyamatot.
     *
     * @implSpec A metódus nem végez méretellenőrzést, feltételezve, hogy a forrás és cél táblák
     *           kompatibilisek. Ha a táblák mérete eltérő, váratlan viselkedés léphet fel.
     *
     * @example
     * // Példa másolási folyamat:
     * // Forrás tábla: [['X', '.', 'O'], ['.', 'X', '.'], ['.', '.', 'O']]
     * // Cél tábla kiürítése
     * // Cél tálla másolás után: [['X', '.', 'O'], ['.', 'X', '.'], ['.', '.', 'O']]
     */
    private void copyBoard(Board source, Board target) {
        target.clear();

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                char symbol = source.getSymbolAt(row, col);
                if (symbol != '.') { // Csak a nem üres mezőket másoljuk
                    target.placeSymbol(row, col, symbol);
                }
            }
        }
    }

    /**
     * Feldolgozza és érvényesíti a felhasználó által megadott lépés bemenetét.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Felosztja a bemeneti stringet szóközök mentén részekre
     * - Ellenőrzi, hogy pontosan két számot tartalmaz-e a bemenet
     * - Konvertálja a számokat integer értékekké
     * - Ellenőrzi a számok tartományát (1 és Board.SIZE között kell legyenek)
     * - Átkonvertálja a felhasználóbarát (1-alapú) indexeket programbarát (0-alapú) indexekké
     * - Megkísérli végrehajtani a lépést a játék szolgáltatáson keresztül
     * - Átfogó hibakezelést biztosít minden lehetséges bemeneti hiba esetén
     *
     * @param input a felhasználó által megadott bemeneti string, amely két számot
     *              kell tartalmazzon szóközzel elválasztva (pl.: "3 5")
     *
     * @return true ha a lépés sikeresen végrehajtásra került, false ha érvénytelen
     *         a bemenet vagy a lépés nem hajtható végre
     *
     * @see GameService#makeMove(int, int) A tényleges lépés végrehajtását végző metódus
     * @see Board#SIZE A tábla méretének konstansa
     *
     * @implNote A metódus kivételkezeléssel van ellátva, hogy biztosítsa a rendszer
     *           stabil működését még érvénytelen bemenet esetén is. A hibakezelés
     *           részletes és felhasználóbarát hibaüzeneteket biztosít.
     *
     * @throws NumberFormatException ha a bemenet nem konvertálható számokká
     *         (a kivételt elkapja és helyileg kezeli, nem propagálja tovább)
     *
     * @example
     * // Érvényes bemenetek: "3 5", "10 2", "1 1"
     * // Érvénytelen bemenetek: "3", "3 5 7", "abc", "0 5", "11 1"
     */
    private boolean processMoveInput(String input) {
        try {
            String[] parts = input.split("\\s+");

            // Pontosan két szám ellenőrzése
            if (parts.length != 2) {
                System.out.println("Hiányos bemenet! Két számot kell megadni szóközzel elválasztva (pl: 3 5).");
                return false;
            }

            // Számok konvertálása
            int row = Integer.parseInt(parts[0]) - 1;
            int col = Integer.parseInt(parts[1]) - 1;

            // Tartomány ellenőrzése
            if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE) {
                System.out.println("Érvénytelen tartomány! Csak 1 és " + Board.SIZE + " közötti számokat adj meg.");
                return false;
            }

            // Lépés végrehajtása
            if (gameService.makeMove(row, col)) {
                return true;
            } else {
                System.out.println("Érvénytelen lépés! A mező már foglalt!");
                return false;
            }

        } catch (NumberFormatException e) {
            System.out.println("Érvénytelen bevitel! Csak számokat adhatsz meg (1-" + Board.SIZE + ").");
            System.out.println("Példa: '3 5' vagy '10 2'");
            return false;
        } catch (Exception e) {
            System.out.println("Váratlan hiba történt. Próbáld újra.");
            LoggerService.warning("Váratlan hiba processMoveInput-ban: " + e.getMessage());
            return false;
        }
    }

    /**
     * Felajánlja a játék mentésének lehetőségét a felhasználónak kilépés előtt.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Interaktív párbeszédet kezd a felhasználóval a játék mentéséről
     * - Feldolgozza a felhasználó válaszát (igen/nem vagy rövidítések)
     * - Ha a felhasználó igennel válaszol, megkísérli menteni a játékállapotot
     * - Ha a mentés sikeres, visszajelzést ad a felhasználónak
     * - Ha a mentés sikertelen, hibát jelez és naplózza a problémát
     * - Ha a felhasználó nemmel válaszol, kilép mentés nélkül
     * - Érvénytelen válasz esetén ismétli a kérdést, amíg érvényes választ nem kap
     * <p>
     * A metódus a következő válaszokat fogadja el:
     * - Igen: "i", "igen" (kis/nagybetű érzéketlen)
     * - Nem: "n", "nem" (kis/nagybetű érzéketlen)
     *
     * @see GameLoader#saveGame(Board, Player, GameMode) A játék mentését végző metódus
     * @see LoggerService#warning(String) A figyelmeztető üzenetek naplózását végző metódus
     *
     * @implNote A metódus egy while ciklusban működik, amely addig ismétli a kérdést,
     *           amíg a felhasználó érvényes választ nem ad. Ez biztosítja, hogy a program
     *           ne lépjen tovább érvénytelen bemenet esetén.
     */
    private void offerSaveGame() {
        while (true) {
            System.out.print("\nSzeretnéd menteni a játékállást? (i/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("i") || response.equals("igen")) {
                try {
                    gameLoader.saveGame(
                            gameService.getBoard(),
                            gameService.getCurrentPlayer(),
                            gameService.getGameMode()
                    );
                    System.out.println("Játékállás sikeresen mentve!");
                } catch (Exception e) {
                    System.out.println("Hiba a mentés során: " + e.getMessage());
                    LoggerService.warning("Játék mentési hiba: " + e.getMessage());
                }
                return;
            } else if (response.equals("n") || response.equals("nem")) {
                System.out.println("Kilépés mentés nélkül.");
                return;
            } else {
                System.out.println("Érvénytelen válasz! Kérlek írj 'i' (igen) vagy 'n' (nem)!");
            }
        }
    }

    /**
     * A játék fő ciklusa, amely kezeli a játék menetét.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Megjeleníti a játék aktuális állapotát (tábla, játékos, játékmód, állapot)
     * - Ellenőrzi, hogy véget ért-e a játék (győzelem vagy döntetlen)
     * - Bekéri a felhasználó lépését vagy a mentési/kilépési opciókat
     * - Feldolgozza a felhasználó inputját (lépés, mentés, kilépés)
     * - AI játékos esetén automatikusan végrehajtja az AI lépését
     * - A játék végén kezeli a győzelem/döntetlen eseményét és kínál új játékot
     * <p>
     * A ciklus a következő lépésekből áll:
     * 1. Játékállapot kiírása (tábla és információk)
     * 2. Játékállapot ellenőrzése (ha nem folyamatban, kilép)
     * 3. Felhasználói input bekérése
     * 4. Input feldolgozása:
     *    - Üres input: újrakezdés
     *    - "m" vagy "mentés": játék mentése
     *    - "k" vagy "kilépés": kilépés a játékból (mentés lehetőségével)
     *    - Egyéb: lépés feldolgozása
     * 5. Sikeres lépés után, ha a következő játékos AI, akkor AI lépés végrehajtása
     *
     * @see #processMoveInput(String) A lépés feldolgozását végző metódus
     * @see #offerSaveGame() A mentést felajánló metódus
     * @see #handleGameEnd() A játék végének kezelését végző metódus
     * @see #offerNewGame() Az új játék indítását felajánló metódus
     *
     * @implNote A ciklus addig fut, amíg a játék állapota `IN_PROGRESS`. A felhasználói input
     *           feldolgozása után a metódus ellenőrzi, hogy a játékos AI-e, és ha igen, akkor
     *           azonnal végrehajtja az AI lépését. Ez biztosítja, hogy az AI mindig az emberi
     *           játékos lépése után azonnal lépjen.
     */
    private void gameLoop() {
        while (true) {
            gameService.printGameState();

            // Játék állapot ellenőrzése
            if (gameService.getGameState() != GameState.IN_PROGRESS) {
                break;
            }

            String playerName = gameService.getCurrentPlayer().getName();
            char playerSymbol = gameService.getCurrentPlayer().getSymbol();

            // Egy prompt mindenre: lépés VAGY opció
            System.out.print("\n" + playerName + " (" + playerSymbol + ") lépése: " +
                    "[sor oszlop] vagy [m]entés vagy [k]ilépés: ");

            String input = scanner.nextLine().trim().toLowerCase();

            // Üres bemenet - újra kérjük
            switch (input) {
                case "" -> {
                    System.out.println("Kérlek adj meg egy értéket!");
                    continue;
                }

                // Opciók kezelése
                case "m", "mentés" -> {
                    try {
                        gameLoader.saveGame(
                                gameService.getBoard(),
                                gameService.getCurrentPlayer(),
                                gameService.getGameMode()
                        );
                        System.out.println("Játékállás sikeresen mentve!");
                    } catch (Exception e) {
                        System.out.println("Hiba a mentés során: " + e.getMessage());
                    }
                    continue;
                }
                case "k", "kilépés" -> {
                    offerSaveGame();
                    return;
                }
                default -> {
                    // Alapértelmezett eset: lépés feldolgozása
                }
            }

            // Lépés feldolgozása
            boolean moveSuccessful = processMoveInput(input);

            if (moveSuccessful && !gameService.getCurrentPlayer().isHuman()) {
                // Ha emberi játékos sikeresen lépett, és a következő AI, akkor AI lép
                gameService.makeAIMove();
            }
        }
        handleGameEnd();
        offerNewGame();
    }

    /**
     * Kezeli a játék végét és végrehajtja a végeredmény alapján szükséges műveleteket.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Kiírja a játék végeredményét (győzelem vagy döntetlen)
     * - Pontozza a győztes játékosokat (10 pont győzelemért, 5 pont döntetlenért)
     * - Elmenti a pontszámokat az adatbázisba a ranglista számára
     * - Megjeleníti a játékosok frissített pontszámait
     * - Differenciált kezelést biztosít a különböző játék végeredmények esetén
     * <p>
     * A pontozási rendszer:
     * - Győzelem: 10 pont a győztes játékosnak
     * - Döntetlen: 5 pont mindkét emberi játékosnak
     * - Játék megszakítása: nincs pontozás
     *
     * @see DatabaseService#saveScore(String, int) A pontszám mentését végző metódus
     * @see HumanPlayer#addScore(int) A pontszám növelését végző metódus
     *
     * @implNote A metódus csak HumanPlayer típusú játékosok számára ad pontokat,
     *           mivel az AI játékosoknak nincs pontszámuk. A pontozás automatikusan
     *           megtörténik a játék végén, a felhasználónak nem kell külön kérnie.
     *
     * @implSpec A metódus feltételezi, hogy a GameService-ben a player1 és player2
     *           megfelelően inicializálva vannak, és hogy a játék végeredménye
     *           helyesen beállításra került a GameState enum segítségével.
     *
     * @example
     * // PLAYER_X_WON esetén:
     * // "Győzelem! Kati nyert!"
     * // "Pontszám: 25"
     * <p>
     * // PLAYER_O_WON esetén:
     * // "Győzelem! AI nyert!"
     * // (nincs pontszám, mert AI nem kap pontot)
     * <p>
     * // DRAW esetén:
     * // "Döntetlen!"
     * // "Pontszám (Kati): 15"
     * // "Pontszám (János): 10"
     */
    private void handleGameEnd() {
        GameState finalState = gameService.getGameState();
        System.out.println("\n=== Játék vége ===");

        switch (finalState) {
            case PLAYER_X_WON:
                Player winnerX = gameService.getPlayer1();

                if (winnerX instanceof HumanPlayer humanPlayer) {
                    // Ember nyert
                    System.out.println("Győzelem! " + humanPlayer.getName() + " nyert!");
                    humanPlayer.addScore(10);
                    databaseService.saveScore(humanPlayer.getName(), 10);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
                } else {
                    // AI nyert
                    System.out.println("Vereség! " + winnerX.getName() + " nyert!");
                }
                break;

            case PLAYER_O_WON:
                Player winnerO = gameService.getPlayer2();

                if (winnerO instanceof HumanPlayer humanPlayer) {
                    // Ember nyert
                    System.out.println("Győzelem! " + humanPlayer.getName() + " nyert!");
                    humanPlayer.addScore(10);
                    databaseService.saveScore(humanPlayer.getName(), 10);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
                } else {
                    // AI nyert
                    System.out.println("Vereség! " + winnerO.getName() + " nyert!");
                }
                break;

            case DRAW:
                System.out.println("Döntetlen!");
                if (gameService.getPlayer1() instanceof HumanPlayer humanPlayer1) {
                    humanPlayer1.addScore(5);
                    databaseService.saveScore(humanPlayer1.getName(), 5);
                    System.out.println("Pontszám (" + humanPlayer1.getName() + "): " + humanPlayer1.getScore());
                }
                if (gameService.getPlayer2() instanceof HumanPlayer humanPlayer2) {
                    humanPlayer2.addScore(5);
                    databaseService.saveScore(humanPlayer2.getName(), 5);
                    System.out.println("Pontszám (" + humanPlayer2.getName() + "): " + humanPlayer2.getScore());
                }
                break;

            default:
                System.out.println("Játék megszakítva.");
        }
    }

    /**
     * Felajánlja a felhasználónak egy új játék indításának lehetőségét a jelenlegi játék végén.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - Interaktív párbeszédet kezd a felhasználóval egy új játék indításáról
     * - Feldolgozza a felhasználó válaszát (igen/nem vagy rövidítések)
     * - Ha a felhasználó igennel válaszol, új GameController példányt hoz létre és indít
     * - Ha a felhasználó nemmel válaszol, köszönő üzenettel zárul a játék
     * - Biztosítja a zökkenőmentes átmenetet az aktuális játékból egy új játékba vagy a kilépésbe
     * <p>
     * A metódus a következő válaszokat fogadja el:
     * - Igen: "i", "igen" (kis/nagybetű érzéketlen)
     * - Nem: "n", "nem" (kis/nagybetű érzéketlen)
     *
     * @see GameController#run() A játék fő futtatási ciklusát végző metódus
     * @see GameController#GameController() Az új játékvezérlő konstruktora
     *
     * @implNote A metódus egy egyszerű kérdés-felelet alapú interakciót valósít meg.
     *           Új játék indításakor a rendszer létrehoz egy teljesen új GameController
     *           példányt, ami biztosítja a teljes állapot resetelését és a tiszta kezdést.
     *           Ez a megoldás előnyösebb, mint az aktuális controller állapotának
     *           alaphelyzetbe állítása, mivel elkerüli a maradék állapotproblémákat.
     *
     * @implSpec A metódus nem végez komplex érvényesítést a felhasználói bemeneten,
     *           csupán az alapvető igen/nem válaszokat fogadja el. Érvénytelen válasz
     *           esetén a metódus nem kérdezi újra, hanem kilép a játékból.
     *
     * @example
     * // Példa interakció:
     * // "Szeretnél új játékot kezdeni? (i/n): i"
     * // Új játék indítása... (a GameController.run() meghívódik)
     * <p>
     * // Vagy:
     * // "Szeretnél új játékot kezdeni? (i/n): n"
     * // "Köszönöm, hogy játszottál!"
     * <p>
     * // Vagy érvénytelen válasz esetén:
     * // "Szeretnél új játékot kezdeni? (i/n): talán"
     * // "Köszönöm, hogy játszottál!" (alapértelmezett nem válasz)
     */
    private void offerNewGame() {
        System.out.print("\nSzeretnél új játékot kezdeni? (i/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("i") || response.equals("igen")) {
            new GameController().run();
        } else {
            System.out.println("Köszönöm, hogy játszottál!");
        }
    }

    /**
     * Játékmód kiválasztását kezeli, lehetőséget biztosítva a főmenübe való visszatérésre.
     *
     * <p>A metódus a következő feladatokat látja el:
     * <ul>
     *   <li>Megjeleníti a választható játékmódokat</li>
     *   <li>Lehetőséget biztosít a "vissza" kulcsszó megadásával a főmenübe való visszatérésre</li>
     *   <li>Ellenőrzi a bemenet érvényességét (1-2 szám vagy "vissza")</li>
     *   <li>Inicializálja a GameService-t a kiválasztott játékmóddal</li>
     * </ul>
     *
     * @return true ha sikeresen kiválasztották a játékmódot, false ha visszaléptek a főmenübe
     * @see GameService
     * @see GameMode
     */
    private boolean selectGameMode() {
        while (true) {
            System.out.println("\n***************************");
            System.out.println("Játékmód kiválasztása:");
            System.out.println("1. " + GameMode.HUMAN_VS_HUMAN.getDisplayName());
            System.out.println("2. " + GameMode.HUMAN_VS_AI.getDisplayName());
            System.out.println("****************************");
            System.out.print("\n---> Kérlek válassz (1-2 vagy 'vissza' a menühöz): ");

            String input = scanner.nextLine().trim();

            // Vissza a főmenübe
            if (input.equalsIgnoreCase("vissza")) {
                System.out.println("Visszalépés a főmenübe...");
                return false;
            }

            try {
                int choice = Integer.parseInt(input);

                if (choice == 1 || choice == 2) {
                    GameMode selectedMode = (choice == 1) ? GameMode.HUMAN_VS_HUMAN : GameMode.HUMAN_VS_AI;
                    this.gameService = new GameService(selectedMode);
                    System.out.println("\nKiválasztva: " + selectedMode.getDisplayName());
                    break;
                } else {
                    System.out.println("HIBA: Csak 1 vagy 2 lehet a választás!");
                    System.out.println("Próbáld újra vagy írd be 'vissza' a főmenübe.");
                }
            } catch (NumberFormatException e) {
                System.out.println("HIBA: Érvénytelen bemenet! Csak 1, 2 vagy 'vissza' fogadható el.");
                System.out.println("Próbáld újra.");
            }
        }
        return true;
    }

    /**
     * Bekéri és beállítja a játékosok neveit a kiválasztott játékmódnak megfelelően.
     * A metódus a következő feladatokat látja el:
     * <p>
     * - A játékmód alapján meghatározza, hány játékos nevét kell bekérni
     * - HUMAN_VS_HUMAN esetén két játékos nevét kéri be
     * - HUMAN_VS_AI esetén csak az emberi játékos nevét kéri be, az AI neve automatikusan "AI" lesz
     * - Meghívja a getPlayerName() segédmetódust a név bekérésére és validálására
     * - Beállítja a játékosok neveit a GameService-ben
     * - Megjeleníti a beállított játékosneveket visszajelzésként
     * <p>
     * A metódus biztosítja, hogy a játékosok nevei megfelelően inicializálva legyenek
     * a játék megkezdése előtt, és hogy a felhasználó lássa, kik fognak játszani.
     *
     * @see #getPlayerName(String) A név bekérését és validálását végző segédmetódus
     * @see GameService#getPlayer1() Az első játékos lekérdezése
     * @see GameService#getPlayer2() A második játékos lekérdezése
     * @see Player#setName(String) A játékos nevének beállítása
     *
     * @implNote A metódus a játékmódtól függően változtatja a felhasználói interfészt:
     *           - HUMAN_VS_HUMAN: két külön prompt két játékosnév bekérésére
     *           - HUMAN_VS_AI: egy prompt az emberi játékos nevének bekérésére
     *           Az AI játékos neve mindig "AI" és nem változtatható.
     *
     * @implSpec A metódus feltételezi, hogy a gameService és a benne lévő játékosok
     *           már inicializálva vannak a megfelelő játékmód szerint. A metódus
     *           csak a neveket állítja be, nem változtatja a játékosok típusát.
     *
     * @example
     * // HUMAN_VS_HUMAN esetén:
     * // "Játékosok:"
     * // "Első játékos neve: Kati"
     * // "Második játékos neve: János"
     * // "Játékosok beállítva:"
     * // "1. játékos: Kati"
     * // "2. játékos: János"
     * <p>
     * // HUMAN_VS_AI esetén:
     * // "Játékos:"
     * // "Add meg a neved: Péter"
     * // "Játékosok beállítva:"
     * // "1. játékos: Péter"
     * // "2. játékos: AI"
     */
    private void getPlayerNames() {

        if (gameService.getGameMode() == GameMode.HUMAN_VS_HUMAN) {
            System.out.println("\nJátékosok:");
        } else if (gameService.getGameMode() == GameMode.HUMAN_VS_AI) {
            System.out.println("\nJátékos:");
        }

        if (gameService.getGameMode() == GameMode.HUMAN_VS_HUMAN) {

            String player1Name = getPlayerName("Első játékos neve");
            gameService.getPlayer1().setName(player1Name);

            String player2Name = getPlayerName("Második játékos neve");
            gameService.getPlayer2().setName(player2Name);

        } else if (gameService.getGameMode() == GameMode.HUMAN_VS_AI) {

            String player1Name = getPlayerName("Add meg a neved");
            gameService.getPlayer1().setName(player1Name);

            gameService.getPlayer2().setName("AI");
        }

        System.out.println("\nJátékosok beállítva:");
        System.out.println("1. játékos: " + gameService.getPlayer1().getName());
        System.out.println("2. játékos: " + gameService.getPlayer2().getName());
        System.out.println();
    }

    private String getPlayerName(String prompt) {
        while (true) {

            System.out.print(prompt + ": ");

            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("A név nem lehet üres! Kérlek adj meg egy nevet.");
                continue;
            }

            if (name.length() > 20) {
                System.out.println("A név túl hosszú! Maximum 20 karakter lehet.");
                continue;
            }

            return name;
        }
    }

    /**
     * Beolvas és validál egy menüpont választást a felhasználótól a megadott tartományban.
     * A metódus addig ismétli a bemenet kérését, amíg a felhasználó érvényes numerikus
     * értéket nem ad meg 1 és maximális értékek között.
     */
    private int getMenuChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= 5) {
                    return choice;
                }
                System.out.print("Kérem adjon meg egy számot " + 1 + " és " + 5 + " között: ");
            } catch (Exception e) {
                System.out.print("Érvénytelen bevitel! Kérem adjon meg egy érvényes számot: ");
                scanner.nextLine();
            }
        }
    }
}