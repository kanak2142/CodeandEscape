package com.escape.model.Service;

import com.escape.model.GameState;

/**
 * Backward-compatible save service.
 *
 * The original public SaveManager API is preserved, but persistence is now
 * handled by the embedded Code and Escape database instead of savegame.dat.
 */
public class SaveManager {

    private static SaveManager instance;
    private SaveManager() { }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    public void saveGame(GameState state) {
        GameDatabase.getInstance().saveSnapshotNow(state, "MANUAL_SAVE");
        System.out.println("Game saved to codeandescape.db!");
    }

    public GameState loadGame() {
        GameState state = GameState.getInstance();
        if (GameDatabase.getInstance().restoreLatestSnapshot(state)) {
            System.out.println("Game loaded from codeandescape.db!");
            return state;
        }

        System.err.println("No database save found.");
        return null;
    }

    public boolean saveExists() {
        return GameDatabase.getInstance().databaseExists();
    }

    public void deleteSave() {
        GameDatabase.getInstance().deleteDatabase();
        System.out.println("Database save deleted!");
    }
}
