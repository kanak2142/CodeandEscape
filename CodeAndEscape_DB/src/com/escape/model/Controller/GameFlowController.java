package com.escape.model.Controller;

import com.escape.model.GameState;

public class GameFlowController {


    private static GameFlowController instance;
    private GameFlowController() { }

    public static GameFlowController getInstance() {
        if (instance == null) {
            instance = new GameFlowController();
        }
        return instance;
    }

    public boolean isRoomComplete(String roomId, GameState state) {
        if (roomId.equals("office")) {
            return state.isPuzzleSolved("desk") &&
                    state.isPuzzleSolved("bookshelf") &&
                    state.isPuzzleSolved("painting");
        }

        if (roomId.equals("vault")) {
            return state.isPuzzleSolved("vault") &&
                    state.isPuzzleSolved("morse");
        }

        if (roomId.equals("rooftop")) {
            return state.isPuzzleSolved("rooftop");
        }

        return false;
    }

    public String getNextRoom(String currentRoom) {
        if (currentRoom.equals("office")) {
            return "vault";
        }

        if (currentRoom.equals("vault")) {
            return "rooftop";
        }

        if (currentRoom.equals("rooftop")) {
            return null;
        }

        return null;
    }

    public boolean canMoveToRoom(String roomId, GameState state) {

        if (roomId.equals("office")) {
            return true;
        }

        if (roomId.equals("vault")) {
            return isRoomComplete("office", state);
        }

        if (roomId.equals("rooftop")) {
            return isRoomComplete("vault", state);
        }

        return false;
    }

    public boolean checkWinCondition(GameState state) {
        return isRoomComplete("office", state) &&
                isRoomComplete("vault", state) &&
                isRoomComplete("rooftop", state);
    }
}