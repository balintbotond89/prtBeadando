package nye.flocrm.progtech.service;

import nye.flocrm.progtech.model.Board;

/**
 * Szolgáltatás a győzelmi feltétel meghatározására
 */
public class WinChecker {

    private static final int WINNING_LENGTH = 5;

    /**
     * Ellenőrzi, hogy az adott pozícióból kiindulva létrejött-e győztes sorozat.
     * <p>
     * A metódus a legutóbb elhelyezett szimbólum (X vagy O) pozíciójából indul ki,
     * és négy irányban vizsgálja meg, hogy található-e legalább 5 egymást követő,
     * azonos szimbólumból álló sorozat:
     * <ul>
     *     <li>vízszintesen (balról jobbra),</li>
     *     <li>függőlegesen (felülről lefelé),</li>
     *     <li>főátló mentén (bal felsőtől jobb alsó felé),</li>
     *     <li>mellékátló mentén (jobb felsőtől bal alsó felé).</li>
     * </ul>
     *
     * @param board   a játéktábla, amelyen az ellenőrzés történik
     * @param lastRow a legutóbbi lépés sora (0–9 közötti index)
     * @param lastCol a legutóbbi lépés oszlopa (0–9 közötti index)
     * @return {@code true}, ha bármelyik irányban megtalálható egy 5 azonos jelből
     *         álló sorozat; {@code false} különben
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
     * Ellenőrzi, hogy az adott játékos nyert-e a táblán.
     *
     * @param board a játéktábla
     * @param playerSymbol a játékos szimbóluma ('X' vagy 'O')
     * @return true ha a játékos nyert, egyébként false
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

    /**
     * Vízszintes irányban ellenőrzi a nyerési feltételt.
     *
     * @param board a játéktábla
     * @param row a kezdő sor
     * @param col a kezdő oszlop
     * @param symbol az ellenőrzendő szimbólum
     * @return true ha van nyerő sorozat
     */
    private boolean checkHorizontal(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 0, 1) >= WINNING_LENGTH;
    }

    /**
     * Függőleges irányban ellenőrzi a nyerési feltételt.
     *
     * @param board a játéktábla
     * @param row a kezdő sor
     * @param col a kezdő oszlop
     * @param symbol az ellenőrzendő szimbólum
     * @return true ha van nyerő sorozat
     */
    private boolean checkVertical(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 1, 0) >= WINNING_LENGTH;
    }

    /**
     * Átlós irányban ellenőrzi a nyerési feltételt.
     *
     * @param board a játéktábla
     * @param row a kezdő sor
     * @param col a kezdő oszlop
     * @param symbol az ellenőrzendő szimbólum
     * @return true ha van nyerő sorozat
     */
    private boolean checkDiagonal(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 1, 1) >= WINNING_LENGTH;
    }

    /**
     * Fordított átlós irányban ellenőrzi a nyerési feltételt.
     *
     * @param board a játéktábla
     * @param row a kezdő sor
     * @param col a kezdő oszlop
     * @param symbol az ellenőrzendő szimbólum
     * @return true ha van nyerő sorozat
     */
    private boolean checkAntiDiagonal(Board board, int row, int col, char symbol) {
        return countConsecutive(board, row, col, symbol, 1, -1) >= WINNING_LENGTH;
    }

    /**
     * Megszámolja, hány azonos szimbólum van egy vonalban mindkét irányba.
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
     *
     * @param board a játéktábla
     * @param row a kezdő sor indexe
     * @param col a kezdő oszlop indexe
     * @param symbol a keresett szimbólum ('X' vagy 'O')
     * @param rowDir a sor iránya (-1: fel, 0: marad, 1: le)
     * @param colDir az oszlop iránya (-1: balra, 0: marad, 1: jobbra)
     * @return az azonos szimbólumok száma az adott irányban (a kezdőpontot nem számolva)
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