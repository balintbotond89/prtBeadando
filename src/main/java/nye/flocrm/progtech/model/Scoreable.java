package nye.flocrm.progtech.model;

/**
 * Külön interfész csak a pontszámhoz.
 */
public interface Scoreable {
    int getScore();
    void setScore(int score);
    void addScore(int points);
}