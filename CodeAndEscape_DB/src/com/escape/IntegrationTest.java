package com.escape;

import com.escape.model.GameState;

/**
 * Simple console test proving the Swing-facing GameState delegates
 * answer validation to the backend PuzzleFactory.
 */
public class IntegrationTest {
    public static void main(String[] args) {
        GameState state = GameState.getInstance();
        state.resetGame();

        String[][] tests = {
                {"puzzle_desk", "55"},
                {"puzzle_bookshelf", "2"},
                {"puzzle_painting", "concat"},
                {"puzzle_vault_door", "17"},
                {"puzzle_radio", "parseInt"},
                {"puzzle_master", "true"}
        };

        for (String[] test : tests) {
            boolean result = state.solvePuzzle(test[0], test[1]);
            System.out.println(test[0] + " -> backend validation -> " + result);
            if (!result) throw new AssertionError("Failed: " + test[0]);
        }

        if (!state.isGameWon()) throw new AssertionError("Win condition not reached");
        System.out.println("ALL 6 FRONTEND -> BACKEND TESTS PASSED");
    }
}
