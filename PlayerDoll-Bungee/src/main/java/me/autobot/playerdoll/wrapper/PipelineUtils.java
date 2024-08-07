package me.autobot.playerdoll.wrapper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PipelineUtils {
    public static final Class<?> pipelineUtilsClass;
    private static final Field BASE_Field;
    private static final Class<? extends ChannelInitializer<Channel>> BASE_Class;
    private static final Method initialChannelMethod;

    static {
        try {
            pipelineUtilsClass = Class.forName("net.md_5.bungee.netty.PipelineUtils");
            BASE_Field = pipelineUtilsClass.getDeclaredField("BASE");
            BASE_Field.setAccessible(true);
            BASE_Class = (Class<? extends ChannelInitializer<Channel>>) BASE_Field.getType();
            initialChannelMethod = BASE_Class.getDeclaredMethod("initChannel", Channel.class);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChannelInitializer<Channel> getBASE() {
        try {
            return (ChannelInitializer<Channel>) BASE_Field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static void BASE_InitialChannel(Channel channel) {
        try {
            initialChannelMethod.invoke(getBASE(), channel);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
