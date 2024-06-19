package me.autobot.playerdoll.wrapper;

import io.netty.channel.Channel;

import java.lang.reflect.Field;

public class ChannelWrapper {
    public static final Class<?> channelWrapperClass;
    private static final Field channelField;

    static {
        try {
            channelWrapperClass = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
            channelField = channelWrapperClass.getDeclaredField("ch");
            channelField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    private final Object instance;
    public ChannelWrapper(Object wrapper) {
        instance = wrapper;
    }

    public Channel channel() {
        try {
            return (Channel) channelField.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
