package nye.flocrm.progtech.model;

/**
 * Interfész a játék összes szereplőjének.
 */
public interface Player {
    String getName();
    void setName(String name);
    char getSymbol();
    void makeMove(Board board);
    boolean isHuman();

    // Default implementáció - NEM KÖTELEZŐ felülírni
    default int getScore() {
        return 0; // Alapértelmezett implementáció
    }

    default void setScore(int score) {
        // Alapértelmezett implementáció - üres, ha nincs szükség tárolásra
    }

    default void addScore(int points) {
        // Alapértelmezett implementáció - üres
    }
}