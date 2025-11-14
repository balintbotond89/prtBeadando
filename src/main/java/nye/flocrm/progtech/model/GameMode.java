package nye.flocrm.progtech.model;

/**
 * A játék lehetséges játékmódjai.
 */
public enum GameMode {
    HUMAN_VS_HUMAN("Ember vs Ember"),
    HUMAN_VS_AI("Ember vs Számítógép");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Visszaadja a játékmód megjelenítendő nevét.
     *
     * @return a megjelenítendő név
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Visszaadja a játékmód szöveges reprezentációját.
     *
     * @return a játékmód neve
     */
    @Override
    public String toString() {
        return displayName;
    }
}