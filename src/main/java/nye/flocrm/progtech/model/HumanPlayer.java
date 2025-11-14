package nye.flocrm.progtech.model;

/**
 * Az emberi játékos (Human player) implementációja.
 */
public class HumanPlayer implements Player {
    private String name;
    private final char symbol;
    private int score;

    public HumanPlayer(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
        this.score = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
            this.name = name.trim();
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public void makeMove(Board board) {
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void addScore(int points) {
        this.score += points;
    }
}