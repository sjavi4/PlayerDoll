package me.autobot.playerdoll.wrapper;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Field;

public class UserConnection {
    private static final Class<?> userConnectionClass;
    private static final Field nameField;
    private static final Field channelWrapperField;
    private static final Field unsafeField;

    static {
        try {
            userConnectionClass = Class.forName("net.md_5.bungee.UserConnection");
            nameField = userConnectionClass.getDeclaredField("name");
            nameField.setAccessible(true);
            channelWrapperField = userConnectionClass.getDeclaredField("ch");
            channelWrapperField.setAccessible(true);
            unsafeField = userConnectionClass.getDeclaredField("unsafe");
            unsafeField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }
    private final Object userConnection;
    public UserConnection(ProxiedPlayer player) {
        userConnection = player;
    }

    public String getName() {
        try {
            return (String) nameField.get(userConnection);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void setName(String name) {
        try {
            nameField.set(userConnection, name);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ChannelWrapper channelWrapper() {
        try {
            return new ChannelWrapper(channelWrapperField.get(userConnection));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUnsafe(Connection.Unsafe unsafe) {
        try {
            unsafeField.set(userConnection, unsafe);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
