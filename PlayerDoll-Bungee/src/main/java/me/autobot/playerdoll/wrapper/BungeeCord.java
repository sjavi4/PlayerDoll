package me.autobot.playerdoll.wrapper;

import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;

import java.lang.reflect.Field;
import java.util.Set;

public class BungeeCord {

    private static final Class<?> bungeeCordClass;
    private static final Field listenersField;
    static {
        try {
            bungeeCordClass = Class.forName("net.md_5.bungee.BungeeCord");
            listenersField = bungeeCordClass.getDeclaredField("listeners");
            listenersField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }
    private final Object bungeeCord;
    public BungeeCord(ProxyServer server) {
        bungeeCord = server;
    }

    public Set<Channel> serverChannels() {
        try {
            return (Set<Channel>) listenersField.get(bungeeCord);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
