package me.autobot.playerdoll.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Function;

public class SetConvertPlayerCheckProtocolEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private Function<Object, Boolean> checkProtocol = null;
    public SetConvertPlayerCheckProtocolEvent() {

    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Function<Object, Boolean> getCheckProtocol() {
        return checkProtocol;
    }

    public void setCheckProtocol(Function<Object, Boolean> checkProtocol) {
        this.checkProtocol = checkProtocol;
    }
}
