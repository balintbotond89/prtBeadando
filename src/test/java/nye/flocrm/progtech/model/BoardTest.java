    package nye.flocrm.progtech.model;

    import java.io.ByteArrayOutputStream;
    import java.io.PrintStream;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    /**
     *
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

    }
