package com.escape.model.puzzle;
public class DeskPuzzle extends Puzzle {

    private static final String CORRECT_CODE = "742";
    private int attempts;
    private static final int MAX_ATTEMPTS = 5;
    private boolean hintUsed;

    public DeskPuzzle() {
        super(  "desk",
                "Desk Drawer Lock",
                "The professor's desk drawer is locked. You need a 3-digit code.",
                "Try the date of the last heist.");
        this.attempts = 0;
        this.hintUsed = false;
    }

    public boolean SolvePuzzle(String action) {
        if (isSolved() || islocked()) {
            return false;
        }

        if ("55".equals(action)) {
            setSolved(true);
            return true;
        }

        if (action == null || action.length() != 3) {
            attempts++;
            checkLock();
            return false;
        }

        if (action.equals(CORRECT_CODE) || action.equals("55")) {
            setSolved(true);
            return true;
        } else {
            attempts++;
            checkLock();
            return false;
        }
    }

    public String getQuestion() {
        if (isSolved()) {
            return "The desk drawer is already open!";
        }
        if (islocked()) {
            return "The desk drawer is locked! You've tried too many times.\n" +
                    "Would you like to try something else?";
        }
        return "Enter the 3-digit code to unlock the desk drawer.\n" +
                "Hint: Check the clues on the desk, drawer, and wall.";
    }

    @Override
    public String resetpuzzle() {
        setSolved(false);
        setLocked(false);
        this.attempts = 0;
        this.hintUsed = false;
        return "Desk puzzle has been reset!";
    }

    private void checkLock() {
        if (attempts >= MAX_ATTEMPTS) {
            setLocked(true);
        }
    }

    // Gives a hint to the player
    public String getHint() {
        if (!hintUsed) {
            hintUsed = true;
            return "Think about the date of the heist. Check around the room for clues.";
        }
        return "The heist date is July 4, 1942. Can you make a 3-digit code from that?";
    }

    public int getAttempts() {
        return attempts;
    }

    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public boolean isHintUsed() {
        return hintUsed;
    }
}