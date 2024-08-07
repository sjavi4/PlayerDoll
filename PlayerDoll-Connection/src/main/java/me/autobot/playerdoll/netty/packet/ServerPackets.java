package me.autobot.playerdoll.netty.packet;

import me.autobot.playerdoll.util.ReflectionUtil;
import net.minecraft.network.protocol.Packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class ServerPackets {

    // Common
    private static final Constructor<?> keepAlivePacketConstructor;
    private static final Constructor<?> resourcePackResponseConstructor;
    // Login
    private static final Constructor<?> loginAckPacketConstructor;
    private static final Field loginAckPacketInstanceField;

    private static final Object resourcePackResponseAction_SUCCESSFULLY_LOADED;
    private static final Object resourcePackResponseAction_DECLINED;


//    private static Constructor<?> loginCustomPayloadAnswerPacketConstructor;

    // Config
    private static final Constructor<?> finishConfigPacketConstructor;
    private static final Field finishConfigPacketInstanceField;
    private static Constructor<?> selectKnownPacksPacketConstructor;

    // Game

    private static final Constructor<?> clientCommandPacketConstructor;
    private static final Constructor<?> acceptTeleportPacketConstructor;

    private static final Object clientCommandAction_performRespawn;

    static {
        // Common
        Class<?> keepAlivePacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ServerboundKeepAlivePacket");
        Objects.requireNonNull(keepAlivePacketClass, "ServerboundKeepAlivePacket.class");
        keepAlivePacketConstructor = Arrays.stream(keepAlivePacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == long.class)
                .findFirst()
                .orElseThrow();
        keepAlivePacketConstructor.setAccessible(true);

        Class<?> resourcePackResponseActionClass = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ServerboundResourcePackPacket$a");
        Objects.requireNonNull(resourcePackResponseActionClass, "ServerboundResourcePackPacket$Action.class");
        Enum<?>[] responseActionEnums;
        try {
            responseActionEnums = (Enum<?>[]) resourcePackResponseActionClass.getMethod("values").invoke(null);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        resourcePackResponseAction_SUCCESSFULLY_LOADED = responseActionEnums[0];
        resourcePackResponseAction_DECLINED = responseActionEnums[1];

        Class<?> resourcePackResponse = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ServerboundResourcePackPacket");
        Objects.requireNonNull(resourcePackResponse, "ServerboundResourcePackPacket.class");
        resourcePackResponseConstructor = Arrays.stream(resourcePackResponse.getDeclaredConstructors())
                .filter(constructor -> {
                    if (constructor.getParameterCount() == 1) {
                        return constructor.getParameterTypes()[0] == resourcePackResponseActionClass;
                    } else if (constructor.getParameterCount() == 2) {
                        return constructor.getParameterTypes()[0] == UUID.class;
                    }
                    return false;
                })
                .findFirst()
                .orElseThrow();
        resourcePackResponseConstructor.setAccessible(true);


        // Login
        Class<?> loginAckPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket");
        Objects.requireNonNull(loginAckPacketClass, "ServerboundLoginAcknowledgedPacket.class");
        loginAckPacketConstructor = Arrays.stream(loginAckPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        loginAckPacketConstructor.setAccessible(true);
        loginAckPacketInstanceField = Arrays.stream(loginAckPacketClass.getDeclaredFields())
                .filter(field -> field.getType() == loginAckPacketClass)
                .findFirst()
                .orElse(null);
        if (loginAckPacketInstanceField != null) {
            loginAckPacketInstanceField.setAccessible(true);
        }

//        Class<?> loginCustomPayloadAnswerPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket");
//        if (loginCustomPayloadAnswerPacketClass != null) {
//            loginCustomPayloadAnswerPacketConstructor = loginCustomPayloadAnswerPacketClass.getDeclaredConstructors()[0];
//            loginCustomPayloadAnswerPacketConstructor.setAccessible(true);
//        }

        // Config
        Class<?> finishConfigPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket");
        Objects.requireNonNull(finishConfigPacketClass, "ServerboundFinishConfigurationPacket.class");
        finishConfigPacketConstructor = Arrays.stream(finishConfigPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        finishConfigPacketConstructor.setAccessible(true);
        finishConfigPacketInstanceField = Arrays.stream(finishConfigPacketClass.getDeclaredFields())
                .filter(field -> field.getType() == finishConfigPacketClass)
                .findFirst()
                .orElse(null);
        if (finishConfigPacketInstanceField != null) {
            finishConfigPacketInstanceField.setAccessible(true);
        }

        Class<?> selectKnownPacksClass = ReflectionUtil.getClass("net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks");
        if (selectKnownPacksClass != null) {
            selectKnownPacksPacketConstructor = selectKnownPacksClass.getConstructors()[0];
            selectKnownPacksPacketConstructor.setAccessible(true);
        }

        // Game

        Class<?> acceptTeleportPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayInTeleportAccept");
        acceptTeleportPacketConstructor = Arrays.stream(acceptTeleportPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == int.class)
                .findFirst()
                .orElse(null);
        if (acceptTeleportPacketConstructor != null) {
            acceptTeleportPacketConstructor.setAccessible(true);
        }

        Class<?> clientCommandActionClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayInClientCommand$EnumClientCommand");
        Objects.requireNonNull(clientCommandActionClass);
        Enum<?>[] commandActions;
        try {
            commandActions = (Enum<?>[]) clientCommandActionClass.getMethod("values").invoke(null);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        clientCommandAction_performRespawn = commandActions[0];

        Class<?> clientCommandPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayInClientCommand");
        clientCommandPacketConstructor = Arrays.stream(clientCommandPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == clientCommandActionClass)
                .findFirst()
                .orElse(null);
        if (clientCommandPacketConstructor != null) {
            clientCommandPacketConstructor.setAccessible(true);
        }
    }


    public static Packet<?> createKeepAlivePacket(long id) {
        return (Packet<?>) ReflectionUtil.newInstance(keepAlivePacketConstructor, id);
    }
    public static Packet<?> createLoginAckPacket() {
        if (loginAckPacketInstanceField != null) {
            return ReflectionUtil.getField(Packet.class, loginAckPacketInstanceField, null);
        } else {
            return (Packet<?>) ReflectionUtil.newInstance(loginAckPacketConstructor);
        }
    }

    public static Packet<?> createFinishConfigPacket() {
        if (finishConfigPacketInstanceField != null) {
            return ReflectionUtil.getField(Packet.class, finishConfigPacketInstanceField, null);
        } else {
            return (Packet<?>) ReflectionUtil.newInstance(finishConfigPacketConstructor);
        }
    }
    public static Packet<?> createSelectKnownPacksPacket(List<?> packs) {
        return (Packet<?>) ReflectionUtil.newInstance(selectKnownPacksPacketConstructor, packs);
    }

//    public static Packet<?> createLoginCustomPayloadAnswerPacket(int payloadId) {
//        return (Packet<?>) ReflectionUtil.newInstance(loginCustomPayloadAnswerPacketConstructor, payloadId, null);
//    }
    public static Packet<?> createAcceptTeleportPacket(int id) {
        return (Packet<?>) ReflectionUtil.newInstance(acceptTeleportPacketConstructor, id);
    }
    public static Packet<?> createPerformRespawnPacket() {
        return (Packet<?>) ReflectionUtil.newInstance(clientCommandPacketConstructor, clientCommandAction_performRespawn);
    }

    public static Packet<?> createResourcePackResponsePacket(Object packet) {
        boolean required = ReflectionUtil.invokeMethod(Boolean.class, ClientPackets.getResourcePackRequiredMethod, packet);
        if (ClientPackets.getResourcePackPushUUIDMethod != null) {
            UUID uuid = ReflectionUtil.invokeMethod(UUID.class, ClientPackets.getResourcePackPushUUIDMethod, packet);
            return (Packet<?>) ReflectionUtil.newInstance(resourcePackResponseConstructor, uuid, required ? resourcePackResponseAction_SUCCESSFULLY_LOADED : resourcePackResponseAction_DECLINED);
        } else {
            return (Packet<?>) ReflectionUtil.newInstance(resourcePackResponseConstructor, required ? resourcePackResponseAction_SUCCESSFULLY_LOADED : resourcePackResponseAction_DECLINED);
        }
    }
}
