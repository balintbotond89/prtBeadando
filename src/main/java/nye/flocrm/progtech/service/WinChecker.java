package nye.flocrm.progtech.service;

import nye.flocrm.progtech.model.Board;

/**
 * Szolgáltatás a győzelmi feltétel meghatározására
 */
public class WinChecker {

    private static final int WINNING_LENGTH = 5;

    /**
     * Ellenőrzés, hogy az utolsó lépés nyertes-e?
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
     * A kezdőpontot is beleszámolja, majd hozzáadja a bal/jobb irányokban találtakat.
     *
     * @param board a játéktábla
     * @param row a kezdő sor indexe
     * @param col a kezdő oszlop indexe
     * @param symbol a keresett szimbólum ('X' vagy 'O')
     * @param rowDir az első irány sor változása
     * @param colDir az első irány oszlop változása
     * @return az összes egymás mellett lévő azonos szimbólum száma a teljes vonalban
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