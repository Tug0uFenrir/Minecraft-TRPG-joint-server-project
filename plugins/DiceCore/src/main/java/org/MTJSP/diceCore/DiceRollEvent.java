package org.MTJSP.diceCore;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DiceRollEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSender sender;
    private final String expression;
    private final DiceResult result;

    public DiceRollEvent(CommandSender sender, String expression, DiceResult result) {
        this.sender = sender;
        this.expression = expression;
        this.result = result;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getExpression() {
        return expression;
    }

    public DiceResult getResult() {
        return result;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}