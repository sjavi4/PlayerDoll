package me.autobot.playerdoll.persistantdatatype;

import me.autobot.playerdoll.config.FlagConfig;

import java.util.Arrays;
import java.util.Optional;

public interface Button {
    boolean isToggleable();
    String getCommand();

    static Button valueOf(String v) {
        Optional<ButtonAction> action = Arrays.stream(ButtonAction.values()).filter(buttonAction -> buttonAction.name().equalsIgnoreCase(v)).findFirst();
        if (action.isPresent()) {
            return action.get();
        }
        Optional<FlagConfig.GlobalFlagType> global = Arrays.stream(FlagConfig.GlobalFlagType.values()).filter(flagType -> flagType.name().equalsIgnoreCase(v)).findFirst();
        if (global.isPresent()) {
            return global.get();
        }
        Optional<FlagConfig.PersonalFlagType> personal = Arrays.stream(FlagConfig.PersonalFlagType.values()).filter(flagType -> flagType.name().equalsIgnoreCase(v)).findFirst();
        if (personal.isPresent()) {
            return personal.get();
        }
        throw new NullPointerException();
    }
}
