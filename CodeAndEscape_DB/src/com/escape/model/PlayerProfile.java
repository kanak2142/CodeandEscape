package com.escape.model;

/**
 * Identifies the person playing Code and Escape.
 */
public class PlayerProfile {
    private final String playerId;
    private final String name;
    private final String discordId;

    public PlayerProfile(String playerId, String name, String discordId) {
        this.playerId = playerId;
        this.name = name;
        this.discordId = discordId;
    }

    public String getPlayerId() { return playerId; }
    public String getName() { return name; }
    public String getDiscordId() { return discordId; }
}
