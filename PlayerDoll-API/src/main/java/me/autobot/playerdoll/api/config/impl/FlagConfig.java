package me.autobot.playerdoll.api.config.impl;

import me.autobot.playerdoll.api.config.AbstractConfig;
import me.autobot.playerdoll.api.config.key.FlagKey;
import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;
import me.autobot.playerdoll.api.inv.button.InvButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FlagConfig extends AbstractConfig {
    private final Map<GlobalFlagButton, FlagKey<FlagConfig>> globalFlags = new LinkedHashMap<>();
    private final Map<PersonalFlagButton, FlagKey<FlagConfig>> personalFlags = new LinkedHashMap<>();
    public FlagConfig(File configFile) {
        super(configFile, true);
        InvButton.getButtons().values().stream().filter(invButton -> invButton instanceof GlobalFlagButton)
                        .forEach(button -> {
                            GlobalFlagButton flagButton = (GlobalFlagButton) button;
                            globalFlags.put(flagButton, new FlagKey<>(FlagConfig.this, flagButton.getFlagPath(), flagButton.getMaterial()));
                        });
        InvButton.getButtons().values().stream().filter(invButton -> invButton instanceof PersonalFlagButton)
                .forEach(button -> {
                    PersonalFlagButton flagButton = (PersonalFlagButton) button;
                    personalFlags.put(flagButton, new FlagKey<>(FlagConfig.this, flagButton.getFlagPath(), flagButton.getMaterial()));
                });

        saveConfig();
        unloadYAML();
    }

    public Map<GlobalFlagButton, FlagKey<FlagConfig>> getGlobalFlags() {
        return globalFlags;
    }

    public Map<PersonalFlagButton, FlagKey<FlagConfig>> getPersonalFlags() {
        return personalFlags;
    }

    @Override
    public String name() {
        return "Flag Config";
    }
}
