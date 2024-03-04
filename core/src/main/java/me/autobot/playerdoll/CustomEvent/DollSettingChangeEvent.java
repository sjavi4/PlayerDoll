package me.autobot.playerdoll.CustomEvent;

import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.IDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollSettingChangeEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final IDoll doll;
    private boolean toggle;
    private final DollConfig.SettingType type;
    public DollSettingChangeEvent(Player who, IDoll whoChanged, DollConfig.SettingType type, boolean b) {
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
    public IDoll getWhoChanged() {
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
