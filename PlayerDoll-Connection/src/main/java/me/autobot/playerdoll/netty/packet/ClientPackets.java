package me.autobot.playerdoll.netty.packet;

import me.autobot.playerdoll.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.*;

public abstract class ClientPackets {

    private static final String s = "net.minecraft.network.protocol.";
    // Other
    private static final Method componentGetStringMethod;
    // Common
    public static final Class<?> commonKeepAlivePacketClass = ReflectionUtil.getClass(s + "common.ClientboundKeepAlivePacket");
    public static final Class<?> commonDisconnectPacketClass = ReflectionUtil.getClass(s + "common.ClientboundDisconnectPacket");

    // 1.20.4+
    public static final Class<?> resourcePackPushPacketClass = ReflectionUtil.getClass(s + ".common.ClientboundResourcePackPushPacket");

    // 1.20.2
    public static final Class<?> resourcePackPacketClass = ReflectionUtil.getClass(s + ".common.ClientboundResourcePackPacket");

    private static final Method getKeepAliveIdMethod;
    private static final Method getCommonDisconnectReasonMethod;
    private static final Method getLoginDisconnectReasonMethod;

    public static Method getResourcePackRequiredMethod;

    // 1.20.4+
    public static Method getResourcePackPushUUIDMethod;

    // Login
    public static final Class<?> compressionPacketClass = ReflectionUtil.getClass(s + "login.PacketLoginOutSetCompression");
    public static final Class<?> gameProfilePacketClass = ReflectionUtil.getClass(s + "login.PacketLoginOutSuccess");

    public static final Class<?> loginDisconnectPacketClass = ReflectionUtil.getClass(s + "login.PacketLoginOutDisconnect");

//    public static final Class<?> loginCustomPayloadClass = ReflectionUtil.getClass(s + "login.PacketLoginOutCustomPayload");
    private static final Method getCompressionThresholdMethod;
//    private static Method getLoginCustomPayloadIdMethod;
    // Configuration
    public static final Class<?> selectKnownPacksPacketClass = ReflectionUtil.getClass(s + "configuration.ClientboundSelectKnownPacks");
//    public static Class<?> serverLinkPacketClass = ReflectionUtil.getClass(s + "configuration.ClientboundServerLinksPacket");
    public static final Class<?> configFinishPacketClass = ReflectionUtil.getClass(s + "configuration.ClientboundFinishConfigurationPacket");

    public static Method getSelectKnownPacksMethod;

//    public static Method getServerLinkMethod;

    // Game
    public static final Class<?> loginPacketClass = ReflectionUtil.getClass(s + "game.PacketPlayOutLogin");
    public static final Class<?> deathScreenPacketClass = ReflectionUtil.getClass(s + "game.ClientboundPlayerCombatKillPacket");
    public static final Class<?> gameEventPacketClass = ReflectionUtil.getClass(s + "game.PacketPlayOutGameStateChange");
    public static final Class<?> playerPositionPacketClass = ReflectionUtil.getClass(s + "game.PacketPlayOutPosition");


    private static final Method getPlayerPositionPacketIdMethod;
    public static final Set<Class<?>> packetSet = new HashSet<>() {{
        add(commonKeepAlivePacketClass);
        add(commonDisconnectPacketClass);
        add(deathScreenPacketClass);
        add(gameEventPacketClass);
        add(playerPositionPacketClass);
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
        Objects.requireNonNull(commonKeepAlivePacketClass, "ClientboundKeepAlivePacket.class");
        getKeepAliveIdMethod = Arrays.stream(commonKeepAlivePacketClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == long.class)
                .findFirst()
                .orElseThrow();
        getKeepAliveIdMethod.setAccessible(true);

        Objects.requireNonNull(commonDisconnectPacketClass, "ClientboundDisconnectPacket.class");
        getCommonDisconnectReasonMethod = Arrays.stream(commonDisconnectPacketClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == componentClass && method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        getCommonDisconnectReasonMethod.setAccessible(true);

        Objects.requireNonNull(loginDisconnectPacketClass, "ClientboundLoginDisconnectPacket.class");
        getLoginDisconnectReasonMethod = Arrays.stream(loginDisconnectPacketClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == componentClass && method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        getLoginDisconnectReasonMethod.setAccessible(true);


        // Login
        Objects.requireNonNull(compressionPacketClass, "ClientboundLoginCompressionPacket.class");
        getCompressionThresholdMethod = Arrays.stream(compressionPacketClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == int.class)
                .findFirst()
                .orElseThrow();
        getCompressionThresholdMethod.setAccessible(true);

//        if (loginCustomPayloadClass != null) {
//            Class<?> customPayloadClass = ReflectionUtil.getClass(s + "login.custom.CustomQueryPayload");
//            Objects.requireNonNull(customPayloadClass, "CustomQueryPayload.class");
//            getLoginCustomPayloadIdMethod = Arrays.stream(loginCustomPayloadClass.getDeclaredMethods())
//                    .filter(method -> method.getReturnType() == int.class)
//                    .findFirst()
//                    .orElseThrow();
//            getLoginCustomPayloadIdMethod.setAccessible(true);
//        }

        // Config
        if (selectKnownPacksPacketClass != null) {
            getSelectKnownPacksMethod = Arrays.stream(selectKnownPacksPacketClass.getDeclaredMethods())
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

        Objects.requireNonNull(playerPositionPacketClass, "playerPositionPacket.class");
        getPlayerPositionPacketIdMethod = Arrays.stream(playerPositionPacketClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == int.class)
                .findFirst()
                .orElseThrow();
        getPlayerPositionPacketIdMethod.setAccessible(true);

        if (resourcePackPacketClass != null) {
            getResourcePackRequiredMethod = Arrays.stream(resourcePackPacketClass.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == boolean.class)
                    .findFirst().orElseThrow();
            getResourcePackRequiredMethod.setAccessible(true);
        }

        if (resourcePackPushPacketClass != null) {
            getResourcePackPushUUIDMethod = Arrays.stream(resourcePackPushPacketClass.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == UUID.class)
                    .findFirst()
                    .orElseThrow();
            getResourcePackPushUUIDMethod.setAccessible(true);
            getResourcePackRequiredMethod = Arrays.stream(resourcePackPushPacketClass.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == boolean.class)
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
