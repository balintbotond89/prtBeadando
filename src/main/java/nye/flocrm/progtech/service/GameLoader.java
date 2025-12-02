package nye.flocrm.progtech.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.GameMode;
import nye.flocrm.progtech.model.HumanPlayer;
import nye.flocrm.progtech.model.Player;

/**
 * Játékállapot mentését és betöltését végző osztály.
 * Kezeli a játék adatainak fájlba írását és visszaolvasását.
 */
public class GameLoader {
    private static final String SAVE_FILE = "game_save.txt";

    public record GameState(
            Board board,
            String player1Name,
            int player1Score,
            String player2Name,
            int player2Score,
            char nextPlayerSymbol,
            String timestamp,
            GameMode gameMode
    ) {}

    /**
     * Betölti a játékállapotot a megadott fájlból.
     *
     * @param filename a betöltendő fájl neve
     * @return a betöltött játékállapot
     * @throws IOException ha hiba történik a fájl olvasása során
     */
    public GameState loadGame(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            throw new IOException("A fájl üres vagy nem létezik: " + filename);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            String player1Name = null;
            int player1Score = 0;

            String player2Name = null;
            int player2Score = 0;

            char nextSymbol = 'X';
            GameMode gameMode = GameMode.HUMAN_VS_HUMAN;
            int boardSize = Board.SIZE;
            String timestamp = null;

            // ==== Fejléc sorok feldolgozása (üres sorig) ====
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {

                if (line.startsWith("Első játékos:")) {
                    player1Name = line.substring("Első játékos:".length()).trim();

                } else if (line.startsWith("Első játékos pontszám:")) {
                    String txt = line.substring("Első játékos pontszám:".length()).trim();
                    if (!txt.isEmpty()) {
                        player1Score = Integer.parseInt(txt);
                    }

                } else if (line.startsWith("Második játékos:")) {
                    player2Name = line.substring("Második játékos:".length()).trim();

                } else if (line.startsWith("Második játékos pontszám:")) {
                    String txt = line.substring("Második játékos pontszám:".length()).trim();
                    if (!txt.isEmpty()) {
                        player2Score = Integer.parseInt(txt);
                    }

                } else if (line.startsWith("Következő:")) {
                    String txt = line.substring("Következő:".length()).trim();
                    if (!txt.isEmpty()) {
                        nextSymbol = txt.charAt(0);
                    }

                } else if (line.startsWith("Játékmód:")) {
                    String txt = line.substring("Játékmód:".length()).trim();
                    if (!txt.isEmpty()) {
                        gameMode = GameMode.valueOf(txt);
                    }

                } else if (line.startsWith("Tábla mérete:")) {
                    String txt = line.substring("Tábla mérete:".length()).trim();
                    if (!txt.isEmpty()) {
                        boardSize = Integer.parseInt(txt);
                    }

                } else if (line.startsWith("Dátum:")) {
                    timestamp = line.substring("Dátum:".length()).trim();
                }
            }

            if (player1Name == null || player2Name == null) {
                throw new IOException("Hiányzó játékos nevek a fájlban.");
            }
            if (timestamp == null) {
                timestamp = new Date().toString();
            }

            // ==== Tábla beolvasása ====
            Board board = new Board();
            String boardLine;
            int row = 0;

            while ((boardLine = reader.readLine()) != null && row < boardSize) {

                if (boardLine.startsWith("| Sor\\Oszlop") || boardLine.trim().isEmpty()) {
                    continue;
                }

                if (boardLine.contains("|")) {
                    processBoardLine(board, boardLine, row);
                    row++;
                }
            }

            return new GameState(
                    board,
                    player1Name,
                    player1Score,
                    player2Name,
                    player2Score,
                    nextSymbol,
                    timestamp,
                    gameMode
            );

        } catch (NumberFormatException e) {
            throw new IOException("Érvénytelen számformátum a fájlban.", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Érvénytelen játékmód a fájlban.", e);
        }
    }

    /**
     * Betölti a játékállapotot az alapértelmezett fájlból.
     *
     * @return a betöltött játékállapot
     * @throws IOException ha hiba történik a fájl olvasása során
     */
    public GameState loadGame() throws IOException {
        return loadGame(SAVE_FILE);
    }

