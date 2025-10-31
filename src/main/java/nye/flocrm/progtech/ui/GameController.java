package nye.flocrm.progtech.ui;

import java.util.Scanner;

import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.GameState;
import nye.flocrm.progtech.service.GameService;
import nye.flocrm.progtech.service.LoggerService;

/**
 * A játék vezérléséért felelős osztály.
 * Kezeli a felhasználói interfészt és a játék folyamatát.
 */
public class GameController {
    private GameService gameService;
    private final Scanner scanner;

    public GameController() {
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Isten hozott az amőba játékban!");
        System.out.println("Rakj le 5 jelet egy sorban a győzelemhez!\n");

        selectGameMode(); // Játékmód kiválasztása
        getPlayerNames(); // Játékos nevek bekérése
        gameLoop(); //Játék vezérlő

        scanner.close();
    }

    private void selectGameMode() {
        System.out.println("Játékmód kiválasztása:");
        System.out.println("1. " + GameMode.HUMAN_VS_HUMAN.getDisplayName());
        System.out.println("2. " + GameMode.HUMAN_VS_AI.getDisplayName());
        System.out.print("Kérlek válassz (1-2): ");

        int choice = getMenuChoice(1, 2);
        GameMode selectedMode = (choice == 1) ? GameMode.HUMAN_VS_HUMAN : GameMode.HUMAN_VS_AI;

        this.gameService = new GameService(selectedMode);
        System.out.println("\nKiválasztva: " + selectedMode.getDisplayName());
    }

    /**
     * Játékos nevek bekérése a kiválasztott játékmód alapján
     */
    private void getPlayerNames() {
        System.out.println("\nJátékosok: ");

        // Első játékos neve (mindig emberi játékos)
        String player1Name = getPlayerName("első");
        gameService.getPlayer1().setName(player1Name);

        // Második játékos neve csak HUMAN_VS_HUMAN módban kérjük be
        if (gameService.getGameMode() == GameMode.HUMAN_VS_HUMAN) {
            String player2Name = getPlayerName("második");
            gameService.getPlayer2().setName(player2Name);  // MÓDOSÍTÁS: itt is setName
        }

        System.out.println("\nJátékosok beállítva:");
        System.out.println("1. játékos: " + gameService.getPlayer1().getName());
        System.out.println("2. játékos: " + gameService.getPlayer2().getName());
        System.out.println();
    }

    /**
     * Egy játékos nevének bekérése
     */
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
     * A játék fő ciklusát vezérli, amely a játékmenet magja.
     */
    private void gameLoop() {
        while (true) {
            gameService.printGameState();

            if (gameService.getGameState() != GameState.IN_PROGRESS) {
                break;
            }

            if (gameService.getCurrentPlayer().isHuman()) {
                handleHumanMove();
            }
        }
        offerNewGame();
    }

    /**
     * Az emberi játékos lépésének kezelését végzi.
     */
    private void handleHumanMove() {
        String playerName = gameService.getCurrentPlayer().getName();
        char playerSymbol = gameService.getCurrentPlayer().getSymbol();

        boolean validMove = false;

        while (!validMove) {

            System.out.print("Lép: " + playerName +
                    " (" + playerSymbol +
                    "), add meg a lépésed (sor oszlop): ");

            try {
                // Egy sor beolvasása és feldolgozása
                String inputLine = scanner.nextLine().trim();

                // Üres bemenet ellenőrzése
                if (inputLine.isEmpty()) {
                    System.out.println("Üres bemenet! Kérlek adj meg két számot (pl: 3 5).");
                    System.out.println("Nyomj Entert az újrapróbálkozáshoz...");
                    continue;
                }

                // Szóközzel elválasztva részekre bontás
                String[] parts = inputLine.split("\\s+");

                // Pontosan két szám ellenőrzése
                if (parts.length != 2) {
                    System.out.println("Hiányos bemenet! Két számot kell megadni szóközzel elválasztva (pl: 3 5).");
                    System.out.println("Megadott: '" + inputLine + "' (" + parts.length + " rész)");
                    System.out.println("Nyomj Entert az újrapróbálkozáshoz...");
                    continue;
                }

                // Számok konvertálása
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;

                // Tartomány ellenőrzése
                if (row < 0 || row >= Board.SIZE || col < 0 || col >= Board.SIZE) {
                    System.out.println("Érvénytelen tartomány! Csak 1 és " + Board.SIZE + " közötti számokat adj meg.");
                    System.out.println("Megadott: " + (row + 1) + " " + (col + 1));
                    System.out.println("Nyomj Entert az újrapróbálkozáshoz...");
                    continue;
                }

                // Lépés végrehajtása
                if (gameService.makeMove(row, col)) {
                    validMove = true; // Sikeres lépés, kilépünk a ciklusból
                } else {
                    System.out.println("Érvénytelen lépés! A mező már foglalt!");
                    System.out.println("Nyomj Entert az újrapróbálkozáshoz...");
                }

            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen bevitel! Csak számokat adhatsz meg (1-" + Board.SIZE + ").");
                System.out.println("Példa: '3 5' vagy '10 2'");
                System.out.println("Nyomj Entert az újrapróbálkozáshoz...");
            } catch (Exception e) {
                System.out.println("Váratlan hiba történt. Próbáld újra.");
                System.out.println("Nyomj Entert az újrapróbálkozáshoz...");
                LoggerService.warning("Váratlan hiba handleHumanMove-ban: " + e.getMessage());
            }
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

    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.print("Kérem adjon meg egy számot " + min + " és " + max + " között: ");
            } catch (Exception e) {
                System.out.print("Érvénytelen bevitel! Kérem adjon meg egy érvényes számot: ");
                scanner.nextLine();
            }
        }
    }
}