package nye.flocrm.progtech.service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logolásért felelős service osztály.
 * Átfogó logolási megoldást biztosít az alkalmazás számára.
 */
public final class LoggerService {

    private static final Logger LOGGER = Logger.getLogger("Amőba játék");

    private LoggerService() {
        // utility osztály - nem példányosítható
    }

    /**
     * Súlyos hibák logolása.
     */
    public static void severe(String message, Throwable throwable) {
        if (throwable != null) {
            LOGGER.log(Level.SEVERE, message, throwable);
        } else {
            LOGGER.severe(message);
        }
    }

    /**
     * Figyelmeztetések logolása.
     */
    public static void warning(String message) {
        LOGGER.warning(message);
    }

    /**
     * Információs üzenetek logolása.
     */
    public static void info(String message) {
        LOGGER.info(message);
    }
}