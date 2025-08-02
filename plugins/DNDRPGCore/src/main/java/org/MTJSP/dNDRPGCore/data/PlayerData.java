package org.MTJSP.dNDRPGCore.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private boolean creationCompleted = false;
    private String playerClass;
    private String origin;
    private int remainingPoints = 20;

    //基础属性
    private final Map<String, Integer> attributes = new HashMap<>();
    private final Map<String, Integer> attributeBonuses = new HashMap<>();

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        //初始化属性
        attributes.put("strength", 0);
        attributes.put("dexterity", 0);
        attributes.put("constitution", 0);
        attributes.put("wisdom", 0);
        attributes.put("intelligence", 0);
        attributes.put("charisma", 0);
    }

    public int getAttribute(String attribute) {
        return attributes.getOrDefault(attribute, 0) + attributeBonuses.getOrDefault(attribute, 0);
    }

    public void increaseAttribute(String attribute, int amount) {
        attributes.put(attribute, attributes.get(attribute) + amount);
    }

    public void decreaseAttribute(String attribute, int amount) {
        attributes.put(attribute, Math.max(0, attributes.get(attribute) - amount));
    }

    public void addAttributeBonus(String attribute, int bonus) {
        attributeBonuses.put(attribute, attributeBonuses.getOrDefault(attribute, 0) + bonus);
    }

    // Getters and Setters
    public UUID getPlayerId() { return playerId; }
    public boolean isCreationCompleted() { return creationCompleted; }
    public void setCreationCompleted(boolean completed) { this.creationCompleted = completed; }
    public String getPlayerClass() { return playerClass; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public int getRemainingPoints() { return remainingPoints; }
    public void decreaseRemainingPoints(int points) { remainingPoints -= points; }
    public void increaseRemainingPoints(int points) { remainingPoints += points; }

    //计算角色的属性
    public int getMaxHealth() {
        return 20 + getAttribute("constitution") * 2;
    }

    public double getMeleeDamage() {
        return getAttribute("strength") * 0.5;
    }

    public double getCooldownReduction() {
        return Math.max(1.0, 1.0 - (getAttribute("dexterity") * 0.1));
    }

    public int getMaxMana() {
        return getAttribute("intelligence") * 5;
    }

    public double getDodgeChance() {
        return getAttribute("wisdom") * 0.01;
    }

    public double getCriticalChance() {
        return getAttribute("charisma") * 0.01;
    }
}