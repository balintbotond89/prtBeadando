package nye.flocrm.progtech.model;

/**
 * Az emberi játékos (Human player) implementációja.
 */
public class HumanPlayer implements Player {
    private String name;
    private final char symbol;
    private int score;

    /**
     * Konstruktor az emberi játékos inicializálásához.
     *
     * @param name a játékos neve
     * @param symbol a játékos szimbóluma ('X' vagy 'O')
     */
    public HumanPlayer(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
        this.score = 0;
    }

    /**
     * Visszaadja a játékos nevét.
     *
     * @return a játékos neve
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Beállítja a játékos nevét.
     *
     * @param name az új név
     */
    @Override
    public void setName(String name) {
            this.name = name.trim();
    }

    /**
     * Visszaadja a játékos szimbólumát.
     *
     * @return a játékos szimbóluma
     */
    @Override
    public char getSymbol() {
        return symbol;
    }

    /**
     * Lépést hajt végre a táblán (üres implementáció, a felhasználói felület kezeli).
     *
     * @param board a játéktábla
     */
    @Override
    public void makeMove(Board board) {
    }

    /**
     * Megadja, hogy a játékos emberi-e.
     *
     * @return mindig true
     */
    @Override
    public boolean isHuman() {
        return true;
    }

    /**
     * Visszaadja a játékos pontszámát.
     *
     * @return a pontszám
     */
    @Override
    public int getScore() {
        return score;
    }

    /**
     * Beállítja a játékos pontszámát.
     *
     * @param score az új pontszám
     */
    @Override
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Hozzáad pontokat a játékos aktuális pontszámához.
     *
     * @param points a hozzáadandó pontok száma
     */
    @Override
    public void addScore(int points) {
        this.score += points;
    }
}