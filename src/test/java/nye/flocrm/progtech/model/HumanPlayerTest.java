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
        humanPlayer = new HumanPlayer("Játékos", 'X');
        board = new Board();
    }

    @Test
    @DisplayName("A HumanPlayer létrehozása helyes adatokkal")
    void testHumanPlayerCreation() {
        // AMIKOR - HumanPlayer létrejön

        // AKKOR - helyes adatokkal rendelkezik
        assertEquals("Játékos", humanPlayer.getName());
        assertEquals('X', humanPlayer.getSymbol());
        assertTrue(humanPlayer.isHuman());
    }

    @Test
    @DisplayName("A getName() metódus visszaadja a helyes nevet")
    void testGetName() {
        // AMIKOR & AKKOR
        assertEquals("Játékos", humanPlayer.getName());
    }

    @Test
    @DisplayName("A getSymbol() metódus visszaadja a helyes szimbólumot")
    void testGetSymbol() {
        // AMIKOR & AKKOR
        assertEquals('X', humanPlayer.getSymbol());
    }

    @Test
    @DisplayName("Az isHuman() metódus mindig true-t ad vissza")
    void testIsHuman() {
        // AMIKOR & AKKOR
        assertTrue(humanPlayer.isHuman());
    }

    @Test
    @DisplayName("A setName() metódus helyesen beállítja az új nevet")
    void testSetNameValid() {
        // AMIKOR
        humanPlayer.setName("Új Játékos");

        // AKKOR
        assertEquals("Új Játékos", humanPlayer.getName());
    }

    @Test
    @DisplayName("A setName() metódus nem változtatja meg a nevet null értékre")
    void testSetNameNull() {
        // AMIKOR
        String originalName = humanPlayer.getName();
        humanPlayer.setName(null);

        // AKKOR
        assertEquals(originalName, humanPlayer.getName());
    }

    @Test
    @DisplayName("A setName() metódus nem változtatja meg a nevet üres stringre")
    void testSetNameEmptyString() {
        // AMIKOR
        String originalName = humanPlayer.getName();
        humanPlayer.setName("");

        // AKKOR
        assertEquals(originalName, humanPlayer.getName());
    }

    @Test
    @DisplayName("A setName() metódus nem változtatja meg a nevet csak whitespace stringre")
    void testSetNameWhitespace() {
        // AMIKOR
        String originalName = humanPlayer.getName();
        humanPlayer.setName("   ");

        // AKKOR
        assertEquals(originalName, humanPlayer.getName());
    }

    @Test
    @DisplayName("A setName() metódus trim-eli a beállítandó nevet")
    void testSetNameTrimming() {
        // AMIKOR
        humanPlayer.setName("  Új Játékos  ");

        // AKKOR
        assertEquals("Új Játékos", humanPlayer.getName());
    }

    @Test
    @DisplayName("A makeMove() metódus nem dob kivételt")
    void testMakeMoveNoException() {
        // AMIKOR & AKKOR - makeMove lefut kivétel nélkül
        assertDoesNotThrow(() -> humanPlayer.makeMove(board));
    }

    @Test
    @DisplayName("A makeMove() metódus nem változtatja meg a táblát")
    void testMakeMoveDoesNotChangeBoard() {
        // AMIKOR - tábla mentése
        Board originalBoard = new Board();
        // Kitöltjük néhány szimbólummal
        originalBoard.placeSymbol(0, 0, 'X');
        originalBoard.placeSymbol(1, 1, 'O');

        // Másoljuk az állapotot
        copyBoardState(originalBoard, board);

        // AMIT
        humanPlayer.makeMove(board);

        // AKKOR - a tábla nem változhat
        assertBoardsEqual(originalBoard, board);
    }

    @Test
    @DisplayName("A HumanPlayer különböző szimbólumokkal helyesen működik")
    void testHumanPlayerWithDifferentSymbols() {
        // AMIKOR
        HumanPlayer playerX = new HumanPlayer("X Játékos", 'X');
        HumanPlayer playerO = new HumanPlayer("O Játékos", 'O');

        // AKKOR
        assertEquals('X', playerX.getSymbol());
        assertEquals("X Játékos", playerX.getName());
        assertEquals('O', playerO.getSymbol());
        assertEquals("O Játékos", playerO.getName());
    }

    @Test
    @DisplayName("A HumanPlayer konstruktor NEM trim-eli a nevet")
    void testConstructorDoesNotTrimName() {
        // AMIKOR - név whitespace-ekkel
        HumanPlayer player = new HumanPlayer("  Trim Játékos  ", 'X');

        // AKKOR - a név megmarad eredeti formájában
        assertEquals("  Trim Játékos  ", player.getName());
    }

    // === SEGÉDFÜGGVÉNYEK ===

    /**
     * Másolja a tábla állapotát egyik tábláról a másikra
     */
    private void copyBoardState(Board source, Board target) {
        target.clear();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                char symbol = source.getSymbolAt(row, col);
                if (symbol != '.') {
                    target.placeSymbol(row, col, symbol);
                }
            }
        }
    }

    /**
     * Ellenőrzi, hogy két tábla egyenlő-e
     */
    private void assertBoardsEqual(Board expected, Board actual) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                assertEquals(expected.getSymbolAt(row, col), actual.getSymbolAt(row, col),
                        String.format("A tábla nem egyezik a %d,%d pozícióban", row, col));
            }
        }
    }
}