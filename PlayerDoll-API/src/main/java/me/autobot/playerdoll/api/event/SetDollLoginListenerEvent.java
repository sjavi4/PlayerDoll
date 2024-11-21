package me.autobot.playerdoll.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.lang.reflect.Constructor;

public class SetDollLoginListenerEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private Constructor<?> constructor = null;
    public SetDollLoginListenerEvent() {
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }
}
