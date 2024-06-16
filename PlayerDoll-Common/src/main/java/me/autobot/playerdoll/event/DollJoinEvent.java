package me.autobot.playerdoll.event;

import me.autobot.playerdoll.doll.Doll;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollJoinEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Player caller;
    private final Doll doll;
    public DollJoinEvent(Player whoJoined, Player caller, Doll doll) {
        super(whoJoined);
        this.caller = caller;
        this.doll = doll;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
    public Player getCaller() {
        return caller;
    }

    public Doll getDoll() {
        return doll;
    }
}
