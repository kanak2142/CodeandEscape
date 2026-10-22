package com.escape.model.Service;

import com.escape.model.GameState;
import com.escape.model.Inventory;
import com.escape.model.Item;
import com.escape.model.PlayerProfile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Properties;

/**
 * MySQL database layer for Code and Escape.
 *
 * The existing game logic is preserved. This class adds real JDBC persistence
 * for game state, puzzle progress, inventory, sessions and game events.
 */
public class GameDatabase {
    private static final String CONFIG_FILE = "db.properties";
    private static final String DB_NAME = "codeandescape";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";

    private static GameDatabase instance;
    private String sessionId;
    private String playerId;
    private boolean available;

    private GameDatabase() {
        available = initializeDatabase();
    }

    public static synchronized GameDatabase getInstance() {
        if (instance == null) instance = new GameDatabase();
        return instance;
    }

    private boolean initializeDatabase() {
        try {
            Class.forName(DRIVER);
            Properties props = loadProperties();
            String user = props.getProperty("db.user", "root");
            String password = props.getProperty("db.password", "");

            try (Connection con = DriverManager.getConnection(SERVER_URL, user, password);
                 Statement st = con.createStatement()) {
                st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            try (Connection con = DriverManager.getConnection(DB_URL, user, password);
                 Statement st = con.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                        "player_id VARCHAR(36) PRIMARY KEY," +
                        "player_name VARCHAR(100) NOT NULL," +
                        "discord_id VARCHAR(100) NOT NULL UNIQUE," +
                        "created_at DATETIME NOT NULL," +
                        "last_login DATETIME NOT NULL)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS game_sessions (" +
                        "session_id VARCHAR(36) PRIMARY KEY," +
                        "player_id VARCHAR(36) NULL," +
                        "started_at DATETIME NOT NULL," +
                        "ended_at DATETIME NULL," +
                        "status VARCHAR(20) NOT NULL," +
                        "FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE SET NULL)");

                ensureGameSessionsPlayerColumn(con);

                st.executeUpdate("CREATE TABLE IF NOT EXISTS game_state (" +
                        "session_id VARCHAR(36) PRIMARY KEY," +
                        "saved_at DATETIME NOT NULL," +
                        "save_reason VARCHAR(100)," +
                        "current_room VARCHAR(50) NOT NULL," +
                        "time_remaining INT NOT NULL," +
                        "game_over BOOLEAN NOT NULL," +
                        "game_won BOOLEAN NOT NULL," +
                        "FOREIGN KEY (session_id) REFERENCES game_sessions(session_id))");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS puzzle_progress (" +
                        "session_id VARCHAR(36) NOT NULL," +
                        "puzzle_id VARCHAR(50) NOT NULL," +
                        "solved BOOLEAN NOT NULL," +
                        "PRIMARY KEY (session_id, puzzle_id)," +
                        "FOREIGN KEY (session_id) REFERENCES game_sessions(session_id) ON DELETE CASCADE)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS inventory (" +
                        "session_id VARCHAR(36) NOT NULL," +
                        "item_id VARCHAR(100) NOT NULL," +
                        "item_name VARCHAR(200)," +
                        "description TEXT," +
                        "icon_path VARCHAR(500)," +
                        "detailed_clue TEXT," +
                        "PRIMARY KEY (session_id, item_id)," +
                        "FOREIGN KEY (session_id) REFERENCES game_sessions(session_id) ON DELETE CASCADE)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS game_events (" +
                        "event_id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "session_id VARCHAR(36)," +
                        "event_time DATETIME NOT NULL," +
                        "event_type VARCHAR(100) NOT NULL," +
                        "room VARCHAR(50)," +
                        "time_remaining INT," +
                        "solved_puzzles INT," +
                        "FOREIGN KEY (session_id) REFERENCES game_sessions(session_id) ON DELETE SET NULL)");
            }
            System.out.println("MySQL database connected: " + DB_NAME);
            return true;
        } catch (Exception e) {
            System.err.println("MySQL database unavailable: " + e.getMessage());
            System.err.println("Check MySQL Server, db.properties and the JDBC driver.");
            return false;
        }
    }

    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        Path path = Paths.get(CONFIG_FILE);
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) { props.load(in); }
        } else {
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "");
        }
        return props;
    }

    private Connection connection() throws SQLException, IOException {
        Properties props = loadProperties();
        return DriverManager.getConnection(
                DB_URL,
                props.getProperty("db.user", "root"),
                props.getProperty("db.password", "")
        );
    }

    private void ensureGameSessionsPlayerColumn(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=? AND TABLE_NAME='game_sessions' AND COLUMN_NAME='player_id'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, DB_NAME);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate("ALTER TABLE game_sessions ADD COLUMN player_id VARCHAR(36) NULL");
                    }
                }
            }
        }
    }

    public boolean isDatabaseAvailable() {
        return available;
    }

    /** Creates a new player or returns the existing player for the Discord ID. */
    public synchronized PlayerProfile registerOrFindPlayer(String name, String discordId) {
        if (!available) return null;
        String normalizedDiscord = discordId.trim();
        try (Connection con = connection()) {
            String find = "SELECT player_id, player_name, discord_id FROM players WHERE discord_id=?";
            try (PreparedStatement ps = con.prepareStatement(find)) {
                ps.setString(1, normalizedDiscord);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String id = rs.getString("player_id");
                        try (PreparedStatement update = con.prepareStatement(
                                "UPDATE players SET player_name=?, last_login=? WHERE player_id=?")) {
                            update.setString(1, name.trim());
                            update.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                            update.setString(3, id);
                            update.executeUpdate();
                        }
                        playerId = id;
                        return new PlayerProfile(id, name.trim(), normalizedDiscord);
                    }
                }
            }

            String id = UUID.randomUUID().toString();
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO players(player_id,player_name,discord_id,created_at,last_login) VALUES(?,?,?,?,?)")) {
                ps.setString(1, id);
                ps.setString(2, name.trim());
                ps.setString(3, normalizedDiscord);
                ps.setTimestamp(4, now);
                ps.setTimestamp(5, now);
                ps.executeUpdate();
            }
            playerId = id;
            return new PlayerProfile(id, name.trim(), normalizedDiscord);
        } catch (Exception e) {
            warn(e);
            return null;
        }
    }

    public synchronized PlayerProfile getCurrentPlayer() {
        if (!available || playerId == null) return null;
        try (Connection con = connection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT player_id,player_name,discord_id FROM players WHERE player_id=?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new PlayerProfile(rs.getString(1), rs.getString(2), rs.getString(3));
            }
        } catch (Exception e) { warn(e); }
        return null;
    }

    public synchronized void startSession(GameState state) {
        startSession(state, null);
    }

    public synchronized void startSession(GameState state, PlayerProfile profile) {
        if (!available) return;
        if (profile != null) playerId = profile.getPlayerId();
        sessionId = UUID.randomUUID().toString();
        try (Connection con = connection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO game_sessions(session_id, player_id, started_at, status) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, sessionId);
                ps.setString(2, playerId);
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(4, "RUNNING");
                ps.executeUpdate();
            }
            recordEvent(con, "SESSION_STARTED", state);
            saveSnapshot(con, state, "SESSION_STARTED");
        } catch (Exception e) { warn(e); }
    }

    public synchronized void endSession(GameState state, String result) {
        if (!available) return;
        if (sessionId == null) sessionId = UUID.randomUUID().toString();
        try (Connection con = connection()) {
            if (sessionExists(con)) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE game_sessions SET ended_at=?, status=? WHERE session_id=?")) {
                    ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(2, result);
                    ps.setString(3, sessionId);
                    ps.executeUpdate();
                }
            }
            recordEvent(con, "SESSION_" + result, state);
            saveSnapshot(con, state, "SESSION_" + result);
        } catch (Exception e) { warn(e); }
    }

    public synchronized void saveSnapshot(GameState state, String reason) {
        if (!available || sessionId == null) return;
        try (Connection con = connection()) { saveSnapshot(con, state, reason); }
        catch (Exception e) { warn(e); }
    }

    public synchronized void saveSnapshotNow(GameState state, String reason) {
        saveSnapshot(state, reason);
    }

    private void saveSnapshot(Connection con, GameState state, String reason) throws SQLException {
        if (state == null || sessionId == null) return;

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO game_state(session_id,saved_at,save_reason,current_room,time_remaining,game_over,game_won) " +
                "VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE saved_at=VALUES(saved_at),save_reason=VALUES(save_reason)," +
                "current_room=VALUES(current_room),time_remaining=VALUES(time_remaining),game_over=VALUES(game_over),game_won=VALUES(game_won)")) {
            ps.setString(1, sessionId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, reason);
            ps.setString(4, state.getCurrentRoom());
            ps.setInt(5, state.getTimeRemaining());
            ps.setBoolean(6, state.isGameOver());
            ps.setBoolean(7, state.isGameWon());
            ps.executeUpdate();
        }

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO puzzle_progress(session_id,puzzle_id,solved) VALUES(?,?,?) " +
                "ON DUPLICATE KEY UPDATE solved=VALUES(solved)")) {
            for (String id : new String[]{"desk","bookshelf","painting","vault","morse","rooftop","master"}) {
                ps.setString(1, sessionId); ps.setString(2, id); ps.setBoolean(3, state.isPuzzleSolved(id)); ps.addBatch();
            }
            ps.executeBatch();
        }

        try (PreparedStatement del = con.prepareStatement("DELETE FROM inventory WHERE session_id=?")) {
            del.setString(1, sessionId); del.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO inventory(session_id,item_id,item_name,description,icon_path,detailed_clue) VALUES(?,?,?,?,?,?)")) {
            Inventory inv = state.getInventory();
            for (Item item : inv.getAllItems()) {
                ps.setString(1, sessionId); ps.setString(2, item.getId()); ps.setString(3, item.getName());
                ps.setString(4, item.getDescription()); ps.setString(5, item.getIconPath()); ps.setString(6, item.getDetailedClue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public synchronized void recordEvent(String event, GameState state) {
        if (!available || sessionId == null) return;
        try (Connection con = connection()) { recordEvent(con, event, state); }
        catch (Exception e) { warn(e); }
    }

    private void recordEvent(Connection con, String event, GameState state) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO game_events(session_id,event_time,event_type,room,time_remaining,solved_puzzles) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, sessionId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, event);
            ps.setString(4, state == null ? null : state.getCurrentRoom());
            ps.setObject(5, state == null ? null : state.getTimeRemaining(), Types.INTEGER);
            ps.setObject(6, state == null ? null : state.getSolvedPuzzleCount(), Types.INTEGER);
            ps.executeUpdate();
        }
    }

    public synchronized DatabaseSnapshot getLatestSnapshot() {
        if (!available || sessionId == null) return null;
        try (Connection con = connection();
             PreparedStatement ps = con.prepareStatement("SELECT current_room,time_remaining,game_over,game_won,save_reason FROM game_state WHERE session_id=?")) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new DatabaseSnapshot(sessionId, rs.getString(1), rs.getInt(2), rs.getBoolean(3), rs.getBoolean(4), rs.getString(5));
            }
        } catch (Exception e) { warn(e); return null; }
    }

    public synchronized boolean restoreLatestSnapshot(GameState state) {
        if (!available || sessionId == null || state == null) return false;
        try (Connection con = connection()) {
            String room; int time; boolean over, won;
            try (PreparedStatement ps = con.prepareStatement("SELECT current_room,time_remaining,game_over,game_won FROM game_state WHERE session_id=?")) {
                ps.setString(1, sessionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    room=rs.getString(1); time=rs.getInt(2); over=rs.getBoolean(3); won=rs.getBoolean(4);
                }
            }
            Map<String,Boolean> puzzles = new HashMap<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT puzzle_id,solved FROM puzzle_progress WHERE session_id=?")) {
                ps.setString(1, sessionId); try (ResultSet rs=ps.executeQuery()) { while(rs.next()) puzzles.put(rs.getString(1),rs.getBoolean(2)); }
            }
            List<ItemRecord> items = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT item_id,item_name,description,icon_path,detailed_clue FROM inventory WHERE session_id=?")) {
                ps.setString(1, sessionId); try (ResultSet rs=ps.executeQuery()) {
                    while(rs.next()) items.add(new ItemRecord(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)));
                }
            }
            state.restoreFromDatabaseSnapshot(room,time,over,won,puzzles,items);
            return true;
        } catch (Exception e) { warn(e); return false; }
    }

    public synchronized boolean databaseExists() { return available; }

    public synchronized void deleteDatabase() {
        if (!available || sessionId == null) return;
        try (Connection con=connection(); PreparedStatement ps=con.prepareStatement("DELETE FROM game_sessions WHERE session_id=?")) {
            ps.setString(1,sessionId); ps.executeUpdate();
        } catch(Exception e){ warn(e); }
    }

    public synchronized int getSessionCount() {
        return count("SELECT COUNT(*) FROM game_sessions");
    }
    public synchronized int getEventCount() {
        return count("SELECT COUNT(*) FROM game_events");
    }

    private int count(String sql) {
        if (!available) return 0;
        try (Connection con=connection(); Statement st=con.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            return rs.next()?rs.getInt(1):0;
        } catch(Exception e){ warn(e); return 0; }
    }

    private boolean sessionExists(Connection con) throws SQLException {
        try (PreparedStatement ps=con.prepareStatement("SELECT 1 FROM game_sessions WHERE session_id=?")) {
            ps.setString(1,sessionId); try(ResultSet rs=ps.executeQuery()){return rs.next();}
        }
    }

    private void warn(Exception e) { System.err.println("MySQL database warning: " + e.getMessage()); }

    public static class DatabaseSnapshot {
        public final String sessionId, currentRoom, saveReason;
        public final int timeRemaining;
        public final boolean gameOver, gameWon;
        DatabaseSnapshot(String sessionId,String currentRoom,int timeRemaining,boolean gameOver,boolean gameWon,String saveReason){
            this.sessionId=sessionId;this.currentRoom=currentRoom;this.timeRemaining=timeRemaining;this.gameOver=gameOver;this.gameWon=gameWon;this.saveReason=saveReason;
        }
    }

    public static class ItemRecord {
        public final String id,name,description,iconPath,detailedClue;
        public ItemRecord(String id,String name,String description,String iconPath,String detailedClue){
            this.id=id;this.name=name;this.description=description;this.iconPath=iconPath;this.detailedClue=detailedClue;
        }
    }
}
