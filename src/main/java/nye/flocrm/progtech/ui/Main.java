//Beadási határidő: december 5

package nye.flocrm.progtech.ui;

import nye.flocrm.progtech.service.LoggerService;

/**
 * Fő osztály az alkalmazás indításához.
 */
@SuppressWarnings({"PMD.SystemPrintln"})
public class Main {
    private Main() {
        // utility osztály - nem példányosítható
    }

    /**
     * Belépési pont az alkalmazáshoz.
     * Inicializálja és elindítja a játékot.
     */
    public static void main(String[] args) {
        try {
            LoggerService.info("Amőba játék indítása...");

            GameController controller = new GameController();
            controller.run();

            LoggerService.info("Amőba játék leállítva.");

        } catch (Exception e) {
            LoggerService.severe("Váratlan hiba történt az alkalmazás indítása során", e);
            System.err.println("Váratlan hiba történt. További részletek a naplófájlban.");
        }
    }
}