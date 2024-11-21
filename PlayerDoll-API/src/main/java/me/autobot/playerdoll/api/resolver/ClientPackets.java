package me.autobot.playerdoll.api.resolver;

import me.autobot.playerdoll.api.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.*;

public final class ClientPackets {

    private static final String s = "network.protocol.";
    // Other
    private static final Method componentGetStringMethod;
    // Common
    public static final Class<?> commonKeepAlive = ReflectionUtil.getNMClass(s.concat("common.ClientboundKeepAlivePacket"));
    public static final Class<?> commonDisconnect = ReflectionUtil.getNMClass(s.concat("common.ClientboundDisconnectPacket"));

    // 1.20.4+
    public static final Class<?> commonResourcePackPush_1204 = ReflectionUtil.getNMClass(s.concat("common.ClientboundResourcePackPushPacket"));

    // 1.20.2
    public static final Class<?> commonResourcePack_1202 = ReflectionUtil.getNMClass(s.concat("common.ClientboundResourcePackPacket"));

    private static final Method getKeepAliveIdMethod;
    private static final Method getCommonDisconnectReasonMethod;
    private static final Method getLoginDisconnectReasonMethod;

    public static Method getResourcePackRequiredMethod;

    // 1.20.4+
    public static Method getResourcePackPushUUIDMethod;

    // Login
    public static final Class<?> loginCompression = ReflectionUtil.getNMClass(s.concat("login.PacketLoginOutSetCompression"));
    public static final Class<?> loginGameProfile = ReflectionUtil.getNMClass(s.concat("login.PacketLoginOutSuccess"));

    public static final Class<?> loginDisconnect = ReflectionUtil.getNMClass(s.concat("login.PacketLoginOutDisconnect"));

//    public static final Class<?> loginCustomPayloadClass = ReflectionUtil.getNMClass(s.concat("login.PacketLoginOutCustomPayload");
    private static final Method getCompressionThresholdMethod;
//    private static Method getLoginCustomPayloadIdMethod;
    // Configuration
    public static final Class<?> configSelectKnownPacks = ReflectionUtil.getNMClass(s.concat("configuration.ClientboundSelectKnownPacks"));
//    public static Class<?> serverLinkPacketClass = ReflectionUtil.getNMClass(s.concat("configuration.ClientboundServerLinksPacket");
    public static final Class<?> configFinishConfig = ReflectionUtil.getNMClass(s.concat("configuration.ClientboundFinishConfigurationPacket"));

    public static Method getSelectKnownPacksMethod;

//    public static Method getServerLinkMethod;

    // Game
    public static final Class<?> gameLogin = ReflectionUtil.getNMClass(s.concat("game.PacketPlayOutLogin"));
    public static final Class<?> gameDeathScreen = ReflectionUtil.getNMClass(s.concat("game.ClientboundPlayerCombatKillPacket"));
    public static final Class<?> gameEvent = ReflectionUtil.getNMClass(s.concat("game.PacketPlayOutGameStateChange"));
    public static final Class<?> gamePlayerPosition = ReflectionUtil.getNMClass(s.concat("game.PacketPlayOutPosition"));


    private static final Method getPlayerPositionPacketIdMethod;
    public static final Set<Class<?>> packetSet = new HashSet<>() {{
        add(commonKeepAlive);
        add(commonDisconnect);
        add(gameDeathScreen);
        add(gameEvent);
        add(gamePlayerPosition);
    }};



