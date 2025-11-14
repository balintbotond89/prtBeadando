package nye.flocrm.progtech.model;

/**
 * A játék lehetséges állapotai.
 */
public enum GameState {
    IN_PROGRESS("Folyamatban"),
    PLAYER_X_WON("X játékos nyert"),
    PLAYER_O_WON("O játékos nyert"),
    DRAW("Döntetlen");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Visszaadja a játékállapot szöveges reprezentációját.
     *
     * @return a játékállapot leírása
     */
    @Override
    public String toString() {
        return displayName;
    }
}