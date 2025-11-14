package nye.flocrm.progtech.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseService {
    private static final String DB_URL = "jdbc:postgresql://homedb.ddns.net:26257/amoba";
    private static final String USER = "balintbotond"; // felhasználó
    private static final String PASSWORD = "dfxpdl771989"; // jelszó

    public DatabaseService() {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL driver not found", e);
        }
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("ssl", "false");
        return DriverManager.getConnection(DB_URL, props);
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS scores (" +
                "id SERIAL PRIMARY KEY, " +
                "player TEXT NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LoggerService.info("Adatbázis tábla inicializálva");
        } catch (SQLException e) {
            LoggerService.severe("Adatbázis inicializálási hiba: " + e.getMessage(), e);
        }
    }

    public void saveScore(String player, int score) {
        String sql = "INSERT INTO scores(player, score) VALUES(?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            LoggerService.info("Pontszám mentve: " + player + " - " + score);
        } catch (SQLException e) {
            LoggerService.warning("Hiba a pont mentésekor: " + e.getMessage());
        }
    }

    public List<String> getTopPlayers(int limit) {
        List<String> topPlayers = new ArrayList<>();
        String sql = "SELECT player, SUM(score) as total_score " +
                "FROM scores " +
                "GROUP BY player " +
                "ORDER BY total_score DESC " +
                "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String player = rs.getString("player");
                int totalScore = rs.getInt("total_score");
                topPlayers.add(rank + ". " + player + " - " + totalScore + " pont");
                rank++;
            }
        } catch (SQLException e) {
            LoggerService.warning("Hiba a ranglista lekérdezésekor: " + e.getMessage());
            topPlayers.add("Hiba a ranglista betöltésekor");
        }

        return topPlayers;
    }

    /**
     * Ellenőrzi, hogy az adatbázis kapcsolat elérhető-e
     *
     * @return true ha az adatbázis elérhető, false egyébként
     */
    public boolean isConnectionAvailable() {
        try (Connection conn = getConnection()) {
            return !conn.isValid(2);
        } catch (SQLException e) {
            LoggerService.warning("Adatbázis kapcsolat hiba: " + e.getMessage());
            return true;
        }
    }
}