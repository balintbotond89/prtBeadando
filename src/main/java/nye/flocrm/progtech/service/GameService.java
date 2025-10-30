package nye.flocrm.progtech.service;

import nye.flocrm.progtech.model.*;

/**
 * A fő játékmódokat megvalósító játékszolgáltatás.
 */
public class GameService {

    private final Board board;
    private final WinChecker winChecker;
    private GameState gameState;
    private Player currentPlayer;
    private Player player1;
    private Player player2;
    private GameMode gameMode;

    public GameService(GameMode gameMode) {
        this.board = new Board();
        this.winChecker = new WinChecker();
        this.gameState = GameState.IN_PROGRESS;
        this.gameMode = gameMode;
        initializePlayers();
    }

    private void initializePlayers() {
        switch (gameMode) {
            case HUMAN_VS_HUMAN:
                this.player1 = new HumanPlayer("Játkos 1", 'X');
                this.player2 = new HumanPlayer("Játékos 2", 'O');
                break;
            case HUMAN_VS_AI:
                this.player1 = new HumanPlayer("Ember", 'X');
                this.player2 = new AIPlayer("Számítógép", 'O', 'X');
                break;
        }
        this.currentPlayer = player1;
    }

    /**
     * Lépést hajt végre a megadott pozíción és frissíti a játék állapotát.
     * A metódus ellenőrzi a lépés érvényességét, megvizsgálja a nyerési feltételeket,
     * és váltja a játékosokat. Automatikusan indítja az AI lépését szükség esetén.
     *
     * @return true ha a lépés sikeres volt, false ha érvénytelen (pl. foglalt mező vagy befejezett játék)
     */
    public boolean makeMove(int row, int col) {
        if (gameState != GameState.IN_PROGRESS || !board.placeSymbol(row, col, currentPlayer.getSymbol())) {
            return false;
        }

        if (winChecker.checkWin(board, row, col)) {
            gameState = (currentPlayer.getSymbol() == 'X') ? GameState.PLAYER_X_WON : GameState.PLAYER_O_WON;
        } else if (board.isFull()) {
            gameState = GameState.DRAW;
        } else {
            switchPlayer();

            // Ha a következő játékos AI, akkor az AI automatikusan lép.
            if (!currentPlayer.isHuman() && gameState == GameState.IN_PROGRESS) {
                makeAIMove();
            }
        }

        return true;
    }

    /**
     * Az AI végrehajt egy automatikus lépést.
     * A metódus csak akkor fut le, ha a játék éppen folyamatban van és az aktuális játékos
     * egy AIPlayer példány. Az AI először megpróbálja megtalálni a nyerő lépést, ha ez nem
     * sikerül, akkor az ellenfél nyerő lépését próbálja blokkolni, végül pedig véletlenszerűen
     * választ egy érvényes pozíciót.
     *
     * A lépés végrehajtása után a metódus átvizsgálja a játék állapotát:
     * - Ellenőrzi, hogy az AI lépése nyerő pozíciót hozott-e létre
     * - Megállapítja, hogy a tábla megtelt-e (döntetlen állapot)
     * - Ha a játék még folyamatban van, visszavált az emberi játékosra
     * A folyamat során konzolüzenet jelzi a felhasználónak, hogy az AI éppen gondolkodik.
     */
    public void makeAIMove() {
        if (currentPlayer instanceof AIPlayer && gameState == GameState.IN_PROGRESS) {
            System.out.println("\n" + currentPlayer.getName() + " tervezás...");
            currentPlayer.makeMove(board);

            // Find where the AI placed its symbol
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    if (board.getSymbolAt(row, col) == currentPlayer.getSymbol()) {
                        // Check if this move resulted in a win
                        if (winChecker.checkWin(board, row, col)) {
                            gameState = (currentPlayer.getSymbol() == 'X') ?
                                    GameState.PLAYER_X_WON : GameState.PLAYER_O_WON;
                        } else if (board.isFull()) {
                            gameState = GameState.DRAW;
                        } else {
                            switchPlayer();
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * A játékos váltást megvalósító metódus.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Visszaállítja a játékot a kezdeti állapotrára.
     */
    public void resetGame() {
        board.clear();
        gameState = GameState.IN_PROGRESS;
        currentPlayer = player1;
    }

    /**
     * Megváltoztatja a játék módot és visszaállítja a kezdeti állapotára
     */
    public void changeGameMode(GameMode newGameMode) {
        this.gameMode = newGameMode;
        initializePlayers();
        resetGame();
    }

    // Getterek:
    public Board getBoard() {
        return board;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    /**
     * Az aktuális játék állapot kiírása.
     */
    public void printGameState() {
        board.print();
        System.out.println("\nJelenlegi játékos: " + currentPlayer.getName() + " (" + currentPlayer.getSymbol() + ")");
        System.out.println("Játék mód: " + gameMode.getDisplayName());
        System.out.println("Játék állapot: " + gameState);
    }
}
