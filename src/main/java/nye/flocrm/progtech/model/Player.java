package nye.flocrm.progtech.model;

/**
 * Interfész a játék összes szereplőjének.
 */
public interface Player {

    String getName();

    void setName(String name);  // Emberi játékos neve

    char getSymbol();

    void makeMove(Board board);

    boolean isHuman();
}