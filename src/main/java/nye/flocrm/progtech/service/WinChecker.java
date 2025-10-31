package nye.flocrm.progtech.service;

import nye.flocrm.progtech.model.Board;

/**
 * Szolgáltatás a győzelmi feltétel meghatározására
 */
public class WinChecker {

    private static final int WINNING_LENGTH = 5;

    /**
     * Teljes tábla ellenőrzése minden szimbólumra
     */
    public boolean checkAnyWin(Board board) {
        // Ellenőrizzük minden nem üres mezőre
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                char symbol = board.getSymbolAt(row, col);
                if (symbol != '.' && checkWin(board, row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checkAnyWin Segédfüggvénye
     */
    public boolean checkWin(Board board, int lastRow, int lastCol) {
        char symbol = board.getSymbolAt(lastRow, lastCol);
        if (symbol == '.') {
            return false;
        }

        return checkHorizontal(board, lastRow, lastCol, symbol) ||
                checkVertical(board, lastRow, lastCol, symbol) ||
                checkDiagonal(board, lastRow, lastCol, symbol) ||
                checkAntiDiagonal(board, lastRow, lastCol, symbol);
    }

    /**
     * Megadja, hogy egy adott játékos nyert-e
     */
    public boolean checkWinForPlayer(Board board, char playerSymbol) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.getSymbolAt(row, col) == playerSymbol &&
                        checkWin(board, row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Vízszintes ellenőrzés
    private boolean checkHorizontal(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 0, 1) >= WINNING_LENGTH;
    }

    // Függőleges ellenőrzés
    private boolean checkVertical(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 1, 0) >= WINNING_LENGTH;
    }

    // Átlós ellenőrzés
    private boolean checkDiagonal(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 1, 1) >= WINNING_LENGTH;
    }

    // Fordított átlos ellenőrzés
    private boolean checkAntiDiagonal(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 1, -1) >= WINNING_LENGTH;
    }

    /**
     * Megszámolja, hány azonos szimbólum van egy vonalban mindkét irányba.
     */
    private int countConsecutive(Board board, int row, int col, char symbol, int rowDir, int colDir) {
        int count = 1;

        // Pozitív irányú számlálás
        count += countInDirection(board, row, col, symbol, rowDir, colDir);
        // Negatív irányú számlálás
        count += countInDirection(board, row, col, symbol, -rowDir, -colDir);

        return count;
    }

    /**
     * Segédmetódus: Megszámolja, hány azonos szimbólum van egy adott irányban egymás mellett.
     */
    private int countInDirection(Board board, int row, int col, char symbol, int rowDir, int colDir) {
        int count = 0;
        int currentRow = row + rowDir;
        int currentCol = col + colDir;

        while (board.isValidPosition(currentRow, currentCol) &&
                board.getSymbolAt(currentRow, currentCol) == symbol) {
            count++;
            currentRow += rowDir;
            currentCol += colDir;
        }

        return count;
    }
}