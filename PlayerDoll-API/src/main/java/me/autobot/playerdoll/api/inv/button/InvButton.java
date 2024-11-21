package me.autobot.playerdoll.api.inv.button;

import me.autobot.playerdoll.api.PlayerDollAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class InvButton {

    private static final Map<String, InvButton> BUTTONS = new LinkedHashMap<>();

    public static InvButton put(InvButton button) {
        if (BUTTONS.values().stream().anyMatch(b -> b.registerName().equals(button.registerName()))) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Ignored Duplicated InvButton [{0}]", button.registerName());
            return null;
        }
        return BUTTONS.put(button.registerName(), button);
    }

    public static Map<String, InvButton> getButtons() {
        return BUTTONS;
    }

    public abstract boolean isToggleable();
    public abstract String registerName();

}
