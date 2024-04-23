package me.autobot.playerdoll.CustomEvent;

import me.autobot.playerdoll.Dolls.DollConfig;

import me.autobot.playerdoll.Dolls.IServerDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollSettingChangeEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final IServerDoll doll;
    private boolean toggle;
    private final DollConfig.SettingType type;
    public DollSettingChangeEvent(Player who, IServerDoll whoChanged, DollConfig.SettingType type, boolean b) {
        super(who);
        this.doll = whoChanged;
        this.type = type;
        this.toggle = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
    public IServerDoll getWhoChanged() {
        return doll;
    }
    public boolean getToggleState() {
        return toggle;
    }
    //public void setToggleState(boolean b) {
    //    toggle = b;
    //}
    public DollConfig.SettingType getType() {
        return type;
    }
}
