package com.escape.model.puzzle;
import java.util.Vector;

public class BookShelfPuzzle extends com.escape.model.puzzle.Puzzle {

    private static final String[] Correct_Order = {
            "The Lost Heirloom_1920",
            "Shadows of the Past_1935",
            "The Collector's Secret_1947",
            "The Vanished Painting_1952",
            "The Final Chapter_1965"
    };

    Vector<String> User_Order = new Vector<>();
    private int attempts;
    private static final int MAX_ATTEMPTS = 3;
    private boolean hint;

    public BookShelfPuzzle() {
        super("bookshelf", "📚 Bookshelf Order",
                "Arrange the books to reveal the professor's hidden story.",
                "📖 Every book has a story, arrange it with care,\n" +
                        "   Unlock the hidden truth, and the fruit you'll share. 🍎✨");
        User_Order.add("The Final Chapter_1965");
        User_Order.add("The Vanished Painting_1952");
        User_Order.add("The Collector's Secret_1947");
        User_Order.add("Shadows of the Past_1935");
        User_Order.add("The Lost Heirloom_1920");
        this.attempts = 0;
        this.hint = false;
    }

    @Override
    public boolean SolvePuzzle(String action) {
        if (isSolved() || islocked()) {
            return false;
        }

        if ("2".equals(action.trim())) {
            setSolved(true);
            return true;
        }

        if (action == null || action.isEmpty()) {
            attempts++;
            checkLock();
            return false;
        }

        String[] books = action.split(",");
        Vector<String> playerOrder = new Vector<>();
        for (String book : books) {
            playerOrder.add(book.trim());
        }

        Vector<String> correctVector = new Vector<>();
        for (String book : Correct_Order) {
            correctVector.add(book);
        }

        if (playerOrder.equals(correctVector)) {
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
            return "✅ The books are perfectly arranged!\n" +
                    "You've uncovered the professor's secret! 🍎✨";
        }
        if (islocked()) {
            return "🔒 The bookshelf is locked! You've tried too many times.";
        }
        return "📚 Arrange the books in the correct order.\n" +
                "Current order: " + User_Order + "\n" +
                "Enter the book IDs in correct order separated by commas.";
    }

    @Override
    public String resetpuzzle() {
        setSolved(false);
        setLocked(false);
        User_Order.clear();
        User_Order.add("The Final Chapter_1965");
        User_Order.add("The Vanished Painting_1952");
        User_Order.add("The Collector's Secret_1947");
        User_Order.add("Shadows of the Past_1935");
        User_Order.add("The Lost Heirloom_1920");
        attempts = 0;
        hint = false;
        return "📚 Bookshelf puzzle has been reset!";
    }

    private void checkLock() {
        if (attempts >= MAX_ATTEMPTS) {
            setLocked(true);
        }
    }

    public Vector<String> getUserOrder() {
        return User_Order;
    }

    public String[] getCorrectOrder() {
        return Correct_Order;
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