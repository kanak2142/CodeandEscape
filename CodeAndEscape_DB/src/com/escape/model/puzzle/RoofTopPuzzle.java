package com.escape.model.puzzle;

public class RoofTopPuzzle extends Puzzle {

    private static final String REQUIRED_ITEM_1 = "rooftop_key";
    private static final String REQUIRED_ITEM_2 = "lighter";
    private int attempts;
    private static final int MAX_ATTEMPTS = 3;
    private boolean hint;

    public RoofTopPuzzle() {
        super("rooftop", "🚪 Rooftop Escape",
                "The final escape! Unlock the rooftop door.",
                "🌙 One to open, one to light,\n" +
                        "   Make your way out through the night.\n" +
                        "   The cold air hits your skin so new,\n" +
                        "   Freedom awaits, the sky so blue.");
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

        String[] items = action.split(",");
        if (items.length != 2) {
            attempts++;
            checkLock();
            return false;
        }

        String item1 = items[0].trim();
        String item2 = items[1].trim();

        if ((item1.equals(REQUIRED_ITEM_1) && item2.equals(REQUIRED_ITEM_2)) ||
                (item1.equals(REQUIRED_ITEM_2) && item2.equals(REQUIRED_ITEM_1))) {
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
            return "🚪✨ THE DOOR SWINGS OPEN! 🌙\n\n" +
                    "The sky so blue, it pictures you,\n" +
                    "Stepping on the rooftop, feeling so new.\n" +
                    "The task is done, you made it through,\n" +
                    "The door has opened to the ultimate view.\n\n" +
                    "City lights glitter in the moonlight,\n" +
                    "You breathe in the cool air of the night.\n\n" +
                    "🏆 YOU DID IT! YOU ESCAPED! 🏆";
        }
        if (islocked()) {
            return "🔒💀 THE DOOR WON'T BUDGE!\n\n" +
                    "Heavy footsteps echo in the stairwell...\n" +
                    "They're getting closer.\n\n" +
                    "💀 GAME OVER.";
        }
        return "🚪 THE FINAL DOOR.\n\n" +
                "The Collector is escaping with the painting.\n" +
                "This is your only chance to stop him.\n\n" +
                "🌙 Use two items to unlock the rooftop door.\n" +
                "Enter the item IDs separated by commas.\n" +
                "Available items: rooftop_key, lighter\n\n" +
                "⏰ The clock is ticking...";
    }

    @Override
    public String resetpuzzle() {
        setSolved(false);
        setLocked(false);
        this.attempts = 0;
        this.hint = false;
        return "🚪 Rooftop puzzle has been reset!";
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