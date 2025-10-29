package nye.flocrm.progtech.model;

import nye.flocrm.progtech.service.WinChecker;
import java.util.Random;

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
    public char getSymbol() {
        return symbol;
    }

    @Override
    public void makeMove(Board board) {
        // AI gondolkodása: először megpróbál nyerni, majd blokkolja az ellenfelet, végül véletlenszerűen lép.
        int[] winningMove = findWinningMove(board, symbol);
        if (winningMove != null) {
            board.placeSymbol(winningMove[0], winningMove[1], symbol);
            return;
        }

        int[] blockingMove = findWinningMove(board, opponentSymbol);
        if (blockingMove != null) {
            board.placeSymbol(blockingMove[0], blockingMove[1], symbol);
            return;
        }

        makeRandomMove(board);
    }

    private int[] findWinningMove(Board board, char playerSymbol) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.isEmptyCell(row, col)) {
                    // Try placing the symbol temporarily
                    board.placeSymbol(row, col, playerSymbol);
                    boolean wouldWin = winChecker.checkWin(board, row, col);
                    // Undo the move
                    board.placeSymbol(row, col, '.');

                    if (wouldWin) {
                        return new int[]{row, col};
                    }
                }
            }
        }
        return null;
    }

    private void makeRandomMove(Board board) {
        int attempts = 0;
        int maxAttempts = Board.SIZE * Board.SIZE;

        while (attempts < maxAttempts) {
            int row = random.nextInt(Board.SIZE);
            int col = random.nextInt(Board.SIZE);

            if (board.isEmptyCell(row, col)) {
                board.placeSymbol(row, col, symbol);
                return;
            }
            attempts++;
        }

        // AI nem talál jó stratégiai lépést, így véletlenszerűen választ
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.isEmptyCell(row, col)) {
                    board.placeSymbol(row, col, symbol);
                    return;
                }
            }
        }
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}