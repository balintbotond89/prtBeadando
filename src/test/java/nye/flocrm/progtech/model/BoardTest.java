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

}
