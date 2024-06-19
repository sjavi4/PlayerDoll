package me.autobot.playerdoll.config;

import me.autobot.playerdoll.configkey.ConfigKey;
import me.autobot.playerdoll.util.ConfigLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicConfig extends AbstractConfig {
    private static final List<String> DEFAULT_STRING_LIST = new ArrayList<>(Collections.singletonList(""));
    private static BasicConfig INSTANCE;
    public final ConfigKey<BasicConfig,String> serverMod;
    public final ConfigKey<BasicConfig,String> pluginLanguage;
    public final ConfigKey<BasicConfig,Boolean> checkUpdate;
    public final ConfigKey<BasicConfig,Boolean> updateReplaceConfig;
//    public final ConfigKey<BasicConfig,Boolean> configAutoReload;
//    public final ConfigKey<BasicConfig,Boolean> backupStartUp;
//    public final ConfigKey<BasicConfig,Boolean> backupShutDown;
//    public final ConfigKey<BasicConfig,Boolean> removeInactiveDoll;
//    public final ConfigKey<BasicConfig,Integer> removeTime;
    public final ConfigKey<BasicConfig,List<String>> preservedDollName;
    public final ConfigKey<BasicConfig,Boolean> adjustableMaxPlayer;
    public final ConfigKey<BasicConfig,Integer> serverMaxDollSpawn;
    public final ConfigKey<BasicConfig,Boolean> broadcastDollDeath;
    public final ConfigKey<BasicConfig,Boolean> broadcastDollJoin;
    public final ConfigKey<BasicConfig,Boolean> broadcastDollDisconnect;

    public final ConfigKey<BasicConfig,List<String>> dollPermission;
    public final ConfigKey<BasicConfig,Integer> dollChatWhenJoinInterval;
    public final ConfigKey<BasicConfig,List<String>> dollChatWhenJoin;
//    public final ConfigKey<BasicConfig,Boolean> dollMultiInstance;
    public final ConfigKey<BasicConfig,Boolean> convertPlayer;
    public final ConfigKey<BasicConfig,Boolean> broadcastConvertShutdown;
    public final ConfigKey<BasicConfig,String> proxyIP;
    public final ConfigKey<BasicConfig,Integer> proxyPort;
    public final ConfigKey<BasicConfig,Integer> proxyAutoJoinDelay;

    @SuppressWarnings("unchecked")
    public BasicConfig(YamlConfiguration conf) {
        super(conf);
        this.serverMod = new ConfigKey<>(this,"server-mod","");
        this.pluginLanguage = new ConfigKey<>(this,"plugin-language", "default");
        ConfigLoader.CUSTOM_LANGUAGE_NAME = this.pluginLanguage.getValue();
        this.checkUpdate = new ConfigKey<>(this,"check-update", true);
        this.updateReplaceConfig = new ConfigKey<>(this,"update-replace-config",false);
//        this.configAutoReload = new ConfigKey<>(this,"config-auto-reload",false);
//        this.backupStartUp = new ConfigKey<>(this,"backup-startup", false);
//        this.backupShutDown = new ConfigKey<>(this,"backup-shutdown",false);
//        this.removeInactiveDoll = new ConfigKey<>(this,"remove-inactive-doll",false);
//        this.removeTime = new ConfigKey<>(this,"remove-time",30);
        this.preservedDollName = new ConfigKey<>(this,"preserved-doll-name", DEFAULT_STRING_LIST);
        this.adjustableMaxPlayer = new ConfigKey<>(this,"adjustable-max-player",true);
        this.serverMaxDollSpawn = new ConfigKey<>(this,"server-max-doll-spawn",-1);
        this.broadcastDollDeath = new ConfigKey<>(this,"broadcast-doll-death",true);
        this.broadcastDollJoin = new ConfigKey<>(this,"broadcast-doll-join",false);
        this.broadcastDollDisconnect = new ConfigKey<>(this,"broadcast-doll-disconnect",false);
        this.dollPermission = new ConfigKey<>(this,"doll-permission",DEFAULT_STRING_LIST);
        this.dollChatWhenJoinInterval = new ConfigKey<>(this, "chat-when-join-interval", 10);
        this.dollChatWhenJoin = new ConfigKey<>(this,"chat-when-join", DEFAULT_STRING_LIST);
//        this.dollMultiInstance = new ConfigKey<>(this, "doll-multi-instance", false);
        this.convertPlayer = new ConfigKey<>(this, "convert-player", false);
        this.broadcastConvertShutdown = new ConfigKey<>(this, "broadcast-convert-shutdown", true);
        this.proxyIP = new ConfigKey<>(this, "proxy-ip", "127.0.0.1");
        this.proxyPort = new ConfigKey<>(this, "proxy-port", 25565);
        this.proxyAutoJoinDelay = new ConfigKey<>(this, "proxy-auto-join-delay", 1);
        ConfigLoader.get().saveConfig(this.yamlConfiguration, ConfigLoader.ConfigType.BASIC);
    }

    public static BasicConfig get() {
        return INSTANCE == null? INSTANCE = new BasicConfig(ConfigLoader.get().getConfig(ConfigLoader.ConfigType.BASIC)) : INSTANCE;
    }
    @Override
    public String getName() {
        return "Basic Config";
    }
}
