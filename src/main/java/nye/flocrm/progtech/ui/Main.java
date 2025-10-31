//Beadási határidő: december 5

package nye.flocrm.progtech.ui;

import nye.flocrm.progtech.service.LoggerService;
/**
 * Az amőba játék fő alkalmazás osztálya.
 * Csak az alkalmazás indításáért felelős.
 */
@SuppressWarnings({"PMD.SystemPrintln"})
public class Main {
    private Main() {
        // utility osztály - nem példányosítható
    }

    /**
     * Belépési pont az alkalmazáshoz.
     * Inicializálja és elindítja a játékot.
     *
     * @param args parancssori argumentumok
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