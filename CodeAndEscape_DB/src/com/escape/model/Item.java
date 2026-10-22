package com.escape.model;

import java.util.function.Consumer;

public class Item {
    private final String id;
    private final String name;
    private final String description;
    private final String iconPath;
    private final String detailedClue; // Clue shown when inspecting closely

    public Item(String id, String name, String description, String iconPath, String detailedClue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconPath = iconPath;
        this.detailedClue = detailedClue;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIconPath() { return iconPath; }
    public String getDetailedClue() { return detailedClue; }

    @Override
    public String toString() { return name; }
}