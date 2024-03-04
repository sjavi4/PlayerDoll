package me.autobot.playerdoll.CustomEvent;

import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.IDoll;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DollConfigLoadEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final IDoll doll;
    private final DollConfig dollConfig;
    public DollConfigLoadEvent(IDoll who, DollConfig config) {
        this.doll = who;
        this.dollConfig = config;
    }

    public boolean isOfflineConfig() {
        return this.doll == null;
    }

    public IDoll getDoll() {
        return doll;
    }

    public DollConfig getDollConfig() {
        return dollConfig;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
