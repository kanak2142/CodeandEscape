package com.escape.model.puzzle;

public class MorsePuzzle extends Puzzle {

    private static final String CORRECT_ANSWER = "SOS";
    private int attempts;
    private static final int MAX_ATTEMPTS = 3;
    private boolean hint;

    public MorsePuzzle() {
        super(  "morse",
                "📡 Morse Code Signal",
                "Decode the Morse message to find the next clue.",
                "🔍 Three dots, three dashes, three dots again,\n" +
                        "   A cry for help, a message to send.");
        this.attempts = 0;
        this.hint = false;
    }

    public boolean SolvePuzzle(String action) {
        if (isSolved() || islocked()) {
            return false;
        }

        if (action == null || action.isEmpty()) {
            attempts++;
            checkLock();
            return false;
        }

        if (action.trim().equalsIgnoreCase(CORRECT_ANSWER)) {
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
            return "✅ You decoded the message! SOS means help is on the way!";
        }
        if (islocked()) {
            return "🔒 The transmitter is locked! You've tried too many times.";
        }
        return "📡 Decode the Morse message to find the next clue.\n" +
                "Morse: ... --- ...\n" +
                "Enter the decoded word:";
    }
    public String resetpuzzle() {
        setSolved(false);
        setLocked(false);
        this.attempts = 0;
        this.hint = false;
        return "📡 Morse puzzle has been reset!";
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