    static {
        // Component
        Class<?> componentClass = ReflectionUtil.getClass("net.minecraft.network.chat.IChatBaseComponent");
        Objects.requireNonNull(componentClass, "IChatBaseComponent.class");
        componentGetStringMethod = Arrays.stream(componentClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == String.class && method.getParameterCount() == 0)
                .filter(method -> method.getName().equals("getString"))
                .findFirst()
                .orElseThrow();
        componentGetStringMethod.setAccessible(true);


        // Common
        Objects.requireNonNull(commonKeepAlive, "ClientboundKeepAlivePacket.class");
        getKeepAliveIdMethod = Arrays.stream(commonKeepAlive.getDeclaredMethods())
                .filter(method -> method.getReturnType() == long.class)
                .findFirst()
                .orElseThrow();
        getKeepAliveIdMethod.setAccessible(true);

        Objects.requireNonNull(commonDisconnect, "ClientboundDisconnectPacket.class");
        getCommonDisconnectReasonMethod = Arrays.stream(commonDisconnect.getDeclaredMethods())
                .filter(method -> method.getReturnType() == componentClass && method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        getCommonDisconnectReasonMethod.setAccessible(true);

        Objects.requireNonNull(loginDisconnect, "ClientboundLoginDisconnectPacket.class");
        getLoginDisconnectReasonMethod = Arrays.stream(loginDisconnect.getDeclaredMethods())
                .filter(method -> method.getReturnType() == componentClass && method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        getLoginDisconnectReasonMethod.setAccessible(true);


        // Login
        Objects.requireNonNull(loginCompression, "ClientboundLoginCompressionPacket.class");
        getCompressionThresholdMethod = Arrays.stream(loginCompression.getDeclaredMethods())
                .filter(method -> method.getReturnType() == int.class)
                .findFirst()
                .orElseThrow();
        getCompressionThresholdMethod.setAccessible(true);

//        if (loginCustomPayloadClass != null) {
//            Class<?> customPayloadClass = ReflectionUtil.getNMClass(s.concat("login.custom.CustomQueryPayload");
//            Objects.requireNonNull(customPayloadClass, "CustomQueryPayload.class");
//            getLoginCustomPayloadIdMethod = Arrays.stream(loginCustomPayloadClass.getDeclaredMethods())
//                    .filter(method -> method.getReturnType() == int.class)
//                    .findFirst()
//                    .orElseThrow();
//            getLoginCustomPayloadIdMethod.setAccessible(true);
//        }

        // Config
        if (configSelectKnownPacks != null) {
            getSelectKnownPacksMethod = Arrays.stream(configSelectKnownPacks.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == List.class)
                    .findFirst()
                    .orElseThrow();
            getSelectKnownPacksMethod.setAccessible(true);
        }
//        if (serverLinkPacketClass != null) {
//            getServerLinkMethod = Arrays.stream(serverLinkPacketClass.getDeclaredMethods())
//                    .filter(method -> method.getReturnType() == List.class)
//                    .findFirst()
//                    .orElseThrow();
//            getServerLinkMethod.setAccessible(true);
//        }

        Objects.requireNonNull(gamePlayerPosition, "playerPositionPacket.class");
        getPlayerPositionPacketIdMethod = Arrays.stream(gamePlayerPosition.getDeclaredMethods())
                .filter(method -> method.getReturnType() == int.class)
                .findFirst()
                .orElseThrow();
        getPlayerPositionPacketIdMethod.setAccessible(true);

        if (commonResourcePack_1202 != null) {
            getResourcePackRequiredMethod = Arrays.stream(commonResourcePack_1202.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == boolean.class && method.getParameterCount() == 0)
                    .findFirst().orElseThrow();
            getResourcePackRequiredMethod.setAccessible(true);
        }

        if (commonResourcePackPush_1204 != null) {
            getResourcePackPushUUIDMethod = Arrays.stream(commonResourcePackPush_1204.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == UUID.class)
                    .findFirst()
                    .orElseThrow();
            getResourcePackPushUUIDMethod.setAccessible(true);
            getResourcePackRequiredMethod = Arrays.stream(commonResourcePackPush_1204.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == boolean.class && method.getParameterCount() == 0)
                    .findFirst().orElseThrow();
            getResourcePackRequiredMethod.setAccessible(true);
        }
    }





    public static long getKeepAliveId(Object packet) {
        return ReflectionUtil.invokeMethod(Long.class, getKeepAliveIdMethod, packet);
    }
    public static int getCompressionThreshold(Object packet) {
        return ReflectionUtil.invokeMethod(Integer.class, getCompressionThresholdMethod, packet);
    }
    public static String getCommonDisconnectReason(Object packet) {
        return ReflectionUtil.invokeMethod(String.class, componentGetStringMethod, ReflectionUtil.invokeMethod(getCommonDisconnectReasonMethod, packet));
    }
    public static String getLoginDisconnectReason(Object packet) {
        return ReflectionUtil.invokeMethod(String.class, componentGetStringMethod, ReflectionUtil.invokeMethod(getLoginDisconnectReasonMethod, packet));
    }
//    public static int getLoginCustomPayloadId(Object packet) {
//        return ReflectionUtil.invokeMethod(Integer.class, getLoginCustomPayloadIdMethod, packet);
//    }


    public static List<?> getSelectKnownPacks(Object packet) {
        return ReflectionUtil.invokeMethod(List.class, getSelectKnownPacksMethod, packet);
    }

    public static int getPlayerPositionPacketId(Object packet) {
        return ReflectionUtil.invokeMethod(Integer.class, getPlayerPositionPacketIdMethod, packet);
    }
//    public static List<?> getServerLink(Object packet) {
//        return ReflectionUtil.invokeMethod(List.class, getServerLinkMethod, packet);
//    }
}
