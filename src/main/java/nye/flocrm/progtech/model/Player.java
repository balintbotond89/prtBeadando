package nye.flocrm.progtech.model;

/**
 * A játékosok által implementálandó interfész.
 * Definíciója a játékosok alapvető műveleteinek és tulajdonságainak.
 */
public interface Player {

    /**
     * Visszaadja a játékos nevét.
     *
     * @return a játékos neve
     */
    String getName();

    /**
     * Beállítja a játékos nevét.
     *
     * @param name az új név
     */
    void setName(String name);

    /**
     * Visszaadja a játékos szimbólumát.
     *
     * @return a játékos szimbóluma ('X' vagy 'O')
     */
    char getSymbol();

    /**
     * Lépést hajt végre a megadott táblán.
     *
     * @param board a játéktábla
     */
    void makeMove(Board board);

    /**
     * Megadja, hogy a játékos emberi-e.
     *
     * @return true ha emberi játékos, false ha AI
     */
    boolean isHuman();

    /**
     * Visszaadja a játékos pontszámát.
     *
     * @return a pontszám
     */
    default int getScore() {
        return 0; // Alapértelmezett implementáció
    }

    /**
     * Beállítja a játékos pontszámát.
     *
     * @param score az új pontszám
     */
    default void setScore(int score) {
        // Alapértelmezett implementáció - üres, nincs szükség tárolásra
    }

    /**
     * Hozzáad pontokat a játékos aktuális pontszámához.
     *
     * @param points a hozzáadandó pontok száma
     */
    default void addScore(int points) {
        // Alapértelmezett implementáció - üres
    }
}