package me.autobot.playerdoll.config;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import me.autobot.playerdoll.configkey.ConfigKey;
import me.autobot.playerdoll.configkey.PermKey;
import me.autobot.playerdoll.util.ConfigLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class PermConfig extends AbstractConfig {
    private static PermConfig INSTANCE;
    public static final String PERM_CREATE_STRING = "playerdoll.perm.max-doll-create.";
    public static final String PERM_PREFIX_STRING = "playerdoll.perm.doll-prefix.";
    public static final String PERM_SUFFIX_STRING = "playerdoll.perm.doll-suffix.";
    public final ConfigKey<PermConfig, Boolean> enable;
    //public final ConfigKey<PermConfig, Boolean> opBypass;
    //public final ConfigKey<PermConfig, Object2IntOpenHashMap<String>> maxDollSpawn;
    public final PermKey<String, Integer> maxDollCreate;
    public final PermKey<String, String> dollPrefix;
    public final PermKey<String, String> dollSuffix;
    //public final ConfigKey<PermConfig, Object2BooleanOpenHashMap<String>> restrictSkin;
    public final Object2IntLinkedOpenHashMap<String> groupPerCreateLimits = new Object2IntLinkedOpenHashMap<>();
    public final Map<String,String> dollPrefixes = new LinkedHashMap<>();
    public final Map<String,String> dollSuffixes = new LinkedHashMap<>();
    public PermConfig(YamlConfiguration config) {
        super(config);
        this.enable = new ConfigKey<>(this, "enable", false);
        //this.opBypass = new ConfigKey<>(this, "op-bypass", false);
        //this.maxDollSpawn = new ConfigKey<>(this,"playerdoll.perm.max-doll-spawn",new Object2IntOpenHashMap<>());
        this.maxDollCreate = new PermKey<>(this,"playerdoll.perm.max-doll-create",new Object2IntLinkedOpenHashMap<>());
        this.groupPerCreateLimits.putAll(maxDollCreate.getValue());
        this.dollPrefix = new PermKey<>(this,"playerdoll.perm.doll-prefix",new LinkedHashMap<>());
        this.dollSuffix = new PermKey<>(this,"playerdoll.perm.doll-suffix",new LinkedHashMap<>());
        this.dollPrefixes.putAll(dollPrefix.getValue());
        this.dollSuffixes.putAll(dollSuffix.getValue());
        //this.restrictSkin = new ConfigKey<>(this,"playerdoll.perm.restrict-skin",new HashMap<>());
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
