package nye.flocrm.progtech.ui;

import java.util.Scanner;
import java.io.File;
import java.util.List;
import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.GameState;
import nye.flocrm.progtech.model.HumanPlayer;
import nye.flocrm.progtech.service.GameService;
import nye.flocrm.progtech.service.GameLoader;
import nye.flocrm.progtech.service.LoggerService;
import nye.flocrm.progtech.service.DatabaseService;

/**
 * A játék vezérléséért felelős osztály.
 * Kezeli a felhasználói interfészt és a játék folyamatát.
 */
public class GameController {
    private GameService gameService;
    private final Scanner scanner;
    private final GameLoader gameLoader;
    private final DatabaseService databaseService;

    public GameController() {
        this.scanner = new Scanner(System.in);
        this.gameLoader = new GameLoader();
        this.databaseService = new DatabaseService();

        // Kapcsolat ellenőrzése az adatbázissal:
        if (databaseService.isConnectionAvailable()) {
            System.out.println("Figyelmeztetés: Nem sikerült csatlakozni az adatbázishoz!");
        }
    }

    public void run() {
        System.out.println("Isten hozott az amőba játékban!");
        System.out.println("Rakj le 5 jelet egy sorban a győzelemhez!\n");

        showMainMenu();

        scanner.close();
    }

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

            LoggerService.info("Amőba játék indítása...");

            // Játékos nevek bekérése
            getPlayerNames();

            // Játék indítása
            gameLoop();

            LoggerService.info("Amőba játék leállítva.");

        } catch (Exception e) {
            LoggerService.severe("Váratlan hiba történt a játék indítása során", e);
            System.err.println("Váratlan hiba történt. További részletek a naplófájlban.");
        }
    }

    /**
     * Ranglista megjelenítése
     */
    private void showRanking() {
        System.out.println("\n******* Ranglista ********");

        System.out.println("A mindenkori legjobb öt játékos és összpontszáma: ");

        if (databaseService.isConnectionAvailable()) {
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
     * Játék állapot betöltése GameState objektumból
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
     * Játék állapot információinak kiírása
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
     * Mentett állás visszatöltése
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
     * Tábla másolása a mentett állapotból
     */
    private void copyBoard(Board source, Board target) {
        // Először töröljük a céltáblát
        target.clear();

        // Másoljuk át az összes mezőt
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
     * Lépés feldolgozása a megadott inputból
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
     * Játékmenet közbeni mentés lehetőség
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
     * Játék végének kezelése - pontok mentése adatbázisba
     */
    private void handleGameEnd() {
        GameState finalState = gameService.getGameState();
        System.out.println("\n=== Játék vége ===");

        switch (finalState) {
            case PLAYER_X_WON:
                System.out.println("Győzelem! " + gameService.getPlayer1().getName() + " nyert!");
                if (gameService.getPlayer1() instanceof HumanPlayer humanPlayer) {
                    humanPlayer.addScore(10);
                    databaseService.saveScore(humanPlayer.getName(), 10);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
                }
                break;
            case PLAYER_O_WON:
                System.out.println("Győzelem! " + gameService.getPlayer2().getName() + " nyert!");
                if (gameService.getPlayer2() instanceof HumanPlayer humanPlayer) {
                    humanPlayer.addScore(10);
                    databaseService.saveScore(humanPlayer.getName(), 10);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
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