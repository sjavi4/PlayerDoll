package me.autobot.playerdoll.api.event.doll;

import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollSetting;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DollSettingEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final CommandSender who;
    private final Doll doll;
    private final boolean toggle;
    private final DollSetting setting;
    public DollSettingEvent(CommandSender who, Doll whoChanged, DollSetting setting, boolean b) {
        this.who = who;
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

    public CommandSender getWho() {
        return who;
    }

    public Doll getWhoChanged() {
        return doll;
    }
    public boolean getToggleState() {
        return toggle;
    }
    public DollSetting getSetting() {
        return setting;
    }
}
