package me.autobot.playerdoll.event;

import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.config.DollConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DollSettingEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Doll doll;
    private final boolean toggle;
    private final DollConfig.DollSettings setting;
    public DollSettingEvent(Player who, Doll whoChanged, DollConfig.DollSettings setting, boolean b) {
        super(who);
        this.doll = whoChanged;
        this.setting = setting;
        this.toggle = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
    public Doll getWhoChanged() {
        return doll;
    }
    public boolean getToggleState() {
        return toggle;
    }
    public DollConfig.DollSettings getSetting() {
        return setting;
    }
}
