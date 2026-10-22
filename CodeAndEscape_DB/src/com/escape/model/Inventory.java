package com.escape.model;

import com.escape.model.Service.GameDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final List<Item> items = new ArrayList<>();
    private final List<Runnable> changeListeners = new ArrayList<>();
    private Item selectedItem = null;

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable r : changeListeners) r.run();
    }

    public void addItem(Item item) {
        if (!hasItem(item.getId())) {
            items.add(item);
            notifyListeners();
            persist("ITEM_ADDED:" + item.getId());
        }
    }

    public void removeItem(String id) {
        items.removeIf(i -> i.getId().equals(id));
        if (selectedItem != null && selectedItem.getId().equals(id)) {
            selectedItem = null;
        }
        notifyListeners();
        persist("ITEM_REMOVED:" + id);
    }

    public boolean hasItem(String id) {
        return items.stream().anyMatch(i -> i.getId().equals(id));
    }

    public List<Item> getAllItems() {
        return Collections.unmodifiableList(items);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item item) {
        this.selectedItem = (this.selectedItem == item) ? null : item; // Toggle selection
        notifyListeners();
        persist("ITEM_SELECTION_CHANGED");
    }

    private void persist(String reason) {
        try {
            GameDatabase.getInstance().saveSnapshot(GameState.getInstance(), reason);
        } catch (Exception e) {
            System.err.println("Database integration warning: " + e.getMessage());
        }
    }

    // Combine two items (e.g. Lighter + Unlit Candle = Lit Candle)
    public boolean combineItems(Item a, Item b) {
        if ((a.getId().equals("lighter") && b.getId().equals("candle")) ||
                (a.getId().equals("candle") && b.getId().equals("lighter"))) {
            removeItem("candle");
            addItem(new Item("lit_candle", "Lit Candle", "A burning candle casting a steady warm glow.", "/assets/images/lit_candle.png", "The flame reveals thermal sensitive ink on old paper."));
            return true;
        }
        return false;
    }
}