package nye.flocrm.progtech.model;

import java.util.Random;

import nye.flocrm.progtech.service.WinChecker;


/**
 * AI játékos egyszerű játék mechanizmussal.
 */
public class AIPlayer implements Player {
    private final String name;
    private final char symbol;
    private final char opponentSymbol;
    private final Random random;
    private final WinChecker winChecker;

    public AIPlayer(String name, char symbol, char opponentSymbol) {
        this.name = name;
        this.symbol = symbol;
        this.opponentSymbol = opponentSymbol;
        this.random = new Random();
        this.winChecker = new WinChecker();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        // Nem csinál semmit - AI neve fix
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public void makeMove(Board board) {
        // 1. Próbál nyerni
        int[] winningMove = findWinningMove(board, symbol);
        if (winningMove != null) {
            board.placeSymbol(winningMove[0], winningMove[1], symbol);
            return;
        }

        // 2. Próbál blokkolni
        int[] blockingMove = findWinningMove(board, opponentSymbol);
        if (blockingMove != null) {
            board.placeSymbol(blockingMove[0], blockingMove[1], symbol);
            return;
        }

        // 3. Majd lép
        makeRandomMove(board);
    }

    /**
     * Megkeresi a nyerő lépést az adott játékos számára.
     * A metódus végigiterál a tábla összes üres mezőjén, és mindegyikre ellenőrzi,
     * hogy ha az adott játékos oda helyezné a szimbólumát, az nyerő lépés-e.
     * A tesztelés egy másolatán történik a táblának, hogy ne módosítsuk az eredeti állapotot.
     */
    private int[] findWinningMove(Board board, char playerSymbol) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.isEmptyCell(row, col)) {
                    // Másolatot készítünk a tábláról
                    Board testBoard = copyBoard(board);

                    // Teszteljük a lépést a másolaton
                    testBoard.placeSymbol(row, col, playerSymbol);
                    boolean wouldWin = winChecker.checkWin(testBoard, row, col);

                    if (wouldWin) {
                        return new int[]{row, col};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Másolatot készít a megadott tábláról.
     * A metódus létrehoz egy új Board példányt és pontosan másolja át
     * az összes szimbólumot az eredeti tábláról. Ez biztosítja, hogy
     * a tesztelések ne befolyásolják az eredeti játékállapotot.
     */
    private Board copyBoard(Board original) {
        Board copy = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                char symbol = original.getSymbolAt(row, col);
                if (symbol != '.') {
                    copy.placeSymbol(row, col, symbol);
                }
            }
        }
        return copy;
    }

    /**
     * Véletlenszerű lépést hajt végre.
     */
    private void makeRandomMove(Board board) {
        // Csak egy véletlen mezőt válasszon és egy jelet helyezzen rá
        int row;
        int col;
        do {
            row = random.nextInt(board.getSize());
            col = random.nextInt(board.getSize());
        } while (!board.isEmptyCell(row, col));

        board.placeSymbol(row, col, symbol);
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}