    /**
     * Feldolgoz egy tábla sort a mentett fájlból és beállítja a megfelelő cellákat a táblán.
     * A metódus a fájlban található formátumot dolgozza fel, ahol a sorok a következőképpen néznek ki:
     * {@code | 1 | X | . | O | ... |}
     *
     * <p>A feldolgozás a következő lépésekből áll:
     * <ol>
     *   <li>A sort felosztja '|' karakterek mentén</li>
     *   <li>Az első két elemet (üres és sor szám) átugorja</li>
     *   <li>A harmadik elemtől kezdve feldolgozza a cella értékeket</li>
     *   <li>Csak érvényes szimbólumokat ('X', 'O', '.') helyez el a táblán</li>
     * </ol>
     *
     * <p>A fájl formátuma miatt:
     * <ul>
     *   <li>{@code cells[0]} - üres string (a sor eleji '|' előtti rész)</li>
     *   <li>{@code cells[1]} - a sor száma</li>
     *   <li>{@code cells[2]} - az első cella értéke</li>
     *   <li>{@code cells[3]} - a második cella értéke</li>
     *   <li>... stb.</li>
     * </ul>
     *
     * @param board a játéktábla, amit feltöltünk a mentett adatokkal
     * @param line a fájlból beolvasott sor, amely a tábla egy sorát reprezentálja
     * @param row a sor indexe (0-tól kezdődően), ahova az adatokat helyezzük
     *
     * @see Board#placeSymbol(int, int, char)
     * @see GameLoader#loadGame()
     * @see GameLoader#loadGame(String)
     */
    private void processBoardLine(Board board, String line, int row) {
        String[] cells = line.split("\\|");

        // A cellák a 2. indextől kezdődnek (0: üres, 1: sor száma, 2: első cella)
        for (int col = 2; col < cells.length && (col - 2) < Board.SIZE; col++) {
            String cellValue = cells[col].trim();
            if (cellValue.length() == 1) {
                char symbol = cellValue.charAt(0);
                // Csak érvényes szimbólumokat helyezünk el
                if (symbol == 'X' || symbol == 'O' || symbol == '.') {
                    // A Board placeSymbol metódusát használjuk
                    // A cella indexe: col - 2, mert a 2. index az első cella
                    board.placeSymbol(row, col - 2, symbol);
                }
            }
        }
    }

    /**
     * Elmenti a játék aktuális állapotát fájlba.
     *
     * @param board a játéktábla
     * @param gameMode a játékmód
     * @throws IOException ha hiba történik a fájl írása során
     */
    public void saveGame(Board board,
                         Player player1,
                         Player player2,
                         Player currentPlayer,
                         GameMode gameMode) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {

            // 1. játékos adatai
            writer.println("Első játékos: " + player1.getName());
            int p1Score = 0;
            if (player1 instanceof HumanPlayer hp1) {
                p1Score = hp1.getScore();
            }
            writer.println("Első játékos pontszám: " + p1Score);

            // 2. játékos adatai
            writer.println("Második játékos: " + player2.getName());
            int p2Score = 0;
            if (player2 instanceof HumanPlayer hp2) {
                p2Score = hp2.getScore();
            }
            writer.println("Második játékos pontszám: " + p2Score);

            // Ki következik?
            writer.println("Következő: " + currentPlayer.getSymbol());

            // Játékmód, tábla méret, dátum
            writer.println("Játékmód: " + gameMode.name());
            writer.println("Tábla mérete: " + board.getSize());
            writer.println("Dátum: " + new Date());
            writer.println();

            // Tábla fejléce
            writer.print("| Sor\\Oszlop ");
            for (int i = 0; i < board.getSize(); i++) {
                writer.print("| " + (i + 1) + " ");
            }
            writer.println("|");

            // Tábla adatok
            for (int row = 0; row < board.getSize(); row++) {
                writer.print("| " + (row + 1) + " ");
                for (int col = 0; col < board.getSize(); col++) {
                    char cellValue = board.getSymbolAt(row, col);
                    writer.print("| " + cellValue + " ");
                }
                writer.println("|");
            }
        }
    }

    /**
     * Megadja, hogy létezik-e mentett játékállapot.
     *
     * @return true ha létezik mentett állapot, egyébként false
     */
    public boolean saveFileExists() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }
}