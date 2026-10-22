package main.java.com.escape.model.combination;

import main.java.com.escape.model.combination.Combination;

import java.util.Vector;

public class CombinationManager {
    private static CombinationManager instance;
    private Vector<Combination> combinations;
    private CombinationManager() {
        this.combinations = new Vector<>();
    }
    public static CombinationManager getInstance() {
        if (instance == null) {
            instance = new CombinationManager();
        }
        return instance;
    }
    public void addCombination(Combination c) {
        combinations.add(c);
    }
    public boolean canCombine(String id1, String id2) {
        for (Combination c : combinations) {
            if (c.matches(id1, id2)) {
                return true;
            }
        }
        return false;
    }
    public String getResult(String id1, String id2) {
        for (Combination c : combinations) {
            if (c.matches(id1, id2)) {
                return c.getResult();
            }
        }
        return null;
    }
    public Vector<Combination> getAllCombinations() {
        return combinations;
    }
}