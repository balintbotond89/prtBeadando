package nye.flocrm.progtech.model;

import java.util.Random;

import nye.flocrm.progtech.service.WinChecker;

/**
 * Mesterséges intelligencia játékos implementáció.
 * Az AI először megpróbál nyerni, majd blokkolja az ellenfelet,
 * végül véletlenszerű lépést hajt végre.
 */
public class
AIPlayer implements Player {
    private final String name;
    private final char symbol;
    private final char opponentSymbol;
    private final Random random;
    private final WinChecker winChecker;

    /**
     * Konstruktor az AI játékos inicializálásához.
     *
     * @param name a játékos neve
     * @param symbol a játékos szimbóluma ('X' vagy 'O')
     * @param opponentSymbol az ellenfél szimbóluma
     */
    public AIPlayer(String name, char symbol, char opponentSymbol) {
        this.name = name;
        this.symbol = symbol;
        this.opponentSymbol = opponentSymbol;
        this.random = new Random();
        this.winChecker = new WinChecker();
    }

    /**
     * Visszaadja a játékos nevét.
     *
     * @return a játékos neve
     */
    @Override
    public String getName() {
        return name;
    }


    /**
     * Beállítja a játékos nevét (AI esetén nem használatos).
     *
     * @param name az új név
     */
    @Override
    public void setName(String name) {
    }

    /**
     * Visszaadja a játékos szimbólumát.
     *
     * @return a játékos szimbóluma
     */
    @Override
    public char getSymbol() {
        return symbol;
    }

    /**
     * Megadja, hogy a játékos emberi-e.
     *
     * @return mindig false, mivel ez AI játékos
     */
    @Override
    public boolean isHuman() {
        return false;
    }

    /**
     * Lépést hajt végre a táblán. Az AI stratégiája sorrendben:
     * 1. Először megpróbál nyerni egy lépéssel
     * 2. Ha nem tud nyerni, megpróbálja blokkolni az ellenfél nyerő lépését
     * 3. Ha egyik sem lehetséges, véletlenszerű lépést hajt végre
     *
     * @param board a játéktábla, amelyen a lépést végre kell hajtani
     */
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
     * A metódus végigiterál a tábla összes üres mezőjén, és mindegyikre
     * ellenőrzi, hogy ha az adott játékos oda helyezné a szimbólumát,
     * az nyerő lépés-e. A tesztelés egy másolatán történik a táblának,
     * hogy ne módosítsuk az eredeti állapotot.
     *
     * @param board a játéktábla
     * @param playerSymbol a játékos szimbóluma, akinek a nyerő lépését keressük
     * @return egy int tömb a [sor, oszlop] pozícióval, ha talált nyerő lépést,
     *         null egyébként
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
     *
     * @param original az eredeti tábla
     * @return a másolt tábla
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
     * Véletlenszerű lépést hajt végre egy üres mezőre.
     * A metódus addig generál véletlenszerű pozíciókat, amíg nem talál
     * egy üres cellát a táblán, majd oda helyezi a szimbólumot.
     *
     * @param board a játéktábla, amelyen a lépést végre kell hajtani
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
}