package com.escape.model.puzzle;

import java.io.Serializable;

public abstract class Puzzle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;          // Unique name to identify puzzle (like "desk", "bookshelf")
    private String name;        // Display name shown to player (like "Desk Drawer Lock")
    private String Description; // What the puzzle is about
    private String hint;        // Clue to help player if stuck

    private boolean solved;     // True when puzzle is solved
    private boolean locked;     // True when player fails too many times


    public Puzzle(String id, String name, String Description, String hint) {
        this.id = id;
        this.name = name;
        this.Description = Description;
        this.hint = hint;
        this.solved = false;    // Puzzle starts unsolved
        this.locked = false;    // Puzzle starts unlocked
    }

    // Getters - to read data
    public String getId() { return id; }                // Returns puzzle ID
    public String getname() { return name; }            // Returns puzzle name
    public String getDescription() { return Description; } // Returns description
    public String gethint() { return hint; }            // Returns hint
    public boolean islocked() { return locked; }        // Returns true if locked
    public boolean isSolved() { return solved; }        // Returns true if solved

    // Setters - to update data
    public void setSolved(boolean solved) { this.solved = solved; }   // Mark as solved/unsolved
    public void setLocked(boolean locked) { this.locked = locked; }   // Mark as locked/unlocked

    // Abstract methods - each puzzle must implement these
    public abstract boolean SolvePuzzle(String action);  // Checks if player's action solves the puzzle
    public abstract String getQuestion();               // Returns question shown to player
    public abstract String resetpuzzle();               // Resets puzzle to original state
}