package nye.flocrm.progtech.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HumanPlayerTest {
    private HumanPlayer humanPlayer;
    private Board board;

    @BeforeEach
    void setUp() {
        humanPlayer = new HumanPlayer("Játékos", 'X'); // [cite: 83]
        board = new Board(); // [cite: 84]
    }

    @Test
    @DisplayName("A HumanPlayer létrehozása helyes adatokkal")
    void testHumanPlayerCreation() {
        // AMIKOR - HumanPlayer létrejön
        // AKKOR - helyes adatokkal rendelkezik
        assertEquals("Játékos", humanPlayer.getName()); // [cite: 85]
        assertEquals('X', humanPlayer.getSymbol()); // [cite: 85]
        assertTrue(humanPlayer.isHuman()); // [cite: 85]
    }

    @Test
    @DisplayName("A getName() metódus visszaadja a helyes nevet")
    void testGetName() {
        // AMIKOR & AKKOR
        assertEquals("Játékos", humanPlayer.getName()); // [cite: 86]
    }

    @Test
    @DisplayName("A getSymbol() metódus visszaadja a helyes szimbólumot")
    void testGetSymbol() {
        // AMIKOR & AKKOR
        assertEquals('X', humanPlayer.getSymbol()); // [cite: 87]
    }

    @Test
    @DisplayName("Az isHuman() metódus mindig true-t ad vissza")
    void testIsHuman() {
        // AMIKOR & AKKOR
        assertTrue(humanPlayer.isHuman()); // [cite: 88]
    }

    @Test
    @DisplayName("A setName() metódus helyesen beállítja az új nevet")
    void testSetNameValid() {
        // AMIKOR
        humanPlayer.setName("Új Játékos"); // [cite: 89]
        // AKKOR
        assertEquals("Új Játékos", humanPlayer.getName()); // [cite: 90]
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("A setName() metódus NullPointerException-t dob null értékre")
    void testSetNameNull() {
        // AMIKOR & AKKOR
        // Az implementáció (name.trim()) NullPointerException-t dob
        assertThrows(NullPointerException.class, () -> humanPlayer.setName(null),
                "A setName(null) hívásnak NullPointerException-t kell dobnia.");
    }

    @Test
    @DisplayName("A setName() metódus üres stringre állítja a nevet, ha az üres string")
    void testSetNameEmptyString() {
        // AMIKOR
        String originalName = humanPlayer.getName(); // [cite: 93]
        humanPlayer.setName(""); // [cite: 94]
        // AKKOR
        // Az implementáció ("".trim()) üres stringet ad vissza
        assertEquals("", humanPlayer.getName(), "A névnek üres stringnek kell lennie, nem az eredetinek.");
        assertNotEquals(originalName, humanPlayer.getName());
    }

    @Test
    @DisplayName("A setName() metódus üres stringre állítja a nevet, ha az csak whitespace")
    void testSetNameWhitespace() {
        // AMIKOR
        String originalName = humanPlayer.getName(); // [cite: 95]
        humanPlayer.setName("   "); // [cite: 96]
        // AKKOR
        // Az implementáció ("   ".trim()) üres stringet ad vissza
        assertEquals("", humanPlayer.getName(), "A névnek üres stringnek kell lennie, nem az eredetinek.");
        assertNotEquals(originalName, humanPlayer.getName());
    }

    @Test
    @DisplayName("A setName() metódus trim-eli a beállítandó nevet")
    void testSetNameTrimming() {
        // AMIKOR
        humanPlayer.setName("  Új Játékos  "); // [cite: 97]
        // AKKOR
        assertEquals("Új Játékos", humanPlayer.getName()); // [cite: 98]
    }

    @Test
    @DisplayName("A makeMove() metódus nem dob kivételt")
    void testMakeMoveNoException() {
        // AMIKOR & AKKOR - makeMove lefut kivétel nélkül
        assertDoesNotThrow(() -> humanPlayer.makeMove(board)); // [cite: 99]
    }

    @Test
    @DisplayName("A makeMove() metódus nem változtatja meg a táblát")
    void testMakeMoveDoesNotChangeBoard() {
        // AMIKOR - tábla mentése
        Board originalBoard = new Board(); // [cite: 100]
        originalBoard.placeSymbol(0, 0, 'X'); // [cite: 101]
        originalBoard.placeSymbol(1, 1, 'O'); // [cite: 101]
        copyBoardState(originalBoard, board); // [cite: 102]

        // AMIT
        humanPlayer.makeMove(board); // [cite: 103]

        // AKKOR - a tábla nem változhat
        assertBoardsEqual(originalBoard, board); // [cite: 104]
    }

    @Test
    @DisplayName("A HumanPlayer különböző szimbólumokkal helyesen működik")
    void testHumanPlayerWithDifferentSymbols() {
        // AMIKOR
        HumanPlayer playerX = new HumanPlayer("X Játékos", 'X'); // [cite: 105]
        HumanPlayer playerO = new HumanPlayer("O Játékos", 'O'); // [cite: 106]
        // AKKOR
        assertEquals('X', playerX.getSymbol()); // [cite: 106]
        assertEquals("X Játékos", playerX.getName()); // [cite: 107]
        assertEquals('O', playerO.getSymbol()); // [cite: 107]
        assertEquals("O Játékos", playerO.getName()); // [cite: 107]
    }

    @Test
    @DisplayName("A HumanPlayer konstruktor NEM trim-eli a nevet")
    void testConstructorDoesNotTrimName() {
        // AMIKOR - név whitespace-ekkel
        HumanPlayer player = new HumanPlayer("  Trim Játékos  ", 'X'); // [cite: 108]
        // AKKOR - a név megmarad eredeti formájában
        assertEquals("  Trim Játékos  ", player.getName()); // [cite: 109]
    }

    @Test
    @DisplayName("A kezdeti pontszám 0")
    void testInitialScoreIsZero() {
        // AMIKOR & AKKOR
        assertEquals(0, humanPlayer.getScore()); // [cite: 218]
    }

    @Test
    @DisplayName("A setScore() metódus helyesen beállítja a pontszámot")
    void testSetScore() {
        // AMIKOR
        humanPlayer.setScore(100); // [cite: 220]
        // AKKOR
        assertEquals(100, humanPlayer.getScore());
    }

    @Test
    @DisplayName("Az addScore() metódus helyesen hozzáad a pontszámhoz")
    void testAddScore() {
        // AMIKOR
        humanPlayer.setScore(50); // [cite: 220]
        humanPlayer.addScore(25); // [cite: 222]
        // AKKOR
        assertEquals(75, humanPlayer.getScore());
    }

    // === SEGÉDFÜGGVÉNYEK ===

    /**
     * Másolja a tábla állapotát egyik tábláról a másikra [cite: 109]
     */
    private void copyBoardState(Board source, Board target) {
        target.clear(); // [cite: 109]
        for (int row = 0; row < Board.SIZE; row++) { // [cite: 110]
            for (int col = 0; col < Board.SIZE; col++) { // [cite: 110]
                char symbol = source.getSymbolAt(row, col); // [cite: 110]
                if (symbol != '.') { // [cite: 111]
                    target.placeSymbol(row, col, symbol); // [cite: 111]
                }
            }
        }
    }

    /**
     * Ellenőrzi, hogy két tábla egyenlő-e [cite: 112]
     */
    private void assertBoardsEqual(Board expected, Board actual) {
        for (int row = 0; row < Board.SIZE; row++) { // [cite: 112]
            for (int col = 0; col < Board.SIZE; col++) { // [cite: 112]
                assertEquals(expected.getSymbolAt(row, col), actual.getSymbolAt(row, col),
                        String.format("A tábla nem egyezik a %d,%d pozícióban", row, col)); // [cite: 113]
            }
        }
    }
}