package me.autobot.playerdoll.Util.Configs;

import me.autobot.playerdoll.Util.ConfigLoader;
import me.autobot.playerdoll.Util.Keys.ConfigKey;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class PermConfig extends AbstractConfig {
    private static PermConfig INSTANCE;
    public final ConfigKey<PermConfig, Boolean> enable;
    public final ConfigKey<PermConfig, Boolean> opBypass;
    public final ConfigKey<PermConfig, Map<String,Integer>> maxDollSpawn;
    public final ConfigKey<PermConfig, Map<String,Integer>> maxDollCreate;
    public final ConfigKey<PermConfig, Map<String,String>> dollPrefix;
    public final ConfigKey<PermConfig, Map<String,String>> dollSuffix;
    public final ConfigKey<PermConfig, Map<String,Boolean>> restrictSkin;
    public PermConfig(YamlConfiguration config) {
        super(config);
        this.enable = new ConfigKey<>(this, "enable", false);
        this.opBypass = new ConfigKey<>(this, "op-bypass", false);
        this.maxDollSpawn = new ConfigKey<>(this,"playerdoll.perm.max-doll-spawn",new HashMap<>());
        this.maxDollCreate = new ConfigKey<>(this,"playerdoll.perm.max-doll-create",new HashMap<>());
        this.dollPrefix = new ConfigKey<>(this,"playerdoll.perm.doll-prefix",new HashMap<>());
        this.dollSuffix = new ConfigKey<>(this,"playerdoll.perm.doll-suffix",new HashMap<>());
        this.restrictSkin = new ConfigKey<>(this,"playerdoll.perm.restrict-skin",new HashMap<>());
        ConfigLoader.get().saveConfig(this.yamlConfiguration, ConfigLoader.ConfigType.PERMISSION);
    }
    public static PermConfig get() {
        return INSTANCE == null? INSTANCE = new PermConfig(ConfigLoader.get().getConfig(ConfigLoader.ConfigType.PERMISSION)) : INSTANCE;
    }
    @Override
    public String getName() {
        return "Permission Config";
    }
}
