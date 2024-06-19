package me.autobot.playerdoll;

import me.autobot.playerdoll.listener.*;
import net.md_5.bungee.api.plugin.Plugin;

public class DollProxy extends Plugin {
    public static DollProxy PLUGIN;

//    public static final Map<String, PacketToServerListener> ADDRESS_LISTENERS = new ConcurrentHashMap<>();
//    public static final Map<UUID, UUID> DOLL_CALLER_MAP = new ConcurrentHashMap<>();
//    public static final Map<UUID, ServerInfo> DOLL_SERVERS = new ConcurrentHashMap<>();
//    public static final Map<String, PendingConnection> PENDING_DOLLS = new ConcurrentHashMap<>();
    @Override
    public void onEnable() {
        PLUGIN = this;
        getProxy().getPluginManager().registerListener(this, new HandshakeListener());
        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new ServerConnectListener());
        getProxy().getPluginManager().registerListener(this, new DisconnectListener());
        getProxy().getPluginManager().registerListener(this, new BungeeMessenger());
        getProxy().registerChannel("playerdoll:doll");

    }

    @Override
    public void onDisable() {
        //ADDRESS_LISTENERS.values().forEach(PacketToClientListener::close);
        getProxy().unregisterChannel("playerdoll:doll");
    }
}
