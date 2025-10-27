package nye.flocrm.progtech.model;

import java.util.Arrays;

public class Board {
    public static final int SIZE = 10;

    private char[][] grid;

    public Board() {
        grid = new char[SIZE][SIZE];
        clear();
    }

    public void clear() {
        for (final char[] row : grid) {
            Arrays.fill(row, '.');
        }
    }

    public void print(){
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
