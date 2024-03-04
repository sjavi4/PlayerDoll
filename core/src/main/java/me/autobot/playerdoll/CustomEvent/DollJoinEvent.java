package me.autobot.playerdoll.CustomEvent;

import me.autobot.playerdoll.Dolls.IDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollJoinEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Player caller;
    private final IDoll doll;
    public DollJoinEvent(Player whoJoined, Player caller, IDoll doll) {
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

    public IDoll getDoll() {
        return doll;
    }
}
