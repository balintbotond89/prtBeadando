//Beadási határidő: december 5

package nye.flocrm.progtech.ui;

import nye.flocrm.progtech.service.GameService;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.GameState;

import java.util.Scanner;

/**
 * Az amőba játék fő alkalmazás osztálya.
 */

@SuppressWarnings({"PMD.SystemPrintln"})
public class Main {

    private GameService gameService;
    private final Scanner scanner;

    public Main() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    //run
    public void run() {
        System.out.println("Isten hozott az amőba játékban!");
        System.out.println("Rakj le 5 jelet egy sorban a győzelemhez!\n");

        //selectGameMode();
        //gameLoop();

        scanner.close();
    }

    //selectGameMode

    //gameLoop

    //displayGameResult

    //offerNewGame

    //Select game mode

    /**
     * Felhasználói menü választást olvas be és érvényesít a konzolról.
     * A metódus egy egyszerű menürendszert valosít meg, ahol
     * a felhasználónak egy előre meghatározott tartományból kell választania.
     * A metódus addig kér be újabb inputot, amíg a felhasználó érvényes számot nem ad meg
     * a megadott határok között.
     */
    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // puffer űrítése
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.print("Kérem adjon meg egy számot a  " + min + " és " + max + "között: ");
            } catch (Exception e) {
                System.out.print("Érvénytelen bevitel! Kérem adjon meg egy érvényes számot: ");
                scanner.nextLine(); // Helytelen bemenet elvetése
            }
        }
    }
}