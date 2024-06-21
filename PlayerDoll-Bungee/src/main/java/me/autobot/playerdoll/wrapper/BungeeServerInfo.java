package me.autobot.playerdoll.wrapper;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BungeeServerInfo {

    private static final Method removePlayerMethod;

    static {
        try {
            Class<?> bungeeServerInfoClass = Class.forName("net.md_5.bungee.BungeeServerInfo");
            removePlayerMethod = bungeeServerInfoClass.getMethod("removePlayer", ProxiedPlayer.class);

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


    }
    private final ServerInfo serverInfo;
    public BungeeServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public void removePlayer(ProxiedPlayer player) {
        try {
            removePlayerMethod.invoke(serverInfo, player);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
