package nye.flocrm.progtech.ui;

import nye.flocrm.progtech.service.GameService;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.GameState;

import java.util.Scanner;

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

        selectGameMode();
        gameLoop();

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
        displayGameResult();
        offerNewGame();
    }

    private void handleHumanMove() {
        System.out.print("Lép: " + gameService.getCurrentPlayer().getName() +
                " (" + gameService.getCurrentPlayer().getSymbol() +
                "), add meg a lépésed (sor oszlop): ");

        try {
            int row = scanner.nextInt() - 1;
            int col = scanner.nextInt() - 1;
            scanner.nextLine();

            if (!gameService.makeMove(row, col)) {
                System.out.println("Érvénytelen lépes! Próbáld újra.");
            }
        } catch (Exception e) {
            System.out.println("Érvénytelen bevitel! Csak számokat adhatsz meg.");
            scanner.nextLine();
        }
    }

    private void displayGameResult() {
        // Implementáld a játék eredmény megjelenítését
        GameState state = gameService.getGameState();
        if (state == GameState.PLAYER_X_WON) {
            System.out.println("X játékos nyert!");
        } else if (state == GameState.PLAYER_O_WON) {
            System.out.println("O játékos nyert!");
        } else {
            System.out.println("Döntetlen!");
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