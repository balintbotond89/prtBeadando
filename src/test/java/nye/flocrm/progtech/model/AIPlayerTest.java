package nye.flocrm.progtech.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AIPlayerTest {

    private AIPlayer aiPlayer;
    private Board board;

    @BeforeEach
    void setUp() {
        aiPlayer = new AIPlayer("AI Player", 'O', 'X');
        board = new Board();
    }

    @Test
    @DisplayName("Az AIPlayer létrehozása helyes adatokkal")
    void testAIPlayerCreation() {
        // AMIKOR - AIPlayer létrejön

        // AKKOR - helyes adatokkal rendelkezik
        assertEquals("AI Player", aiPlayer.getName());
        assertEquals('O', aiPlayer.getSymbol());
        assertFalse(aiPlayer.isHuman());
    }

    @Test
    @DisplayName("A getName() metódus visszaadja a helyes nevet")
    void testGetName() {
        // AMIKOR & AKKOR
        assertEquals("AI Player", aiPlayer.getName());
    }

    @Test
    @DisplayName("A getSymbol() metódus visszaadja a helyes szimbólumot")
    void testGetSymbol() {
        // AMIKOR & AKKOR
        assertEquals('O', aiPlayer.getSymbol());
    }

    @Test
    @DisplayName("Az isHuman() metódus mindig false-t ad vissza")
    void testIsHuman() {
        // AMIKOR & AKKOR
        assertFalse(aiPlayer.isHuman());
    }

    @Test
    @DisplayName("A setName() metódus nem változtatja meg a nevet")
    void testSetNameDoesNothing() {
        // AMIKOR
        String originalName = aiPlayer.getName();
        aiPlayer.setName("Új Név");

        // AKKOR
        assertEquals(originalName, aiPlayer.getName());
    }

    @Test
    @DisplayName("A makeMove() metódus helyesen elhelyez egy szimbólumot")
    void testMakeMovePlacesSymbol() {
        // AMIKOR
        aiPlayer.makeMove(board);

        // AKKOR - ellenőrizzük, hogy valamelyik cellában 'O' van
        boolean foundSymbol = false;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.getSymbolAt(row, col) == 'O') {
                    foundSymbol = true;
                    break;
                }
            }
        }
        assertTrue(foundSymbol, "A makeMove() után legalább egy cellában 'O' szimbólumnak kell lennie");
    }

    @Test
    @DisplayName("A makeMove() metódus csak üres cellába helyez szimbólumot")
    void testMakeMoveOnlyToEmptyCells() {
        // AMIKOR - kitöltjük a tábla egy részét
        board.placeSymbol(0, 0, 'X');
        board.placeSymbol(0, 1, 'X');
        board.placeSymbol(1, 0, 'X');

        // AMIT
        aiPlayer.makeMove(board);

        // AKKOR - az AI nem léphet foglalt cellába
        assertEquals('X', board.getSymbolAt(0, 0));
        assertEquals('X', board.getSymbolAt(0, 1));
        assertEquals('X', board.getSymbolAt(1, 0));
        // Legalább egy üres cellában 'O'-nak kell lennie
    }

    @Test
    @DisplayName("Az AIPlayer különböző szimbólumokkal helyesen működik")
    void testAIPlayerWithDifferentSymbols() {
        // AMIKOR
        AIPlayer aiPlayerX = new AIPlayer("AI X", 'X', 'O');

        // AKKOR
        assertEquals('X', aiPlayerX.getSymbol());
        assertEquals("AI X", aiPlayerX.getName());
    }

    @Test
    @DisplayName("A makeMove() metódus nem dob kivételt üres táblán")
    void testMakeMoveOnEmptyBoard() {
        // AMIKOR - üres tábla

        // AMIT & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> aiPlayer.makeMove(board));
    }

    @Test
    @DisplayName("A makeMove() metódus nem dob kivételt majdnem teli táblán")
    void testMakeMoveOnAlmostFullBoard() {
        // AMIKOR - majdnem teli tábla (1 cella üres)
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (!(row == 9 && col == 9)) { // utolsó cella üresen hagyva
                    board.placeSymbol(row, col, 'X');
                }
            }
        }

        // AMIT & AKKOR - nem dob kivételt
        assertDoesNotThrow(() -> aiPlayer.makeMove(board));

        // AKKOR - az AI az egyetlen üres cellába lépett
        assertEquals('O', board.getSymbolAt(9, 9));
    }
}