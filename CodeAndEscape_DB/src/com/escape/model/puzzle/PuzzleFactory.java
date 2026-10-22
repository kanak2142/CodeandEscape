package com.escape.model.puzzle;

public class PuzzleFactory {
    public static com.escape.model.puzzle.Puzzle getPuzzle(String puzzleId)
    {
        switch (puzzleId) {
            case "desk":
                return new DeskPuzzle();
            case "bookshelf":
                return new BookShelfPuzzle();
            case "morse":
                return new MorsePuzzle();
            case "painting":
                return new PaintingPuzzle();
            case "rooftop":
                return new RoofTopPuzzle();
            case "vault":
                return new VaultPuzzle();
            default:
                throw new IllegalArgumentException("Unknown puzzle: " + puzzleId);
        }
    }
}

