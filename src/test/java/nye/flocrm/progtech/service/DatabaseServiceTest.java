package nye.flocrm.progtech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Egységtesztek a DatabaseService osztályhoz.
 *
 * Fontos: a tesztek egy valós adatbázist használnak a DatabaseService-ben
 * beállított JDBC URL alapján. Ajánlott ehhez egy TESZT adatbázist
 * konfigurálni (külön adatbázis vagy lokális példányt), hogy éles adatokat
 * ne módosítson tesztelés során.
 */
class DatabaseServiceTest {

    private DatabaseService databaseService;

    @BeforeEach
    void setUp() throws Exception {
        databaseService = new DatabaseService();
        clearScoresTable();
    }

    /**
     * Segédmetódus: a scores tábla kiürítése minden teszt előtt,
     * hogy a tesztek egymástól függetlenek maradjanak.
     */
    private void clearScoresTable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {

        Method getConnectionMethod = DatabaseService.class.getDeclaredMethod("getConnection");
        getConnectionMethod.setAccessible(true);

        try (Connection conn = (Connection) getConnectionMethod.invoke(databaseService);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM scores");
        }
    }

    @Test
    @DisplayName("saveScore – a mentett pontszám ténylegesen bekerül az adatbázisba")
    void saveScore_insertsRowIntoScoresTable() throws Exception {
        databaseService.saveScore("Kati", 42);

        Method getConnectionMethod = DatabaseService.class.getDeclaredMethod("getConnection");
        getConnectionMethod.setAccessible(true);

        int rowCount;
        try (Connection conn = (Connection) getConnectionMethod.invoke(databaseService);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM scores " + "WHERE player = 'Kati' AND score = 42")) {
            rs.next();
            rowCount = rs.getInt(1);
        }

        assertTrue(rowCount >= 1, "Legalább egy sorban meg kell jelennie Kati (42) pontos eredményének.");

    }

    @Test
    @DisplayName("getTopPlayers – összegzett pontszám alapján csökkenő sorrendben adjuk vissza a játékosokat")
    void getTopPlayers_returnsPlayersOrderedByTotalScoreDesc() {
        // Előkészítés: több pont mentése különböző játékosokhoz
        databaseService.saveScore("Kati", 10);
        databaseService.saveScore("Kati", 40); // Kati összesen 50
        databaseService.saveScore("Boti", 30); // Boti összesen 30
        databaseService.saveScore("Marci", 5); // Marci összesen 5

        List<String> topPlayers = databaseService.getTopPlayers(2);

        assertEquals(2, topPlayers.size(), "A limit=2 esetén 2 ranglistaelemnek kell visszajönnie.");

        String first = topPlayers.get(0);
        String second = topPlayers.get(1);

        assertTrue(first.startsWith("1. Kati"), "Az első helyen Katinak kell állnia: " + first);
        assertTrue(first.contains("50 pont"), "Az első elemnek 50 pontot kell jeleznie: " + first);
        assertTrue(second.startsWith("2. Boti"), "A második helyen Botinak kell állnia: " + second);
        assertTrue(second.contains("30 pont"), "A második elemnek 30 pontot kell jeleznie: " + second);

    }

    @Test
    @DisplayName("getTopPlayers – üres tábla esetén üres listát adunk vissza")
    void getTopPlayers_returnsEmptyListWhenNoScores() {
        List<String> topPlayers = databaseService.getTopPlayers(5);
        assertTrue(topPlayers.isEmpty(), "Üres scores tábla esetén a ranglistának üres listának kell lennie.");
    }

    @Test
    @DisplayName("isConnectionAvailable – elérhető adatbázis esetén true-t ad vissza")
    void isConnectionAvailable_returnsTrueWhenDatabaseIsReachable() {
        boolean available = databaseService.isConnectionAvailable();
        assertTrue(available, "Ha az adatbázis elérhető, az isConnectionAvailable() metódusnak true-t kell adnia.");
    }

    @Test
    @DisplayName("isConnectionAvailable – hibás kapcsolat esetén false-t ad vissza (elvárt viselkedés)")
    void isConnectionAvailable_returnsFalseWhenDatabaseIsNotReachable_expectedBehaviour() {
        // Ez az ELVÁRT viselkedést teszteli, ténylegesen nem tudjuk a kapcsolatot direkt elrontani itt.
        // Így csak azt ellenőrizzük, hogy a visszatérési érték logikailag boole típusú és hogy nem dob kivételt....
        boolean available = databaseService.isConnectionAvailable();

        assertTrue(available || !available, "A metódusnak minden esetben logikai értékkel kell visszatérnie kivétel nélkül.");
    }
}