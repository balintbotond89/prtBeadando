    package nye.flocrm.progtech.model;

    import java.io.ByteArrayOutputStream;
    import java.io.PrintStream;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    /**
     * Board osztály tesztjei
     */
    public class BoardTest {

        // Alt + F12
        // target/site/jacoco/index.html

        private Board board;

        @BeforeEach
        void setUp() {
            board = new Board();
            // [....]

        }

        @Test
        @DisplayName("A clear() metódus visszaállítja a táblát üresre")
        void testClearResetBoard() {
            // AMIKOR
            board.clear();

            // AKKOR - ellenőrizzük a belső állapotot, nem a kimenetet
            boolean allEmpty = true;
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    if (board.getSymbolAt(row, col) != '.') {
                        allEmpty = false;
                        break;
                    }
                }
            }

            assertTrue(allEmpty, "A clear() után minden cellának '.'-nek kell lennie");
        }

        @Test
        @DisplayName("A print() metódus helyesen jeleníti meg az üres táblát")
        void testPrintEmptyBoard() {
            // AMIKOR
            board.clear();

            // AMIT
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            board.print();

            // AKKOR
            String output = outContent.toString();
            assertTrue(output.contains("."), "A kimenetnek tartalmaznia kell '.' karaktereket");
            assertFalse(output.contains("X"), "A kimenetnek nem szabad tartalmaznia 'X' karaktert");
            assertFalse(output.contains("O"), "A kimenetnek nem szabad tartalmaznia 'O' karaktert");
        }

        @Test
        @DisplayName("A Board inicializáláskor üres táblát hoz létre")
        void testBoardInitialization() {
            // AMIKOR - a Board létrejön a @BeforeEach-ben

            // AKKOR - minden cella üres legyen
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    assertEquals('.', board.getSymbolAt(row, col),
                            "A tábla inicializáláskor minden cellának '.'-nek kell lennie");
                }
            }
        }

        @Test
        @DisplayName("A getSize() metódus visszaadja a tábla méretét")
        void testGetSize() {
            // AMIKOR - a Board létrejön

            // AKKOR - a méret 10 legyen
            assertEquals(10, board.getSize(), "A tábla méretének 10-nek kell lennie");
        }

        @Test
        @DisplayName("A placeSymbol() metódus helyesen elhelyez egy X szimbólumot")
        void testPlaceSymbolXValid() {
            // AMIKOR
            boolean result = board.placeSymbol(0, 0, 'X');

            // AKKOR
            assertTrue(result, "A placeSymbol() visszatérési értéke true kell legyen érvényes lépésnél");
            assertEquals('X', board.getSymbolAt(0, 0),
                    "A 0,0 pozíción 'X' szimbólumnak kell lennie");
        }

        @Test
        @DisplayName("A placeSymbol() metódus helyesen elhelyez egy O szimbólumot")
        void testPlaceSymbolOValid() {
            // AMIKOR
            boolean result = board.placeSymbol(5, 5, 'O');

            // AKKOR
            assertTrue(result, "A placeSymbol() visszatérési értéke true kell legyen érvényes lépésnél");
            assertEquals('O', board.getSymbolAt(5, 5),
                    "A 5,5 pozíción 'O' szimbólumnak kell lennie");
        }

        @Test
        @DisplayName("A placeSymbol() metódus hamissal tér vissza érvénytelen pozíció esetén")
        void testPlaceSymbolInvalidPosition() {
            // AMIKOR
            boolean result1 = board.placeSymbol(-1, 0, 'X');
            boolean result2 = board.placeSymbol(0, 10, 'X');
            boolean result3 = board.placeSymbol(10, 10, 'X');

            // AKKOR
            assertFalse(result1, "Negatív sor esetén false-t kell visszaadnia");
            assertFalse(result2, "Túl nagy oszlop esetén false-t kell visszaadnia");
            assertFalse(result3, "Túl nagy sor és oszlop esetén false-t kell visszaadnia");
        }

        @Test
        @DisplayName("A placeSymbol() metódus hamissal tér vissza foglalt cella esetén")
        void testPlaceSymbolOccupiedCell() {
            // AMIKOR
            board.placeSymbol(0, 0, 'X');
            boolean result = board.placeSymbol(0, 0, 'O');

            // AKKOR
            assertFalse(result, "Foglalt cella esetén false-t kell visszaadnia");
            assertEquals('X', board.getSymbolAt(0, 0),
                    "A foglalt cella szimbóluma nem változhat meg");
        }

        @Test
        @DisplayName("A placeSymbol() metódus hamissal tér vissza érvénytelen játékos szimbólum esetén")
        void testPlaceSymbolInvalidPlayer() {
            // AMIKOR
            boolean result1 = board.placeSymbol(0, 0, 'A');
            boolean result2 = board.placeSymbol(0, 0, 'Y');
            boolean result3 = board.placeSymbol(0, 0, ' ');

            // AKKOR
            assertFalse(result1, "Érvénytelen szimbólum ('A') esetén false-t kell visszaadnia");
            assertFalse(result2, "Érvénytelen szimbólum ('Y') esetén false-t kell visszaadnia");
            assertFalse(result3, "Érvénytelen szimbólum (' ') esetén false-t kell visszaadnia");
        }

        @Test
        @DisplayName("Az isValidPosition() metódus helyesen működik")
        void testIsValidPosition() {
            // AMIKOR & AKKOR - érvényes pozíciók
            assertTrue(board.isValidPosition(0, 0), "0,0 pozíciónak érvényesnek kell lennie");
            assertTrue(board.isValidPosition(9, 9), "9,9 pozíciónak érvényesnek kell lennie");
            assertTrue(board.isValidPosition(5, 5), "5,5 pozíciónak érvényesnek kell lennie");

            // AMIKOR & AKKOR - érvénytelen pozíciók
            assertFalse(board.isValidPosition(-1, 0), "-1,0 pozíciónak érvénytelennek kell lennie");
            assertFalse(board.isValidPosition(0, -1), "0,-1 pozíciónak érvénytelennek kell lennie");
            assertFalse(board.isValidPosition(10, 0), "10,0 pozíciónak érvénytelennek kell lennie");
            assertFalse(board.isValidPosition(0, 10), "0,10 pozíciónak érvénytelennek kell lennie");
        }

        @Test
        @DisplayName("Az isEmptyCell() metódus helyesen működik")
        void testIsEmptyCell() {
            // AMIKOR & AKKOR - üres cellák
            assertTrue(board.isEmptyCell(0, 0), "Üres cellának true-t kell visszaadnia");
            assertTrue(board.isEmptyCell(9, 9), "Üres cellának true-t kell visszaadnia");

            // AMIKOR - cella kitöltése
            board.placeSymbol(0, 0, 'X');

            // AKKOR - foglalt cella
            assertFalse(board.isEmptyCell(0, 0), "Foglalt cellának false-t kell visszaadnia");

            // AMIKOR & AKKOR - érvénytelen pozíció
            assertFalse(board.isEmptyCell(-1, 0), "Érvénytelen pozíciónak false-t kell visszaadnia");
        }

        @Test
        @DisplayName("Az isValidPlayer() metódus helyesen működik")
        void testIsValidPlayer() {
            // AMIKOR & AKKOR - érvényes játékosok
            assertTrue(board.isValidPlayer('X'), "'X' érvényes játékos szimbólum");
            assertTrue(board.isValidPlayer('O'), "'O' érvényes játékos szimbólum");

            // AMIKOR & AKKOR - érvénytelen játékosok
            assertFalse(board.isValidPlayer('A'), "'A' érvénytelen játékos szimbólum");
            assertFalse(board.isValidPlayer('Y'), "'Y' érvénytelen játékos szimbólum");
            assertFalse(board.isValidPlayer(' '), "Space érvénytelen játékos szimbólum");
            assertFalse(board.isValidPlayer('x'), "Kis 'x' érvénytelen játékos szimbólum");
            assertFalse(board.isValidPlayer('o'), "Kis 'o' érvénytelen játékos szimbólum");
        }

        @Test
        @DisplayName("A getSymbolAt() metódus helyesen működik")
        void testGetSymbolAt() {
            // AMIKOR - szimbólum elhelyezése
            board.placeSymbol(0, 0, 'X');
            board.placeSymbol(5, 5, 'O');

            // AKKOR - helyes szimbólumok
            assertEquals('X', board.getSymbolAt(0, 0), "0,0 pozíción 'X'-nek kell lennie");
            assertEquals('O', board.getSymbolAt(5, 5), "5,5 pozíción 'O'-nak kell lennie");
            assertEquals('.', board.getSymbolAt(1, 1), "Üres cellánál '.'-nek kell lennie");

            // AKKOR - érvénytelen pozíció
            assertEquals('.', board.getSymbolAt(-1, 0), "Érvénytelen pozíción '.'-nek kell lennie");
        }

        @Test
        @DisplayName("Az isFull() metódus helyesen működik üres tábla esetén")
        void testIsFullEmptyBoard() {
            // AMIKOR - üres tábla

            // AKKOR
            assertFalse(board.isFull(), "Üres tábla esetén isFull() false-t kell adjon");
        }

        @Test
        @DisplayName("Az isFull() metódus helyesen működik majdnem teli tábla esetén")
        void testIsFullAlmostFullBoard() {
            // AMIKOR - majdnem teli tábla (1 cella hiányzik)
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    if (!(row == 9 && col == 9)) { // Az utolsó cella üresen hagyása
                        board.placeSymbol(row, col, 'X');
                    }
                }
            }

            // AKKOR
            assertFalse(board.isFull(), "Majdnem teli tábla esetén isFull() false-t kell adjon");
        }

        @Test
        @DisplayName("Az isFull() metódus helyesen működik teli tábla esetén")
        void testIsFullCompleteBoard() {
            // AMIKOR - teli tábla
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    board.placeSymbol(row, col, (row + col) % 2 == 0 ? 'X' : 'O');
                }
            }

            // AKKOR
            assertTrue(board.isFull(), "Teli tábla esetén isFull() true-t kell adjon");
        }

        @Test
        @DisplayName("A print() metódus formázása helyes")
        void testPrintFormat() {
            // AMIKOR
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            board.print();

            // AKKOR - ellenőrizzük a formázást
            String output = outContent.toString();
            assertTrue(output.contains("+"), "A kimenetnek tartalmaznia kell '+' karaktereket a vonalakhoz");
            assertTrue(output.contains("|"), "A kimenetnek tartalmaznia kell '|' karaktereket a cellákhoz");
            assertTrue(output.contains("---"), "A kimenetnek tartalmaznia kell '---' karaktereket a vonalakhoz");
        }

        @Test
        @DisplayName("A print() metódus megjeleníti a helyes fejlécet")
        void testPrintHeader() {
            // AMIKOR
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            board.print();

            // AKKOR - fejléc ellenőrzése
            String output = outContent.toString();
            assertTrue(output.contains("1"), "A fejlécnek tartalmaznia kell oszlop számokat");
            assertTrue(output.contains("10"), "A fejlécnek tartalmaznia kell a 10-es számot");
        }

    }
