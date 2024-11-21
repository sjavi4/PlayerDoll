package me.autobot.playerdoll.api.resolver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.constant.AbsServerBranch;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Connections {

    // ConnectionFetcher Start
    public static Field vanillaConnectionListenerField;
    public static final Field connectionListField;
    public static final boolean FOLIA = PlayerDollAPI.getServerBranch() == AbsServerBranch.FOLIA;
    public static final Class<?> C_PACKET_LISTENER = ReflectionUtil.getClass("net.minecraft.network.PacketListener");
    public static final Field connectionPacketListenerField;
    public static final Field connectionChannelField;
    public static final Class<?> NMSLoginListenerClass = ReflectionUtil.getNMSClass("network.LoginListener");
    // Set Here
    public static Class<?> convertPlayerCustomLoginListenerClass = null;
    // Set Here
    public static Class<?> dollCustomLoginListenerClass = null;
    public static Constructor<?> dollCustomLoginListenerConstructor = null;
    // ConnectionFetcher End

    // Connection Start
    public static final Method configureSerializationMethod;
    public static final Object packetFlow_Clientbound;
    public static final Object packetFlow_Serverbound;
    // Connection End


    // ConnectionFetcher Start
    static {
        if (FOLIA) {
            try {
                Class<?> foliaRegoinizedServerClass = ReflectionUtil.getFoliaRegoinizedServerClass();
                connectionListField = foliaRegoinizedServerClass.getDeclaredField("connections");
                connectionListField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } else {
            // ServerConnectionListener (mojang) / ServerConnection
            Class<?> serverConnectionListenerClass = ReflectionUtil.getNMSClass("network.ServerConnection");
            vanillaConnectionListenerField = Arrays.stream(ReflectionUtil.getDedicatedServerClass().getSuperclass().getDeclaredFields())
                    .filter(field -> field.getType() == serverConnectionListenerClass)
                    .findFirst()
                    .orElseThrow();
            vanillaConnectionListenerField.setAccessible(true);

            connectionListField = Arrays.stream(serverConnectionListenerClass.getDeclaredFields())
                    .filter(field -> Modifier.isFinal(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()))
                    .filter(field -> field.getGenericType() instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == List.class && parameterizedType.getActualTypeArguments()[0] != ChannelFuture.class)
                    .findFirst()
                    .orElseThrow();
            connectionListField.setAccessible(true);
        }

        Class<?> connectionClass = ReflectionUtil.getClass("net.minecraft.network.NetworkManager");
        Objects.requireNonNull(connectionClass, "connectionClass");
        connectionPacketListenerField = Arrays.stream(connectionClass.getDeclaredFields())
                .filter(field -> field.getType() == C_PACKET_LISTENER)
                // Should always be the second Field
                .toList().get(1);
        connectionPacketListenerField.setAccessible(true);

        connectionChannelField = Arrays.stream(connectionClass.getFields())
                .filter(field -> field.getType() == Channel.class)
                .findFirst()
                .orElseThrow();
        connectionListField.setAccessible(true);
    }
    // ConnectionFetcher End

    // Connection Start
    static {
        // Connection.class / NetworkManager.class
        Class<?> connectionClass = ReflectionUtil.getClass("net.minecraft.network.NetworkManager");
        // PacketFlow.class / EnumProtocolDirection.class
        Class<?> packetFlowClass = ReflectionUtil.getClass("net.minecraft.network.protocol.EnumProtocolDirection");
        // BandwidthDebugMonitor.class
        Class<?> bandwidthDebugMonitorClass = ReflectionUtil.getClass("net.minecraft.network.BandwidthDebugMonitor");

        Objects.requireNonNull(connectionClass, "Connection.class");
        Objects.requireNonNull(packetFlowClass, "PacketFlow.class");

        Object[] enums = packetFlowClass.getEnumConstants();
        Objects.requireNonNull(enums, "PacketFlow$enums");

        packetFlow_Serverbound = enums[0];
        packetFlow_Clientbound = enums[1];

        configureSerializationMethod = Arrays.stream(connectionClass.getDeclaredMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()) && method.getReturnType() == void.class)
                .filter(method -> {
                    Class<?>[] param = method.getParameterTypes();
                    if (method.getParameterCount() == 3) {
                        return param[0] == ChannelPipeline.class && param[1] == packetFlowClass && param[2] == bandwidthDebugMonitorClass;
                    } else if (method.getParameterCount() == 4) {
                        return param[0] == ChannelPipeline.class && param[1] == packetFlowClass && param[2] == boolean.class && param[3] == bandwidthDebugMonitorClass;
                    }
                    return false;
                })
                .findFirst().orElseThrow();
        configureSerializationMethod.setAccessible(true);
    }
    // Connection End
}
