package nye.flocrm.progtech.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.HumanPlayer;
import nye.flocrm.progtech.model.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.lang.reflect.Method;

public class GameLoaderTest {

    private GameLoader gameLoader;
    private Board board;
    private HumanPlayer player1;
    private HumanPlayer player2;
    private Player currentPlayer;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        gameLoader = new GameLoader();
        board = new Board();

        // Játékosok
        player1 = new HumanPlayer("Teszt Játékos 1", 'X');
        player1.setScore(15);

        player2 = new HumanPlayer("Teszt Játékos 2", 'O');
        player2.setScore(10);

        currentPlayer = player1;

        // Állítsunk be egy alap táblaállapotot
        board.placeSymbol(0, 0, 'X');
        board.placeSymbol(1, 1, 'O');
        board.placeSymbol(2, 2, 'X');
        board.placeSymbol(3, 3, 'O');

        // Hozzunk létre egy ideiglenes fájlt a tesztekhez
        tempFile = File.createTempFile("game_test", ".txt");
        tempFile.deleteOnExit();
    }

    // Segédmetódus a fájl törlésére
    private void deleteFileSilently(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            // Figyelmen kívül hagyjuk, mert ez csak takarítás
        }
    }

    @Test
    @DisplayName("A saveFileExists() metódus false-t ad vissza, ha nincs mentett fájl")
    void testSaveFileExistsNoFile() {
        // AMIKOR - nincs mentett fájl (töröljük, ha véletlenül létezne)
        deleteFileSilently(new File("game_save.txt"));

        // AKKOR
        assertFalse(gameLoader.saveFileExists(), "Nem szabad léteznie mentett fájlnak");
    }

    @Test
    @DisplayName("A saveFileExists() metódus true-t ad vissza, ha van mentett fájl")
    void testSaveFileExistsWithFile() throws IOException {
        // AMIKOR - mentett fájl létrehozása
        gameLoader.saveGame(board, player1, player2, currentPlayer, GameMode.HUMAN_VS_HUMAN);

        // AKKOR
        assertTrue(gameLoader.saveFileExists(), "A mentési fájlnak léteznie kell");

        // Takarítás
        deleteFileSilently(new File("game_save.txt"));
    }

    @Test
    @DisplayName("A saveGame() metódus létrehozza a mentési fájlt")
    void testSaveGameCreatesFile() throws IOException {
        // AMIKOR - játék mentése
        gameLoader.saveGame(board, player1, player2, currentPlayer, GameMode.HUMAN_VS_HUMAN);

        // AKKOR - fájl létezik és nem üres
        File file = new File("game_save.txt");
        assertTrue(file.exists(), "A mentési fájlnak léteznie kell");
        assertTrue(file.length() > 0, "A mentési fájlnak nem lehet üres");

        // Takarítás
        deleteFileSilently(file);
    }

    @Test
    @DisplayName("A saveGame() metódus nem dob kivételt érvényes paraméterekkel")
    void testSaveGameNoException() {
        // AMIKOR & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> gameLoader.saveGame(board, player1, player2, currentPlayer, GameMode.HUMAN_VS_HUMAN));

        // Takarítás
        deleteFileSilently(new File("game_save.txt"));
    }

    @Test
    @DisplayName("A saveGame() metódus helyesen menti a játékosok adatait")
    void testSaveGamePlayerInfo() throws IOException {
        // AMIKOR - játék mentése konkrét adatokkal
        HumanPlayer p1 = new HumanPlayer("Kati", 'X');
        p1.setScore(25);

        HumanPlayer p2 = new HumanPlayer("Joci", 'O');
        p2.setScore(5);

        // Tegyük fel, hogy épp játékos 2 következik
        gameLoader.saveGame(board, p1, p2, (Player) p2, GameMode.HUMAN_VS_AI);

        // AKKOR - fájl tartalma ellenőrzése
        File file = new File("game_save.txt");
        String content = Files.readString(file.toPath());

        assertTrue(content.contains("Első játékos: Kati"),
                "A fájlnak tartalmaznia kell az első játékos nevét");
        assertTrue(content.contains("Első játékos pontszám: 25"),
                "A fájlnak tartalmaznia kell az első játékos pontszámát");

        assertTrue(content.contains("Második játékos: Joci"),
                "A fájlnak tartalmaznia kell a második játékos nevét");
        assertTrue(content.contains("Második játékos pontszám: 5"),
                "A fájlnak tartalmaznia kell a második játékos pontszámát");

        assertTrue(content.contains("Következő: O"),
                "A fájlnak tartalmaznia kell a soron következő játékos szimbólumát");
        assertTrue(content.contains("Játékmód: HUMAN_VS_AI"),
                "A fájlnak tartalmaznia kell a játékmódot");

        // Takarítás
        deleteFileSilently(file);
    }

    @Test
    @DisplayName("A saveGame() metódus helyesen menti a tábla állapotát")
    void testSaveGameBoardState() throws IOException {
        // AMIKOR - tábla feltöltése és mentése
        Board testBoard = new Board();
        testBoard.placeSymbol(0, 0, 'X');
        testBoard.placeSymbol(1, 1, 'O');
        testBoard.placeSymbol(4, 4, 'X');

        gameLoader.saveGame(board, player1, player2, currentPlayer, GameMode.HUMAN_VS_HUMAN);

        // AKKOR - fájl tartalma ellenőrzése
        File file = new File("game_save.txt");
        String content = Files.readString(file.toPath());

        assertTrue(content.contains("X"), "A fájlnak tartalmaznia kell X szimbólumokat");
        assertTrue(content.contains("O"), "A fájlnak tartalmaznia kell O szimbólumokat");
        assertTrue(content.contains("Tábla mérete: 10"), "A fájlnak tartalmaznia kell a tábla méretét");

        // Takarítás
        deleteFileSilently(file);
    }

    @Test
    @DisplayName("A loadGame() metódus kivételt dob, ha a fájl nem létezik")
    void testLoadGameThrowsExceptionWhenFileNotExists() {
        // AMIKOR & AKKOR - kivételt dob
        assertThrows(IOException.class, () -> gameLoader.loadGame("nonexistent_file.txt"));
    }

    @Test
    @DisplayName("A loadGame() metódus kivételt dob, ha a fájl üres")
    void testLoadGameThrowsExceptionWhenFileEmpty() throws IOException {
        // AMIKOR - üres fájl létrehozása
        File tempFile = File.createTempFile("test_empty", ".txt");
        tempFile.deleteOnExit();

        // AKKOR - kivételt dob
        assertThrows(IOException.class, () -> gameLoader.loadGame(tempFile.getAbsolutePath()));
    }

    @Test
    @DisplayName("A loadGame() metódus kivételt dob, ha hiányzik a játékos információ")
    void testLoadGameThrowsExceptionWhenMissingPlayerInfo() throws IOException {
        // AMIKOR - fájl létrehozása hiányos információkkal
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new FileWriter(tempFile))) {
            writer.println("Pontszám: 10");
            writer.println("Szimbólum: X");
            // Hiányzik: "Játékos: ..."
        }

        // AKKOR - kivételt dob
        assertThrows(IOException.class, () -> gameLoader.loadGame(tempFile.getAbsolutePath()));
    }

    @Test
    @DisplayName("A loadGame() metódus helyesen betölt egy érvényes mentési fájlt")
    void testLoadGameValidFile() throws IOException {
        // AMIKOR - érvényes mentési fájl létrehozása az ÚJ formátummal
        createValidSaveFile(tempFile);

        // AMIT
        GameLoader.GameState loadedState = gameLoader.loadGame(tempFile.getAbsolutePath());

        // AKKOR
        assertNotNull(loadedState, "A betöltött állapot nem lehet null");
        assertNotNull(loadedState.board(), "A tábla nem lehet null");
        assertEquals(GameMode.HUMAN_VS_HUMAN, loadedState.gameMode(),
                "A játékmódnak HUMAN_VS_HUMAN-nek kell lennie");

        // Tábla állapot ellenőrzése
        Board loadedBoard = loadedState.board();
        assertEquals('X', loadedBoard.getSymbolAt(0, 0), "0,0 pozíción X-nek kell lennie");
        assertEquals('O', loadedBoard.getSymbolAt(1, 1), "1,1 pozíción O-nak kell lennie");
        assertEquals('.', loadedBoard.getSymbolAt(2, 0), "2,0 pozíción üresnek kell lennie");

        // Játékos adatok ellenőrzése – az ÚJ mezőkön
        assertEquals("Teszt Játékos 1", loadedState.player1Name(),
                "Az első játékos nevének egyeznie kell");
        assertEquals("Teszt Játékos 2", loadedState.player2Name(),
                "A második játékos nevének egyeznie kell");

        assertEquals(15, loadedState.player1Score(),
                "Az első játékos pontszámának 15-nek kell lennie");
        assertEquals(10, loadedState.player2Score(),
                "A második játékos pontszámának 10-nek kell lennie");
    }

    @Test
    @DisplayName("A loadGame() metódus kezeli, ha a második játékos AI névvel szerepel")
    void testLoadGameWithAIPlayer() throws IOException {
        // AMIKOR - fájl létrehozása AI nevű második játékossal az ÚJ formátumban
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new FileWriter(tempFile))) {
            writer.println("Első játékos: Ember");
            writer.println("Első játékos pontszám: 0");
            writer.println("Második játékos: AI");
            writer.println("Második játékos pontszám: 0");
            writer.println("Következő: O");
            writer.println("Játékmód: HUMAN_VS_AI");
            writer.println("Tábla mérete: 10");
            writer.println("Dátum: " + new java.util.Date());
            writer.println();
            writer.println("| Sor\\Oszlop | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |");
            for (int row = 0; row < 10; row++) {
                writer.print("| " + (row + 1) + " ");
                for (int col = 0; col < 10; col++) {
                    writer.print("| . ");
                }
                writer.println("|");
            }
        }

        // AMIT
        GameLoader.GameState loadedState = gameLoader.loadGame(tempFile.getAbsolutePath());

        // AKKOR
        assertNotNull(loadedState, "A betöltött állapot nem lehet null");
        assertEquals("AI", loadedState.player2Name(),
                "A második játékos nevének AI-nak kell lennie");
        assertEquals("Ember", loadedState.player1Name(),
                "Az első játékos neve Ember legyen");
        assertEquals(GameMode.HUMAN_VS_AI, loadedState.gameMode(),
                "A játékmódnak HUMAN_VS_AI-nak kell lennie");
        assertEquals('O', loadedState.nextPlayerSymbol(),
                "A soron következő játékos szimbólumának O-nak kell lennie");
    }

    @Test
    @DisplayName("A loadGame() metódus kezeli a különböző tábla méreteket")
    void testLoadGameWithDifferentBoardSize() throws IOException {
        // AMIKOR - fájl létrehozása 10x10-es táblával
        createValidSaveFile(tempFile);

        // AMIT
        GameLoader.GameState loadedState = gameLoader.loadGame(tempFile.getAbsolutePath());

        // AKKOR
        assertNotNull(loadedState.board(), "A tábla nem lehet null");
        assertEquals(10, loadedState.board().getSize(), "A tábla méretének 10-nek kell lennie");
    }

    @Test
    @DisplayName("A loadGame() metódus helyesen dolgozza fel a tábla sorait")
    void testLoadGameBoardProcessing() throws IOException {
        // AMIKOR - fájl létrehozása konkrét tábla adatokkal az ÚJ formátumban
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new FileWriter(tempFile))) {
            writer.println("Első játékos: Tábla Teszt 1");
            writer.println("Első játékos pontszám: 15");
            writer.println("Második játékos: Tábla Teszt 2");
            writer.println("Második játékos pontszám: 10");
            writer.println("Következő: X");
            writer.println("Játékmód: HUMAN_VS_HUMAN");
            writer.println("Tábla mérete: 10");
            writer.println("Dátum: " + new java.util.Date());
            writer.println();
            writer.println("| Sor\\Oszlop | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |");

            // Első sor: X . O . . . . . . .
            writer.print("| 1 ");
            writer.print("| X | . | O | . | . | . | . | . | . | . |");
            writer.println();

            // Második sor: . X . O . . . . . .
            writer.print("| 2 ");
            writer.print("| . | X | . | O | . | . | . | . | . | . |");
            writer.println();

            // Üres sorok a többihez
            for (int row = 3; row <= 10; row++) {
                writer.print("| " + row + " ");
                for (int col = 0; col < 10; col++) {
                    writer.print("| . ");
                }
                writer.println("|");
            }
        }

        // AMIT
        GameLoader.GameState loadedState = gameLoader.loadGame(tempFile.getAbsolutePath());

        // AKKOR - tábla állapot ellenőrzése
        Board loadedBoard = loadedState.board();
        assertEquals('X', loadedBoard.getSymbolAt(0, 0), "0,0 pozíción X-nek kell lennie");
        assertEquals('.', loadedBoard.getSymbolAt(0, 1), "0,1 pozíción üresnek kell lennie");
        assertEquals('O', loadedBoard.getSymbolAt(0, 2), "0,2 pozíción O-nak kell lennie");
        assertEquals('.', loadedBoard.getSymbolAt(1, 0), "1,0 pozíción üresnek kell lennie");
        assertEquals('X', loadedBoard.getSymbolAt(1, 1), "1,1 pozíción X-nek kell lennie");
        assertEquals('O', loadedBoard.getSymbolAt(1, 3), "1,3 pozíción O-nak kell lennie");
    }

    @Test
    @DisplayName("A loadGame() paraméter nélküli metódus a default fájlt használja")
    void testLoadGameDefaultFile() throws IOException {
        // AMIKOR - default fájl létrehozása
        createValidSaveFile(new File("game_save.txt"));

        // AMIT
        GameLoader.GameState loadedState = gameLoader.loadGame();

        // AKKOR
        assertNotNull(loadedState, "A betöltött állapot nem lehet null");

        // Takarítás
        deleteFileSilently(new File("game_save.txt"));
    }

    @Test
    @DisplayName("A loadGame() metódus kivételt dob érvénytelen játékmód esetén")
    void testLoadGameThrowsExceptionForInvalidGameMode() throws IOException {
        // AMIKOR - fájl létrehozása érvénytelen játékmóddal
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new FileWriter(tempFile))) {
            writer.println("Játékos: Teszt");
            writer.println("Pontszám: 10");
            writer.println("Játékmód: INVALID_MODE"); // Érvénytelen mód
            writer.println("Tábla mérete: 10");
        }

        // AKKOR - kivételt dob
        assertThrows(IOException.class, () -> gameLoader.loadGame(tempFile.getAbsolutePath()));
    }

    @Test
    @DisplayName("A loadGame() metódus kivételt dob érvénytelen számformátum esetén")
    void testLoadGameThrowsExceptionForInvalidNumberFormat() throws IOException {
        // AMIKOR - fájl létrehozása érvénytelen számmal
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new FileWriter(tempFile))) {
            writer.println("Játékos: Teszt");
            writer.println("Pontszám: invalid_number"); // Érvénytelen szám
            writer.println("Tábla mérete: 10");
        }

        // AKKOR - kivételt dob
        assertThrows(IOException.class, () -> gameLoader.loadGame(tempFile.getAbsolutePath()));
    }

    @Test
    @DisplayName("A processBoardLine metódus helyesen dolgoz fel egy tábla sort")
    void testProcessBoardLine() {
        // AMIKOR - GameLoader példány és tábla
        GameLoader loader = new GameLoader();
        Board testBoard = new Board();

        // Egy tipikus tábla sor formátuma
        String boardLine = "| 1 | X | . | O | . | . | . | . | . | . | . |";

        // AMIT - reflexió használata a privát metódus eléréséhez
        try {
            Method method = GameLoader.class.getDeclaredMethod("processBoardLine", Board.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(loader, testBoard, boardLine, 0);
        } catch (Exception e) {
            fail("Reflexió hiba: " + e.getMessage());
        }

        // AKKOR - a tábla állapotának ellenőrzése
        assertEquals('X', testBoard.getSymbolAt(0, 0), "0,0 pozíción X-nek kell lennie");
        assertEquals('.', testBoard.getSymbolAt(0, 1), "0,1 pozíción üresnek kell lennie");
        assertEquals('O', testBoard.getSymbolAt(0, 2), "0,2 pozíción O-nak kell lennie");
        assertEquals('.', testBoard.getSymbolAt(0, 3), "0,3 pozíción üresnek kell lennie");
    }

    @Test
    @DisplayName("A processBoardLine metódus kezeli a különböző formázásokat")
    void testProcessBoardLineDifferentFormats() {
        // AMIKOR - GameLoader példány és tábla
        GameLoader loader = new GameLoader();
        Board testBoard = new Board();

        // Különböző formázású sorok
        String[] boardLines = {
                "| 1 | X | . | O | . |", // Kevesebb oszlop
                "| 2 | X | X | X | X | X | X | X | X | X | X | X | X |", // Több oszlop
                "| 3 |   | X |   | O |   |", // Üresek a cellák között
        };

        // AMIT - minden sor feldolgozása
        try {
            Method method = GameLoader.class.getDeclaredMethod("processBoardLine", Board.class, String.class, int.class);
            method.setAccessible(true);

            for (int i = 0; i < boardLines.length; i++) {
                method.invoke(loader, testBoard, boardLines[i], i);
            }
        } catch (Exception e) {
            fail("Reflexió hiba: " + e.getMessage());
        }

        // AKKOR - a tábla állapotának ellenőrzése
        // Csak az érvényes pozíciókban kell lennie szimbólumoknak
        assertEquals('X', testBoard.getSymbolAt(0, 0), "0,0 pozíción X-nek kell lennie");
        assertEquals('O', testBoard.getSymbolAt(0, 2), "0,2 pozíción O-nak kell lennie");
    }

    /**
     * Létrehoz egy érvényes mentési fájlt a teszteléshez
     */
    private void createValidSaveFile(File file) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new FileWriter(file))) {

            // ÚJ mezők
            writer.println("Első játékos: Teszt Játékos 1");
            writer.println("Első játékos pontszám: 15");
            writer.println("Második játékos: Teszt Játékos 2");
            writer.println("Második játékos pontszám: 10");
            writer.println("Következő: X");
            writer.println("Játékmód: HUMAN_VS_HUMAN");
            writer.println("Tábla mérete: 10");
            writer.println("Dátum: " + new java.util.Date());
            writer.println();

            // Fejléc
            writer.println("| Sor\\Oszlop | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |");

            // Tábla adatok (például X, O, X, O átlóban)
            for (int row = 0; row < 10; row++) {
                writer.print("| " + (row + 1) + " ");

                for (int col = 0; col < 10; col++) {
                    char symbol = '.';

                    // Egy kis minta-adat
                    if (row == col && (row % 2 == 0)) symbol = 'X';
                    if (row == col && (row % 2 == 1)) symbol = 'O';

                    writer.print("| " + symbol + " ");
                }
                writer.println("|");
            }
        }
    }
}