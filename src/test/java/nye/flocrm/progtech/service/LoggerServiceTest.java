package nye.flocrm.progtech.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerServiceTest {

    @Test
    @DisplayName("Az info() metódus nem dob kivételt normál üzenettel")
    void testInfoWithNormalMessage() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.info("Normál információs üzenet"));
    }

    @Test
    @DisplayName("Az info() metódus nem dob kivétel üres üzenettel")
    void testInfoWithEmptyMessage() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.info(""));
    }

    @Test
    @DisplayName("Az info() metódus nem dob kivétel null üzenettel")
    void testInfoWithNullMessage() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.info(null));
    }

    @Test
    @DisplayName("Az info() metódus nem dob kivétel speciális karakterekkel")
    void testInfoWithSpecialCharacters() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.info("Speciális karakterek: áéíóöőúüű"));
        assertDoesNotThrow(() -> LoggerService.info("Új sorral \n és tabulátorral \t"));
        assertDoesNotThrow(() -> LoggerService.info("Escape karakterek: \\ \" '"));
    }

    @Test
    @DisplayName("A warning() metódus nem dob kivételt normál üzenettel")
    void testWarningWithNormalMessage() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.warning("Normál figyelmeztető üzenet"));
    }

    @Test
    @DisplayName("A warning() metódus nem dob kivétel üres üzenettel")
    void testWarningWithEmptyMessage() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.warning(""));
    }

    @Test
    @DisplayName("A warning() metódus nem dob kivétel null üzenettel")
    void testWarningWithNullMessage() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.warning(null));
    }

    @Test
    @DisplayName("A warning() metódus nem dob kivétel hosszú üzenettel")
    void testWarningWithLongMessage() {
        // AMIKOR - nagyon hosszú üzenet
        String longMessage = "X".repeat(1000);

        // AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.warning(longMessage));
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt normál üzenettel és kivétellel")
    void testSevereWithNormalMessageAndException() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() ->
                LoggerService.severe("Normál súlyos hiba üzenet", new RuntimeException("Teszt kivétel"))
        );
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt normál üzenettel és null kivétellel")
    void testSevereWithNormalMessageAndNullException() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.severe("Normál súlyos hiba üzenet", null));
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt üres üzenettel és kivétellel")
    void testSevereWithEmptyMessageAndException() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.severe("", new Exception("Teszt")));
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt null üzenettel és kivétellel")
    void testSevereWithNullMessageAndException() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.severe(null, new Exception("Teszt")));
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt null üzenettel és null kivétellel")
    void testSevereWithNullMessageAndNullException() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> LoggerService.severe(null, null));
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt különböző típusú kivételekkel")
    void testSevereWithDifferentExceptionTypes() {
        // AMIKOR & AKKOR - nem dob kivételt különböző kivétel típusokkal
        assertDoesNotThrow(() ->
                LoggerService.severe("NullPointerException", new NullPointerException())
        );
        assertDoesNotThrow(() ->
                LoggerService.severe("IllegalArgumentException", new IllegalArgumentException("Érvénytelen argumentum"))
        );
        assertDoesNotThrow(() ->
                LoggerService.severe("IOException", new java.io.IOException("IO hiba"))
        );
    }

    @Test
    @DisplayName("A severe() metódus nem dob kivételt speciális karakterekkel")
    void testSevereWithSpecialCharacters() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() ->
                LoggerService.severe("Speciális: áéíóöőúüű \n \t \\", new Exception("Kivétel üzenet"))
        );
    }

    @Test
    @DisplayName("A LoggerService metódusai nem befolyásolják egymást")
    void testLoggerMethodsDoNotInterfere() {
        // AMIKOR - több metódus hívása egymás után
        // AKKOR - nem dob kivételt és nem befolyásolják egymást
        assertDoesNotThrow(() -> {
            LoggerService.info("Első info üzenet");
            LoggerService.warning("Figyelmeztetés között");
            LoggerService.severe("Súlyos hiba", new Exception("Teszt"));
            LoggerService.info("Második info üzenet");
        });
    }

    @Test
    @DisplayName("A LoggerService kezeli a szálbiztonságot")
    void testLoggerServiceThreadSafety() {
        // AMIKOR - több szálból hívjuk a logger metódusokat
        // AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> {
            Thread thread1 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    LoggerService.info("Szál 1 - Info " + i);
                }
            });

            Thread thread2 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    LoggerService.warning("Szál 2 - Warning " + i);
                }
            });

            Thread thread3 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    LoggerService.severe("Szál 3 - Severe " + i, new Exception("Szál 3 kivétel"));
                }
            });

            thread1.start();
            thread2.start();
            thread3.start();

            thread1.join();
            thread2.join();
            thread3.join();
        });
    }

    @Test
    @DisplayName("A LoggerService logger példánya nem null")
    void testLoggerInstanceNotNull() throws Exception {
        // AMIKOR - a logger példányát lekérjük reflexióval
        var loggerField = LoggerService.class.getDeclaredField("LOGGER");
        loggerField.setAccessible(true);
        Logger logger = (Logger) loggerField.get(null);

        // AKKOR - a logger nem lehet null
        assertNotNull(logger, "A logger példány nem lehet null");
    }

    @Test
    @DisplayName("A LoggerService logolási szintje megfelelő")
    void testLoggerLevel() throws Exception {
        // AMIKOR - a logger példányát lekérjük reflexióval
        var loggerField = LoggerService.class.getDeclaredField("LOGGER");
        loggerField.setAccessible(true);
        Logger logger = (Logger) loggerField.get(null);

        // AKKOR - a logger szintje megfelelő (általában INFO vagy magasabb)
        assertTrue(logger.isLoggable(Level.INFO), "A logger-nek képesnek kell lennie INFO szintű üzenetek logolására");
        assertTrue(logger.isLoggable(Level.WARNING), "A logger-nek képesnek kell lennie WARNING szintű üzenetek logolására");
        assertTrue(logger.isLoggable(Level.SEVERE), "A logger-nek képesnek kell lennie SEVERE szintű üzenetek logolására");
    }
}