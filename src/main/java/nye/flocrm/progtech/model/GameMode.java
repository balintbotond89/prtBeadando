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

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}