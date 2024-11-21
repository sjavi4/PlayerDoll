package me.autobot.playerdoll.api.event;

import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandRegisterEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final LiteralCommandNode<Object> root;
    public CommandRegisterEvent(LiteralCommandNode<Object> root) {
        this.root = root;
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public LiteralCommandNode<Object> getRoot() {
        return root;
    }
}
