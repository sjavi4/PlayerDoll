package me.autobot.playerdoll.CustomEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollPreJoinEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    public DollPreJoinEvent(Player whoJoined) {
        super(whoJoined);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
