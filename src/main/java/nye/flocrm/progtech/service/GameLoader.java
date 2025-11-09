package nye.flocrm.progtech.service;

import nye.flocrm.progtech.model.Player;
import nye.flocrm.progtech.model.Board;
import nye.flocrm.progtech.model.HumanPlayer;
import nye.flocrm.progtech.model.GameMode;
import java.io.*;
import java.util.Date;

public class GameLoader {
    private static final String SAVE_FILE = "game_save.txt";

    public record GameState(Board board, Player player, String timestamp, GameMode gameMode) {
    }

    public GameState loadGame() throws IOException {
        File file = new File(SAVE_FILE);

        if (!file.exists() || file.length() == 0) {
            throw new IOException("A fájl üres vagy nem létezik.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            HumanPlayer player = null;
            Board board;
            GameMode gameMode = GameMode.HUMAN_VS_AI; // alapértelmezett
            int boardSize = Board.SIZE;

            // Fejléc sorok feldolgozása
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                if (line.startsWith("Játékos:")) {
                    String playerName = line.substring(9).trim();
                    player = new HumanPlayer(playerName, 'X');
                } else if (line.startsWith("Pontszám:") && player != null) {
                    int score = Integer.parseInt(line.substring(10).trim());
                    player.setScore(score);
                } else if (line.startsWith("Tábla mérete:")) {
                    boardSize = Integer.parseInt(line.substring(14).trim());
                } else if (line.startsWith("Játékmód:")) {
                    String modeName = line.substring(10).trim();
                    gameMode = GameMode.valueOf(modeName);
                }
            }

            if (player == null) {
                throw new IOException("Hiányzó játékos információ a fájlban.");
            }

            // Tábla létrehozása
            board = new Board();

            // Tábla adatok beolvasása
            String boardLine;
            int row = 0;
            while ((boardLine = reader.readLine()) != null && row < boardSize) {
                // Fejléc sor átugrása
                if (boardLine.startsWith("| Sor\\Oszlop") || boardLine.trim().isEmpty()) {
                    continue;
                }

                // Tábla sor feldolgozása
                if (boardLine.contains("|")) {
                    processBoardLine(board, boardLine, row);
                    row++;
                }
            }

            return new GameState(board, player, new Date().toString(), gameMode);

        } catch (NumberFormatException e) {
            throw new IOException("Érvénytelen számformátum a fájlban.", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Érvénytelen játékmód a fájlban.", e);
        }
    }

    private void processBoardLine(Board board, String line, int row) {
        String[] cells = line.split("\\|");

        // Az első elem a sor száma, utána jönnek a cellák
        for (int col = 1; col < cells.length && col <= Board.SIZE; col++) {
            String cellValue = cells[col].trim();
            if (cellValue.length() == 1) {
                char symbol = cellValue.charAt(0);
                // Csak érvényes szimbólumokat helyezünk el
                if (symbol == 'X' || symbol == 'O' || symbol == '.') {
                    // A Board placeSymbol metódusát használjuk
                    board.placeSymbol(row, col - 1, symbol);
                }
            }
        }
    }

    public void saveGame(Board board, Player player, GameMode gameMode) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            // Játékos információk
            writer.println("Játékos: " + player.getName());
            writer.println("Dátum: " + new Date());

            // Pontszám - csak HumanPlayer esetén
            int score = 0;
            if (player instanceof HumanPlayer humanPlayer) {
                score = humanPlayer.getScore(); // Nincs casting
            }
            writer.println("Pontszám: " + score);

            writer.println("Szimbólum: " + player.getSymbol());
            writer.println("Játékmód: " + gameMode.name());
            writer.println("Tábla mérete: " + board.getSize());
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

    public boolean saveFileExists() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }
}