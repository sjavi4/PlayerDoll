package me.autobot.playerdoll;

import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Proxy extends Plugin {
    private static Proxy instance;
    public static Proxy getInstance() {
        return instance;
    }
    //public static final Map<String, Object> dollConnections = new HashMap<>();
    //public static final Map<UUID, String> dollNames = new HashMap<>();
    //public static final Map<UUID, Server> callerServer = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        //getProxy().getPluginManager().registerListener(this, new EventListener());
        getProxy().getPluginManager().registerListener(this, new BungeeCordMessenger());

        //getProxy().registerChannel("playerdoll:player");
        getProxy().registerChannel("playerdoll:doll");
    }

    @Override
    public void onDisable() {
        //getProxy().unregisterChannel("playerdoll:player");
        getProxy().unregisterChannel("playerdoll:doll");
    }
}
