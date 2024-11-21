package me.autobot.playerdoll.api.config.impl;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import me.autobot.playerdoll.api.config.AbstractConfig;
import me.autobot.playerdoll.api.config.key.ConfigKey;
import me.autobot.playerdoll.api.config.key.PermKey;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PermConfig extends AbstractConfig {
    public static final String PERM_CREATE_STRING = "playerdoll.perm.max-doll-create.";
    public static final String PERM_PREFIX_STRING = "playerdoll.perm.doll-prefix.";
    public static final String PERM_SUFFIX_STRING = "playerdoll.perm.doll-suffix.";
    public final ConfigKey<PermConfig, Boolean> enable;
    public final PermKey<String, Integer> maxDollCreate;
    public final PermKey<String, String> dollPrefix;
    public final PermKey<String, String> dollSuffix;
    public final Object2IntLinkedOpenHashMap<String> groupPerCreateLimits = new Object2IntLinkedOpenHashMap<>();
    public final Map<String,String> dollPrefixes = new LinkedHashMap<>();
    public final Map<String,String> dollSuffixes = new LinkedHashMap<>();
    public PermConfig(File configFile) {
        super(configFile, true);
        this.enable = new ConfigKey<>(this, "enable", false);
        this.maxDollCreate = new PermKey<>(this,"playerdoll.perm.max-doll-create",new Object2IntLinkedOpenHashMap<>());
        this.dollPrefix = new PermKey<>(this,"playerdoll.perm.doll-prefix",new LinkedHashMap<>());
        this.dollSuffix = new PermKey<>(this,"playerdoll.perm.doll-suffix",new LinkedHashMap<>());

        this.groupPerCreateLimits.putAll(maxDollCreate.getValue());
        this.dollPrefixes.putAll(dollPrefix.getValue());
        this.dollSuffixes.putAll(dollSuffix.getValue());
        saveConfig();
    }

    @Override
    public String name() {
        return "Permission Config";
    }
}
