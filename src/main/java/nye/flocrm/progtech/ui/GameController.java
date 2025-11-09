package nye.flocrm.progtech.ui;

import java.util.Scanner;
import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.GameState;
import nye.flocrm.progtech.model.HumanPlayer;
import nye.flocrm.progtech.service.GameService;
import nye.flocrm.progtech.service.GameLoader;
import nye.flocrm.progtech.service.LoggerService;

/**
 * A játék vezérléséért felelős osztály.
 * Kezeli a felhasználói interfészt és a játék folyamatát.
 */
public class GameController {
    private GameService gameService;
    private final Scanner scanner;
    private final GameLoader gameLoader; // final javítás

    public GameController() {
        this.scanner = new Scanner(System.in);
        this.gameLoader = new GameLoader();
    }

    public void run() {
        System.out.println("Isten hozott az amőba játékban!");
        System.out.println("Rakj le 5 jelet egy sorban a győzelemhez!\n");

        showMainMenu();

        scanner.close();
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== Főmenü ===");
            System.out.println("1. Új játék");
            System.out.println("2. Játék betöltése");

            if (gameLoader.saveFileExists()) {
                System.out.println("3. Mentett állás visszatöltése");
            }

            System.out.println("4. Kilépés");
            System.out.print("Válassz opciót: ");

            int choice = getMenuChoice(4);

            switch (choice) {
                case 1:
                    startNewGame();
                    return;
                case 2:
                    loadGame();
                    return;
                case 3:
                    if (gameLoader.saveFileExists()) {
                        loadSavedGame();
                        return;
                    }
                    break;
                case 4:
                    System.out.println("Kilépés...");
                    return;
                default:
                    System.out.println("Érvénytelen választás!");
            }
        }
    }

    private void startNewGame() {
        selectGameMode();
        getPlayerNames();
        gameLoop();
    }

    private void loadGame() {
        System.out.print("Add meg a betöltendő fájl nevét: ");
        String filename = scanner.nextLine().trim(); // változó használata
        System.out.println("Fájl: " + filename); // változó használata

        System.out.println("Ez a funkció jelenleg fejlesztés alatt áll.");
        System.out.println("Új játékot indítok helyette...");
        startNewGame();
    }

    /**
     * Mentett állás visszatöltése
     */
    private void loadSavedGame() {
        try {
            GameLoader.GameState gameState = gameLoader.loadGame();

            // GameService létrehozása a mentett játékmóddal
            this.gameService = new GameService(gameState.gameMode());

            // Tábla másolása
            copyBoard(gameState.board(), gameService.getBoard());

            // Csak a játékos nevének és pontszámának másolása
            if (gameState.player() instanceof HumanPlayer savedPlayer) { // pattern variable
                HumanPlayer currentPlayer = (HumanPlayer) gameService.getPlayer1();

                currentPlayer.setName(savedPlayer.getName());
                currentPlayer.setScore(savedPlayer.getScore());
            }

            // WinChecker állapot frissítése
            gameService.checkForWinner();

            System.out.println("Sikeresen betöltötted " + gameState.player().getName() +
                    " játékát (" + gameState.timestamp() + ")");
            System.out.println("Játékmód: " + gameState.gameMode().getDisplayName());

            // Pontszám kiírása - pattern variable használata
            if (gameState.player() instanceof HumanPlayer humanPlayer) {
                System.out.println("Pontszám: " + humanPlayer.getScore());
            }

            // Játék folytatása
            gameLoop();

        } catch (Exception e) {
            System.out.println("Hiba a mentett állás betöltésekor: " + e.getMessage());
            LoggerService.warning("Mentett játék betöltési hiba: " + e.getMessage()); // LoggerService javítás
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

    /**
     * Módosított játék ciklus mentési lehetőséggel
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
     * Játék vége kezelése
     */
    private void handleGameEnd() {
        GameState finalState = gameService.getGameState();
        System.out.println("\n=== Játék vége ===");

        switch (finalState) {
            case PLAYER_X_WON:
                System.out.println("GYŐZELEM! " + gameService.getPlayer1().getName() + " nyert!");
                if (gameService.getPlayer1() instanceof HumanPlayer humanPlayer) {
                    humanPlayer.addScore(10);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
                }
                break;
            case PLAYER_O_WON:
                System.out.println("GYŐZELEM! " + gameService.getPlayer2().getName() + " nyert!");
                if (gameService.getPlayer2() instanceof HumanPlayer humanPlayer) {
                    humanPlayer.addScore(10);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
                }
                break;
            case DRAW:
                System.out.println("DÖNTETLEN!");
                if (gameService.getPlayer1() instanceof HumanPlayer humanPlayer) {
                    humanPlayer.addScore(5);
                    System.out.println("Pontszám: " + humanPlayer.getScore());
                }
                break;
            default:
                System.out.println("Játék megszakítva.");
        }
    }

    /**
     * Felajánlja a felhasználónak egy új játék indításának lehetőségét.
     */
    private void offerNewGame() {
        while (true) {
            System.out.print("\nSzeretnél új játékot kezdeni? (i/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("i") || response.equals("igen")) {
                new GameController().run();
                return;
            } else if (response.equals("n") || response.equals("nem")) {
                System.out.println("Köszönöm, hogy játszottál!");
                return;
            } else {
                System.out.println("Érvénytelen válasz! Kérlek írj 'i' (igen) vagy 'n' (nem) -et!");
            }
        }
    }

    private void selectGameMode() {
        System.out.println("Játékmód kiválasztása:");
        System.out.println("1. " + GameMode.HUMAN_VS_HUMAN.getDisplayName());
        System.out.println("2. " + GameMode.HUMAN_VS_AI.getDisplayName());
        System.out.print("Kérlek válassz (1-2): ");

        int choice = getMenuChoice(2);
        GameMode selectedMode = (choice == 1) ? GameMode.HUMAN_VS_HUMAN : GameMode.HUMAN_VS_AI;

        this.gameService = new GameService(selectedMode);
        System.out.println("\nKiválasztva: " + selectedMode.getDisplayName());
    }

    private void getPlayerNames() {
        System.out.println("\nJátékosok: ");
        String player1Name = getPlayerName("első");
        gameService.getPlayer1().setName(player1Name);

        if (gameService.getGameMode() == GameMode.HUMAN_VS_HUMAN) {
            String player2Name = getPlayerName("második");
            gameService.getPlayer2().setName(player2Name);
        }

        System.out.println("\nJátékosok beállítva:");
        System.out.println("1. játékos: " + gameService.getPlayer1().getName());
        System.out.println("2. játékos: " + gameService.getPlayer2().getName());
        System.out.println();
    }

    private String getPlayerName(String playerNumber) {
        while (true) {
            System.out.print(playerNumber + " játékos neve: ");
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
 * értéket nem ad meg a megadott minimális és maximális értékek között.
 */
    private int getMenuChoice(int max) {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= max) {
                    return choice;
                }
                System.out.print("Kérem adjon meg egy számot " + 1 + " és " + max + " között: ");
            } catch (Exception e) {
                System.out.print("Érvénytelen bevitel! Kérem adjon meg egy érvényes számot: ");
                scanner.nextLine();
            }
        }
    }
}