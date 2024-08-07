package me.autobot.playerdoll.netty.protocol;


import io.netty.buffer.ByteBuf;
import io.netty.util.AttributeKey;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.util.ReflectionUtil;
import net.minecraft.network.protocol.Packet;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

public abstract class Pre1_20_R4_Protocols {

    public static final AttributeKey<Object> ATTRIBUTE_SERVERBOUND_PROTOCOL = AttributeKey.valueOf("serverbound_protocol");
    public static final AttributeKey<Object> ATTRIBUTE_CLIENTBOUND_PROTOCOL = AttributeKey.valueOf("clientbound_protocol");

    public static Object CLIENT_LOGIN_PROTOCOL;
    public static Object CLIENT_CONFIGURATION_PROTOCOL;
    public static Object CLIENT_PLAY_PROTOCOL;

    public static Object SERVER_LOGIN_PROTOCOL;
    public static Object SERVER_CONFIGURATION_PROTOCOL;
    public static Object SERVER_PLAY_PROTOCOL;

    public static final Method getPacketIdMethod;
    public static final Method createPacketMethod;

    public static final Constructor<?> friendlyByteBufConstructor;

    static {

        Class<?> connectionProtocolClass = ReflectionUtil.getClass("net.minecraft.network.EnumProtocol");
        Objects.requireNonNull(connectionProtocolClass, "connectionProtocol.class");
        Enum<?>[] connectionProtocolEnums;
        try {
            connectionProtocolEnums = (Enum<?>[]) connectionProtocolClass.getMethod("values").invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Field connectionProtocolNameField = Arrays.stream(connectionProtocolClass.getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType() == String.class)
                .findFirst()
                .orElseThrow();
        connectionProtocolNameField.setAccessible(true);

        Class<?> connectionProtocolCodecDataClass = ReflectionUtil.getClass("net.minecraft.network.EnumProtocol$a");
        Objects.requireNonNull(connectionProtocolCodecDataClass, "connectionProtocol$CodecData.class");
        Method connectionProtocolCodecMethod = Arrays.stream(connectionProtocolClass.getMethods())
                .filter(method -> method.getReturnType() == connectionProtocolCodecDataClass)
                .findFirst()
                .orElseThrow();
        connectionProtocolCodecMethod.setAccessible(true);

        for (Enum<?> protocol : connectionProtocolEnums) {
            String name = ReflectionUtil.getField(String.class, connectionProtocolNameField, protocol);
            switch (name) {
                case "login" -> {
                    CLIENT_LOGIN_PROTOCOL = ReflectionUtil.invokeMethod(connectionProtocolCodecMethod, protocol, DollConnection.packetFlow_Clientbound);
                    SERVER_LOGIN_PROTOCOL = ReflectionUtil.invokeMethod(connectionProtocolCodecMethod, protocol, DollConnection.packetFlow_Serverbound);
                }
                case "configuration" -> {
                    CLIENT_CONFIGURATION_PROTOCOL = ReflectionUtil.invokeMethod(connectionProtocolCodecMethod, protocol, DollConnection.packetFlow_Clientbound);
                    SERVER_CONFIGURATION_PROTOCOL = ReflectionUtil.invokeMethod(connectionProtocolCodecMethod, protocol, DollConnection.packetFlow_Serverbound);
                }
                case "play" -> {
                    CLIENT_PLAY_PROTOCOL = ReflectionUtil.invokeMethod(connectionProtocolCodecMethod, protocol, DollConnection.packetFlow_Clientbound);
                    SERVER_PLAY_PROTOCOL = ReflectionUtil.invokeMethod(connectionProtocolCodecMethod, protocol, DollConnection.packetFlow_Serverbound);
                }
            }
        }

        getPacketIdMethod = Arrays.stream(connectionProtocolCodecDataClass.getMethods())
                .filter(method -> method.getReturnType() == int.class)
                .findFirst()
                .orElseThrow();
        getPacketIdMethod.setAccessible(true);

        createPacketMethod = Arrays.stream(connectionProtocolCodecDataClass.getMethods())
                .filter(method -> method.getReturnType() == Packet.class)
                .findFirst()
                .orElseThrow();
        createPacketMethod.setAccessible(true);


        Class<?> friendlyByteBufClass = ReflectionUtil.getClass("net.minecraft.network.PacketDataSerializer");
        Objects.requireNonNull(friendlyByteBufClass, "friendlyByteBuf.class");
        friendlyByteBufConstructor = friendlyByteBufClass.getConstructors()[0];
    }


    public static int getPacketId(Object codec, Packet<?> packet) {
        return ReflectionUtil.invokeMethod(Integer.class, getPacketIdMethod, codec, packet);
    }
    public static Packet<?> createPacket(Object codec, int packetId, Object buf) {
        return ReflectionUtil.invokeMethod(Packet.class, createPacketMethod, codec, packetId, buf);
    }

    public static Object createFriendlyByteBuf(ByteBuf buf) {
        return ReflectionUtil.newInstance(friendlyByteBufConstructor, buf);
    }
}
