package nye.flocrm.progtech.model;

/**
 * Pontozható objektumok interfésze.
 * Biztosítja a pontszám kezelésének alapvető műveleteit.
 */
public interface Scoreable {

    /**
     * Visszaadja az objektum pontszámát.
     *
     * @return a pontszám
     */
    int getScore();

    /**
     * Beállítja az objektum pontszámát.
     *
     * @param score az új pontszám
     */
    void setScore(int score);

    /**
     * Hozzáad pontokat az objektum aktuális pontszámához.
     *
     * @param points a hozzáadandó pontok száma
     */
    void addScore(int points);
}