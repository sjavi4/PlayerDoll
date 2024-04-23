package me.autobot.playerdoll.CustomEvent;

import me.autobot.playerdoll.Dolls.IServerDoll;
import me.autobot.playerdoll.Dolls.IServerPlayerExt;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollJoinEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Player caller;
    private final IServerDoll doll;
    public DollJoinEvent(Player whoJoined, Player caller, IServerDoll doll) {
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

    public IServerDoll getDoll() {
        return doll;
    }
}
