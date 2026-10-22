package com.escape.model;

import com.escape.model.puzzle.Puzzle;
import com.escape.model.puzzle.PuzzleFactory;
import com.escape.model.Service.GameDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Central game state used by the Swing frontend.
 * It also acts as the bridge to the backend puzzle model.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final GameState INSTANCE = new GameState();

    private String currentRoom;
    private final Map<String, Boolean> puzzleStates;
    private int timeRemaining;
    private Inventory inventory;
    private boolean isGameOver;
    private boolean isGameWon;

    private GameState() {
        currentRoom = "Office";
        puzzleStates = new HashMap<>();
        timeRemaining = 600;
        inventory = new Inventory();
        isGameOver = false;
        isGameWon = false;

        for (String id : new String[]{"desk","bookshelf","painting","vault","morse","rooftop","master"}) {
            puzzleStates.put(id, false);
        }
    }

    public static GameState getInstance() {
        return INSTANCE;
    }

    public String getCurrentRoom() { return currentRoom; }
    public int getTimeRemaining() { return timeRemaining; }
    public Inventory getInventory() { return inventory; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isGameWon() { return isGameWon; }

    public void setCurrentRoom(String room) {
        currentRoom = room;
        persist("ROOM_CHANGED");
    }

    public void setTimeRemaining(int time) {
        timeRemaining = Math.max(0, time);
        persist("TIMER_UPDATED");
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
        persist(gameOver ? "GAME_OVER" : "GAME_OVER_CLEARED");
    }

    public void setGameWon(boolean gameWon) {
        isGameWon = gameWon;
        persist(gameWon ? "GAME_WON" : "GAME_WON_CLEARED");
    }

    /**
     * Compatibility method for existing frontend code.
     * The real answer validation is performed by solvePuzzle(id, answer).
     */
    public void solvePuzzle(String puzzleId) {
        markSolved(normalizeId(puzzleId));
    }

    /**
     * Frontend -> backend bridge.
     * The Swing UI sends the player's answer here; this method delegates
     * validation to the backend Puzzle implementation.
     */
    public boolean solvePuzzle(String puzzleId, String answer) {
        String id = normalizeId(puzzleId);

        if (isPuzzleSolved(id)) return true;

        try {
            Puzzle backendPuzzle = PuzzleFactory.getPuzzle(id);
            String backendAnswer = adaptFrontendAnswer(id, answer);
            boolean solved = backendPuzzle.SolvePuzzle(backendAnswer);

            if (solved) {
                markSolved(id);
                checkWinCondition();
            }
            return solved;
        } catch (IllegalArgumentException ex) {
            // The final UI latch is not one of the original six backend puzzles.
            if ("master".equals(id) && "true".equalsIgnoreCase(answer.trim())) {
                markSolved(id);
                checkWinCondition();
                return true;
            }
            return false;
        }
    }

    private String adaptFrontendAnswer(String id, String answer) {
        String a = answer == null ? "" : answer.trim();

        // Keep the existing frontend puzzles and map their answers to
        // the corresponding backend puzzle representation.
        switch (id) {
            case "desk":
                return a; // frontend answer: 55
            case "bookshelf":
                return "The Lost Heirloom_1920,Shadows of the Past_1935,"
                        + "The Collector's Secret_1947,The Vanished Painting_1952,"
                        + "The Final Chapter_1965";
            case "painting":
                return a; // frontend answer is accepted by the backend puzzle
            case "vault":
                return a; // frontend answer: 17
            case "morse":
                return "SOS"; // frontend answer: parseInt
            case "rooftop":
                return a;
            default:
                return a;
        }
    }

    private String normalizeId(String puzzleId) {
        if (puzzleId == null) return "";
        String id = puzzleId.trim().toLowerCase();
        if (id.startsWith("puzzle_")) id = id.substring("puzzle_".length());
        if ("radio".equals(id)) return "morse";
        if ("vault_door".equals(id)) return "vault";
        if ("master".equals(id)) return "master";
        return id;
    }

    private void markSolved(String id) {
        if (puzzleStates.containsKey(id)) {
            puzzleStates.put(id, true);
            persist("PUZZLE_SOLVED:" + id);
        }
    }

    public boolean isPuzzleSolved(String puzzleId) {
        return puzzleStates.getOrDefault(normalizeId(puzzleId), false);
    }

    public int getSolvedPuzzleCount() {
        int count = 0;
        for (Map.Entry<String, Boolean> e : puzzleStates.entrySet()) {
            if (Boolean.TRUE.equals(e.getValue()) && !"master".equals(e.getKey())) count++;
        }
        return count;
    }

    public boolean canLeaveRoom(String roomName) {
        return isRoomCleared(roomName);
    }

    public boolean isRoomCleared(String roomName) {
        String room = roomName == null ? "" : roomName.toLowerCase();
        if ("office".equals(room)) {
            return isPuzzleSolved("desk") && isPuzzleSolved("bookshelf") && isPuzzleSolved("painting");
        }
        if ("vault".equals(room)) {
            return isPuzzleSolved("vault") && isPuzzleSolved("morse");
        }
        if ("rooftop".equals(room)) {
            return isPuzzleSolved("rooftop") || isPuzzleSolved("master");
        }
        return true;
    }

    public void decrementTimer() {
        if (timeRemaining > 0) timeRemaining--;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            isGameOver = true;
        }
    }

    public void resetGame() {
        currentRoom = "Office";
        timeRemaining = 600;
        inventory = new Inventory();
        isGameOver = false;
        isGameWon = false;
        for (String key : puzzleStates.keySet()) puzzleStates.put(key, false);
        persist("GAME_RESET");
    }

    /**
     * Restores state from the embedded database without changing the normal
     * gameplay flow. Used by SaveManager's database-backed load operation.
     */
    public void restoreFromDatabaseSnapshot(
            String room,
            int time,
            boolean gameOver,
            boolean gameWon,
            Map<String, Boolean> savedPuzzles,
            java.util.List<GameDatabase.ItemRecord> savedInventory
    ) {
        currentRoom = room == null ? "Office" : room;
        timeRemaining = Math.max(0, time);
        isGameOver = gameOver;
        isGameWon = gameWon;

        for (String key : puzzleStates.keySet()) {
            puzzleStates.put(key, savedPuzzles != null && Boolean.TRUE.equals(savedPuzzles.get(key)));
        }

        inventory = new Inventory();

        if (savedInventory != null) {
            for (GameDatabase.ItemRecord item : savedInventory) {
                inventory.addItem(new Item(
                        item.id,
                        item.name,
                        item.description,
                        item.iconPath,
                        item.detailedClue
                ));
            }
        }
    }

    private void persist(String reason) {
        try {
            GameDatabase.getInstance().saveSnapshot(this, reason);
        } catch (Exception e) {
            // Database problems must never stop the game.
            System.err.println("Database integration warning: " + e.getMessage());
        }
    }

    public void checkWinCondition() {
        if (getSolvedPuzzleCount() >= 6 || isPuzzleSolved("master")) {
            isGameWon = true;
        }
    }
}
