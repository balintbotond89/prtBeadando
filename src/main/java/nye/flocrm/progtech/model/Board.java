package nye.flocrm.progtech.model;

import java.util.Arrays;

/**
 * 10x10-es amőba játék tábla megvalósitása:
 */
public final class Board {

    //Statikus a program futása során nem megváltoztatható változók:
    public static final int SIZE = 10;
    private static final char EMPTY_CELL = '.';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';

    //Tömb amely a táblát alkotja:
    private final char[][] grid;

    /**
     * Létrehoz egy új, üres 10x10-es játéktáblát.
     */
    public Board() {
        grid = new char[SIZE][SIZE];
        clear();
    }

    // Getter a Board méretét adja vissza
    public int getSize() {
        return SIZE;
    }

    /**
     * Törli a táblát, és minden cellát üresre állít.
     */
    public void clear() {
        for (final char[] row : grid) {
            Arrays.fill(row, EMPTY_CELL);
        }
    }

    /**
     * A játékos szimbólumát a megadott pozicioba helyezzük.
     *
     * @param row a sor indexe (0-tól kezdődően)
     * @param col az oszlop indexe (0-tól kezdődően)
     * @param player a játékos szimbóluma (»X« vagy »O«)
     * @return true, ha a lépés sikeres volt, egyébként false
     */
    public boolean placeSymbol(int row, int col, char player) {
        if (!isValidPosition(row, col) || !isEmptyCell(row, col) || !isValidPlayer(player)) {
            return false;
        }
        grid[row][col] = player;
        return true;
    }

    /**
     * Ellenőrizzük, hogy az adott pozició érvényes-e a táblán.
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Ellenőrizzük, hogy egy az adott cella üres vagy sem.
     */
    public boolean isEmptyCell(int row, int col) {
        return isValidPosition(row, col) && grid[row][col] == EMPTY_CELL;
    }

    /**
     * Ellenőrizzük, a játékos szimbólumának érvényességét.
     */
    public boolean isValidPlayer(char player) {
        return player == PLAYER_X || player == PLAYER_O;
    }

    /**
     * Getter a megadott pozícióban lévő szimbólumnak.
     */
    public char getSymbolAt(int row, int col) {
        return isValidPosition(row, col) ? grid[row][col] : EMPTY_CELL;
    }

    /**
     * A tábla vizuális formázása és nyomtatása:
     */
    public void print() {
        printHeader();
        printGrid();
    }

    private void printHeader() {
        System.out.print("   ");
        for (int c = 0; c < SIZE; c++) {
            System.out.print(" " + (c + 1) + "  ");
        }
        System.out.println();
    }

    private void printGrid() {
        for (int r = 0; r < SIZE; r++) {
            printHorizontalLine();
            printRow(r);
        }
        printHorizontalLine();
    }

    /**
     * Segédfüggvény a printHeader függvényhez.
     */
    private void printHorizontalLine() {
        System.out.print("  +");
        for (int c = 0; c < SIZE; c++) {
            System.out.print("---+");
        }
        System.out.println();
    }

    /**
     * Segédfüggvény a printHeader függvényhez.
     */
    private void printRow(int row) {
        System.out.printf("%2d|", row + 1);
        for (int c = 0; c < SIZE; c++) {
            System.out.print(" " + grid[row][c] + " |");
        }
        System.out.println();
    }

    /**
     * A függvény megvizsgálja, hogy megtelt-e a tábla.
     */
    public boolean isFull() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] == EMPTY_CELL) {
                    return false;
                }
            }
        }
        return true;
    }
}