package nye.flocrm.progtech.model;

/**
 * Interfész a játék összes szereplőjének.
 */
public interface Player {

    String getName();

    char getSymbol();

    void makeMove(Board board);

    boolean isHuman();
}