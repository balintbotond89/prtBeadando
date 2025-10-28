package nye.flocrm.progtech.model;

/**
 * Az emberi játékos (Human player) implementációja.
 */
public class HumanPlayer implements Player {
    private final String name;
    private final char symbol;

    public HumanPlayer(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
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
        // Human move is handled by the UI, this is just a placeholder
        // The actual move input comes from the console/UI
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}