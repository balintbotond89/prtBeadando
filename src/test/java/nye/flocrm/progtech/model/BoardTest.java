    package nye.flocrm.progtech.model;

    import java.io.ByteArrayOutputStream;
    import java.io.PrintStream;

    import nye.flocrm.progtech.model.Board;

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

            // AMIKOR: -> kiindulási körülmények
            board.clear();

            // AMIT / AHOGY: -> ami ténylegesn vizsgálandó feltétel, történés, esetmény
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            board.print();

            // AKKOR : -> elvárt eredmény és annak ellenőrzése
            String output = outContent.toString();
            assertTrue(output.lines().allMatch(line -> line.replace(" ", "").chars().allMatch(ch -> ch == '.')),
                    "A clear() után csak '.' karakterek maradhatnak a táblán");

        }

        @Test
        @DisplayName("A print() metódus megfelelő formátumban jeleníti meg a táblát")
        void testPrintOutputFormat() {
            // AMIKOR
            board.clear();

            // AMIT
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            board.print();

            // AKKOR
            String output = outContent.toString();
            String[] lines = output.split(System.lineSeparator());

            // Ellenőrizzük, hogy van fejléc
            assertTrue(lines[0].contains("1"), "A print() kimenetének tartalmaznia kell oszlopszámokat");

            // Ellenőrizzük, hogy vannak vízszintes elválasztók
            assertTrue(output.contains("+"), "A print() kimenetének tartalmaznia kell elválasztó vonalakat");
            assertTrue(output.contains("---"), "A print() kimenetének tartalmaznia kell cella elválasztókat");

            // Ellenőrizzük, hogy vannak függőleges elválasztók
            assertTrue(output.contains("|"), "A print() kimenetének tartalmaznia kell függőleges elválasztókat");

            // Ellenőrizzük, hogy minden sor tartalmaz üres cellákat
            long dotCount = output.chars().filter(ch -> ch == '.').count();
            assertEquals(100, dotCount, "A print() kimenetének pontosan 100 '.' karaktert kell tartalmaznia");
        }

    }
