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


}
