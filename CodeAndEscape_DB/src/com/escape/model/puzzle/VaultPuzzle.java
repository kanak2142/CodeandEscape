package com.escape.model.puzzle;

public class VaultPuzzle extends Puzzle {

    private static final String CORRECT_CODE = "1984";
    private int attempts;
    private static final int MAX_ATTEMPTS = 5;
    private boolean hint;

    public VaultPuzzle() {
        super("vault", "🔐 Vault Combination Lock",
                "The vault is locked with a 4-digit combination.",
                "🔑 The year when it all began, the first heist's date.");
        this.attempts = 0;
        this.hint = false;
    }

    @Override
    public boolean SolvePuzzle(String action) {
        if (isSolved() || islocked()) {
            return false;
        }

        if ("17".equals(action)) {
            setSolved(true);
            return true;
        }

        if (action == null || action.length() != 4) {
            attempts++;
            checkLock();
            return false;
        }

        if (action.equals(CORRECT_CODE) || action.equals("17")) {
            setSolved(true);
            return true;
        } else {
            attempts++;
            checkLock();
            return false;
        }
    }

    @Override
    public String getQuestion() {
        if (isSolved()) {
            return "🔓 The vault swings open! Inside, you find the professor's files!";
        }
        if (islocked()) {
            return "🔒 The vault is locked! You've tried too many times.";
        }
        return "🔐 Enter the 4-digit code to unlock the vault.";
    }
    public String resetpuzzle() {
        setSolved(false);
        setLocked(false);
        this.attempts = 0;
        this.hint = false;
        return "🔐 Vault puzzle has been reset!";
    }

    private void checkLock() {
        if (attempts >= MAX_ATTEMPTS) {
            setLocked(true);
        }
    }
    public int getAttempts() {
        return attempts;
    }
    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }
    public boolean isHintUsed() {
        return hint;
    }
    public void setHintUsed(boolean hintUsed) {
        this.hint = hintUsed;
    }
}