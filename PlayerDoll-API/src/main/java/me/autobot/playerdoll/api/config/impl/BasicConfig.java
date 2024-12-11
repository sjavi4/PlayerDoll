package me.autobot.playerdoll.api.config.impl;


import me.autobot.playerdoll.api.config.AbstractConfig;
import me.autobot.playerdoll.api.config.key.ConfigKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BasicConfig extends AbstractConfig {
    public final ConfigKey<BasicConfig,String> serverMod;
    public final ConfigKey<BasicConfig,String> pluginLanguage;
    public final ConfigKey<BasicConfig,Boolean> checkUpdate;
//    public final ConfigKey<BasicConfig,Boolean> updateReplaceConfig;
    public final ConfigKey<BasicConfig,List<String>> preservedDollName;
    public final ConfigKey<BasicConfig,Boolean> adjustableMaxPlayer;
    public final ConfigKey<BasicConfig,Integer> serverMaxDollSpawn;
    public final ConfigKey<BasicConfig,Boolean> broadcastDollDeath;
    public final ConfigKey<BasicConfig,Boolean> broadcastDollJoin;
    public final ConfigKey<BasicConfig,Boolean> broadcastDollDisconnect;
    public final ConfigKey<BasicConfig,Boolean> displayDollWhenPing;
    public final ConfigKey<BasicConfig,Boolean> opCanSeeHiddenDoll;

    public final ConfigKey<BasicConfig,List<String>> dollPermission;
    public final ConfigKey<BasicConfig,List<String>> dollMetadata;
    public final ConfigKey<BasicConfig,Integer> dollChatWhenJoinInterval;
    public final ConfigKey<BasicConfig,List<String>> dollChatWhenJoin;
    public final ConfigKey<BasicConfig,Boolean> convertPlayer;
    public final ConfigKey<BasicConfig,Boolean> broadcastConvertShutdown;
    public final ConfigKey<BasicConfig,Boolean> forceProxyIP;
    public final ConfigKey<BasicConfig,String> proxyIP;
    public final ConfigKey<BasicConfig,Integer> proxyPort;
    public final ConfigKey<BasicConfig,Integer> autoJoinDelay;

    public final ConfigKey<BasicConfig,String> dollIdentifier;

    public BasicConfig(File configFile) {
        super(configFile, true);
        List<String> emptyStrList = new ArrayList<>(Collections.singletonList(""));
        this.serverMod = new ConfigKey<>(this,"server-mod","");
        this.pluginLanguage = new ConfigKey<>(this,"plugin-language", "default");
        this.checkUpdate = new ConfigKey<>(this,"check-update", true);
//        this.updateReplaceConfig = new ConfigKey<>(this,"update-replace-config",false);
        this.dollIdentifier = new ConfigKey<>(this, "doll-identifier", "-");

        this.preservedDollName = new ConfigKey<>(this,"preserved-doll-name", emptyStrList);
        this.adjustableMaxPlayer = new ConfigKey<>(this,"adjustable-max-player",true);
        this.serverMaxDollSpawn = new ConfigKey<>(this,"server-max-doll-spawn",-1);
        this.broadcastDollDeath = new ConfigKey<>(this,"broadcast-doll-death",true);
        this.broadcastDollJoin = new ConfigKey<>(this,"broadcast-doll-join",false);
        this.broadcastDollDisconnect = new ConfigKey<>(this,"broadcast-doll-disconnect",false);
        this.displayDollWhenPing = new ConfigKey<>(this, "display-doll-when-ping", false);
        this.opCanSeeHiddenDoll = new ConfigKey<>(this, "op-can-see-hidden-doll", true);
        this.dollPermission = new ConfigKey<>(this,"doll-permission",emptyStrList);
        this.dollMetadata = new ConfigKey<>(this, "doll-metadata", emptyStrList);
        this.dollChatWhenJoinInterval = new ConfigKey<>(this, "chat-when-join-interval", 10);
        this.dollChatWhenJoin = new ConfigKey<>(this,"chat-when-join", emptyStrList);
        this.convertPlayer = new ConfigKey<>(this, "convert-player", false);
        this.broadcastConvertShutdown = new ConfigKey<>(this, "broadcast-convert-shutdown", true);
        this.forceProxyIP = new ConfigKey<>(this, "force-proxy-ip", false);
        this.proxyIP = new ConfigKey<>(this, "proxy-ip", "127.0.0.1");
        this.proxyPort = new ConfigKey<>(this, "proxy-port", 25565);
        this.autoJoinDelay = new ConfigKey<>(this, "auto-join-delay", 1);

        String dollId = dollIdentifier.getValue();
        if (dollId.length() > 1) {
            dollIdentifier.setNewValue("-");
        }

        this.preservedDollName.setNewValue(this.preservedDollName.getValue().stream().map(String::toLowerCase).toList());

        saveConfig();
        unloadYAML();
    }
    @Override
    public String name() {
        return "Basic Config";
    }
}
