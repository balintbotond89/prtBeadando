package nye.flocrm.progtech.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import nye.flocrm.progtech.model.Board;

public class WinCheckerTest {

    private WinChecker winChecker;
    private Board board;

    @BeforeEach
    void setUp() {
        winChecker = new WinChecker();
        board = new Board();
    }

    @Test
    @DisplayName("A checkWin() metódus false-t ad vissza üres cellán")
    void testCheckWinOnEmptyCell() {
        // AMIKOR
        boolean result = winChecker.checkWin(board, 0, 0);

        // AKKOR
        assertFalse(result, "Üres cellán false-t kell visszaadnia");
    }

    @Test
    @DisplayName("A checkWin() metódus észleli a vízszintes győzelmet")
    void testCheckWinHorizontal() {
        // AMIKOR - 5 egymás melletti X a sorban
        for (int col = 0; col < 5; col++) {
            board.placeSymbol(0, col, 'X');
        }

        // AKKOR
        assertTrue(winChecker.checkWin(board, 0, 2), "Vízszintes győzelem észlelése");
    }

    @Test
    @DisplayName("A checkWin() metódus észleli a függőleges győzelmet")
    void testCheckWinVertical() {
        // AMIKOR - 5 egymás alatti X az oszlopban
        for (int row = 0; row < 5; row++) {
            board.placeSymbol(row, 0, 'X');
        }

        // AKKOR
        assertTrue(winChecker.checkWin(board, 2, 0), "Függőleges győzelem észlelése");
    }

    @Test
    @DisplayName("A checkWin() metódus észleli az átlós győzelmet")
    void testCheckWinDiagonal() {
        // AMIKOR - 5 X az átlóban
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(i, i, 'X');
        }

        // AKKOR
        assertTrue(winChecker.checkWin(board, 2, 2), "Átlós győzelem észlelése");
    }

    @Test
    @DisplayName("A checkWin() metódus észleli a fordított átlós győzelmet")
    void testCheckWinAntiDiagonal() {
        // AMIKOR - 5 X a fordított átlóban
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(i, 4 - i, 'X');
        }

        // AKKOR
        assertTrue(winChecker.checkWin(board, 2, 2), "Fordított átlós győzelem észlelése");
    }

    @Test
    @DisplayName("A checkWinForPlayer() metódus helyesen működik X játékosra")
    void testCheckWinForPlayerX() {
        // AMIKOR - X nyer
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(0, i, 'X');
        }

        // AKKOR
        assertTrue(winChecker.checkWinForPlayer(board, 'X'), "X játékos győzelmének észlelése");
        assertFalse(winChecker.checkWinForPlayer(board, 'O'), "O játékosnak nem szabad nyernie");
    }

    @Test
    @DisplayName("A checkWinForPlayer() metódus helyesen működik O játékosra")
    void testCheckWinForPlayerO() {
        // AMIKOR - O nyer
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(0, i, 'O');
        }

        // AKKOR
        assertTrue(winChecker.checkWinForPlayer(board, 'O'), "O játékos győzelmének észlelése");
        assertFalse(winChecker.checkWinForPlayer(board, 'X'), "X játékosnak nem szabad nyernie");
    }

    @Test
    @DisplayName("A checkWinForPlayer() metódus false-t ad vissza, ha nincs győzelem")
    void testCheckWinForPlayerNoWin() {
        // AMIKOR - nincs győzelem
        board.placeSymbol(0, 0, 'X');
        board.placeSymbol(0, 1, 'O');

        // AKKOR
        assertFalse(winChecker.checkWinForPlayer(board, 'X'), "Nem szabad győzelmet észlelni X-re");
        assertFalse(winChecker.checkWinForPlayer(board, 'O'), "Nem szabad győzelmet észlelni O-re");
    }

    @Test
    @DisplayName("A checkWin() metódus nem észlel győzelmet 4 egymás mellett lévő szimbólumra")
    void testCheckWinFourInRow() {
        // AMIKOR - csak 4 egymás mellett lévő X
        for (int col = 0; col < 4; col++) {
            board.placeSymbol(0, col, 'X');
        }

        // AKKOR
        assertFalse(winChecker.checkWin(board, 0, 1), "4 egymás mellett lévő szimbólum nem nyer");
    }

    @Test
    @DisplayName("A checkWin() metódus érzékeli a győzelmet a tábla szélén")
    void testCheckWinAtBoardEdge() {
        // AMIKOR - győzelem a tábla jobb szélén
        for (int col = 5; col < 10; col++) {
            board.placeSymbol(0, col, 'X');
        }

        // AKKOR
        assertTrue(winChecker.checkWin(board, 0, 7), "Győzelem észlelése a tábla szélén");
    }
}