package me.autobot.playerdoll.api.resolver;

import me.autobot.playerdoll.api.ReflectionUtil;
import net.minecraft.network.protocol.Packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ServerPackets {

    // Common
    private static final Constructor<?> commonKeepAliveConstructor;
    private static final Constructor<?> commonResourcePackResponseConstructor;
    // Login
    private static final Constructor<?> loginAckConstructor;
    private static final Field loginAckStaticInstanceField;

    private static final Object resourcePackResponseAction_SUCCESSFULLY_LOADED;
    private static final Object resourcePackResponseAction_DECLINED;


//    private static Constructor<?> loginCustomPayloadAnswerPacketConstructor;

    // Config
    private static final Constructor<?> configFinishConfigConstructor;
    private static final Field configFinishConfigStaticInstanceField;
    private static Constructor<?> configSelectKnownPacksConstructor;

    // Game

    private static final Constructor<?> gameClientCommandConstructor;
    private static final Constructor<?> gameAcceptTeleportConstructor;

    private static final Object clientCommandAction_performRespawn;

    static {
        // Common
        Class<?> keepAlivePacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ServerboundKeepAlivePacket");
        Objects.requireNonNull(keepAlivePacketClass, "ServerboundKeepAlivePacket.class");
        commonKeepAliveConstructor = Arrays.stream(keepAlivePacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == long.class)
                .findFirst()
                .orElseThrow();
        commonKeepAliveConstructor.setAccessible(true);

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
        commonResourcePackResponseConstructor = Arrays.stream(resourcePackResponse.getDeclaredConstructors())
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
        commonResourcePackResponseConstructor.setAccessible(true);


        // Login
        Class<?> loginAckPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket");
        Objects.requireNonNull(loginAckPacketClass, "ServerboundLoginAcknowledgedPacket.class");
        loginAckConstructor = Arrays.stream(loginAckPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        loginAckConstructor.setAccessible(true);
        loginAckStaticInstanceField = Arrays.stream(loginAckPacketClass.getDeclaredFields())
                .filter(field -> field.getType() == loginAckPacketClass)
                .findFirst()
                .orElse(null);
        if (loginAckStaticInstanceField != null) {
            loginAckStaticInstanceField.setAccessible(true);
        }

//        Class<?> loginCustomPayloadAnswerPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket");
//        if (loginCustomPayloadAnswerPacketClass != null) {
//            loginCustomPayloadAnswerPacketConstructor = loginCustomPayloadAnswerPacketClass.getDeclaredConstructors()[0];
//            loginCustomPayloadAnswerPacketConstructor.setAccessible(true);
//        }

        // Config
        Class<?> finishConfigPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket");
        Objects.requireNonNull(finishConfigPacketClass, "ServerboundFinishConfigurationPacket.class");
        configFinishConfigConstructor = Arrays.stream(finishConfigPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        configFinishConfigConstructor.setAccessible(true);
        configFinishConfigStaticInstanceField = Arrays.stream(finishConfigPacketClass.getDeclaredFields())
                .filter(field -> field.getType() == finishConfigPacketClass)
                .findFirst()
                .orElse(null);
        if (configFinishConfigStaticInstanceField != null) {
            configFinishConfigStaticInstanceField.setAccessible(true);
        }

        Class<?> selectKnownPacksClass = ReflectionUtil.getClass("net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks");
        if (selectKnownPacksClass != null) {
            configSelectKnownPacksConstructor = selectKnownPacksClass.getConstructors()[0];
            configSelectKnownPacksConstructor.setAccessible(true);
        }

        // Game

        Class<?> acceptTeleportPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayInTeleportAccept");
        gameAcceptTeleportConstructor = Arrays.stream(acceptTeleportPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == int.class)
                .findFirst()
                .orElse(null);
        if (gameAcceptTeleportConstructor != null) {
            gameAcceptTeleportConstructor.setAccessible(true);
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
        gameClientCommandConstructor = Arrays.stream(clientCommandPacketClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == clientCommandActionClass)
                .findFirst()
                .orElse(null);
        if (gameClientCommandConstructor != null) {
            gameClientCommandConstructor.setAccessible(true);
        }
    }


    public static Packet<?> createKeepAlivePacket(long id) {
        return (Packet<?>) ReflectionUtil.newInstance(commonKeepAliveConstructor, id);
    }
    public static Packet<?> createLoginAckPacket() {
        if (loginAckStaticInstanceField != null) {
            return ReflectionUtil.getField(Packet.class, loginAckStaticInstanceField, null);
        } else {
            return (Packet<?>) ReflectionUtil.newInstance(loginAckConstructor);
        }
    }

    public static Packet<?> createFinishConfigPacket() {
        if (configFinishConfigStaticInstanceField != null) {
            return ReflectionUtil.getField(Packet.class, configFinishConfigStaticInstanceField, null);
        } else {
            return (Packet<?>) ReflectionUtil.newInstance(configFinishConfigConstructor);
        }
    }
    public static Packet<?> createSelectKnownPacksPacket(List<?> packs) {
        return (Packet<?>) ReflectionUtil.newInstance(configSelectKnownPacksConstructor, packs);
    }

//    public static Packet<?> createLoginCustomPayloadAnswerPacket(int payloadId) {
//        return (Packet<?>) ReflectionUtil.newInstance(loginCustomPayloadAnswerPacketConstructor, payloadId, null);
//    }
    public static Packet<?> createAcceptTeleportPacket(int id) {
        return (Packet<?>) ReflectionUtil.newInstance(gameAcceptTeleportConstructor, id);
    }
    public static Packet<?> createPerformRespawnPacket() {
        return (Packet<?>) ReflectionUtil.newInstance(gameClientCommandConstructor, clientCommandAction_performRespawn);
    }

    public static Packet<?> createResourcePackResponsePacket(Object packet) {
        boolean required = ReflectionUtil.invokeMethod(Boolean.class, ClientPackets.getResourcePackRequiredMethod, packet);
        if (ClientPackets.getResourcePackPushUUIDMethod != null) {
            UUID uuid = ReflectionUtil.invokeMethod(UUID.class, ClientPackets.getResourcePackPushUUIDMethod, packet);
            return (Packet<?>) ReflectionUtil.newInstance(commonResourcePackResponseConstructor, uuid, required ? resourcePackResponseAction_SUCCESSFULLY_LOADED : resourcePackResponseAction_DECLINED);
        } else {
            return (Packet<?>) ReflectionUtil.newInstance(commonResourcePackResponseConstructor, required ? resourcePackResponseAction_SUCCESSFULLY_LOADED : resourcePackResponseAction_DECLINED);
        }
    }
}
