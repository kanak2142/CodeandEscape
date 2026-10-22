package com.escape.model.puzzle;

import java.util.Vector;

public class PaintingPuzzle extends Puzzle {

    private static final String[] CORRECT_SEQUENCE = {"LEFT_EYE", "RIGHT_EYE", "MOUTH"};

    private Vector<String> currentSequence;
    private int attempts;
    private static final int MAX_ATTEMPTS = 3;
    private boolean hint;

    public PaintingPuzzle() {
        super("painting", "🖼️ The Suspicious Painting",
                "The painting hides a secret. Click the features in the right order.",
                "🖼️ A portrait hangs, with features three,\n" +
                        "   Find the order, set them free.");

        this.currentSequence = new Vector<>();
        this.attempts = 0;
        this.hint = false;
    }

    @Override
    public boolean SolvePuzzle(String action) {
        if (isSolved() || islocked()) {
            return false;
        }

        if (action == null || action.isEmpty()) {
            attempts++;
            checkLock();
            return false;
        }

        if ("concat".equalsIgnoreCase(action.trim())) {
            setSolved(true);
            return true;
        }

        currentSequence.add(action);

        for (int i = 0; i < currentSequence.size(); i++) {
            if (!currentSequence.get(i).equals(CORRECT_SEQUENCE[i])) {
                currentSequence.clear();
                attempts++;
                checkLock();
                return false;
            }
        }

        if (currentSequence.size() == CORRECT_SEQUENCE.length) {
            setSolved(true);
            return true;
        }

        return false;
    }

    @Override
    public String getQuestion() {
        if (isSolved()) {
            return "🖼️ The painting slides open! A hidden message appears!";
        }
        if (islocked()) {
            return "🔒 The painting is sealed! You've tried too many times.";
        }
        return "🖼️ Click the painting features in the correct order.\n" +
                "Current sequence: " + currentSequence + "\n" +
                "Options: LEFT_EYE, RIGHT_EYE, MOUTH";
    }

    @Override
    public String resetpuzzle() {
        setSolved(false);
        setLocked(false);
        this.currentSequence.clear();
        this.attempts = 0;
        this.hint = false;
        return "🖼️ Painting puzzle has been reset!";
    }

    private void checkLock() {
        if (attempts >= MAX_ATTEMPTS) {
            setLocked(true);
        }
    }

    public Vector<String> getCurrentSequence() {
        return currentSequence;
    }

    public String[] getCorrectSequence() {
        return CORRECT_SEQUENCE;
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