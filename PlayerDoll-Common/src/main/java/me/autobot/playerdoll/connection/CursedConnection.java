package me.autobot.playerdoll.connection;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.packet.DollPacketInjector;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CursedConnection {

//    private static final String SERVER_IP = Bukkit.getIp();
//    private static final int SERVER_PORT = Bukkit.getPort();

//    private static final InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(SERVER_IP, SERVER_PORT);

    //private static Field foliaConnectionField;
    private static Field vanillaConnectionListenerField;
    private static final Field connectionListField;
    private static final boolean ISFOLIA = PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA;

    private static final Class<?> C_PACKET_LISTENER = ReflectionUtil.getClass("net.minecraft.network.PacketListener");
    private static final Field connectionPacketListenerField;
    private static final Field connectionChannelField;
//    private static Method connectionSendPacketMethod;

    private static final Class<?> loginListenerClass = ReflectionUtil.getNMSClass("network.LoginListener");
    // ServerboundHelloPacket (mojang) / PacketLoginInStart
//    private static final Constructor<?> helloPacketConstructor;

    private static final Constructor<?> customLoginListenerConstructor;

    static {
        if (ISFOLIA) {
            try {
                Class<?> foliaRegoinizedServerClass = ReflectionUtil.getFoliaRegoinizedServerClass();
                connectionListField = foliaRegoinizedServerClass.getDeclaredField("connections");
                connectionListField.setAccessible(true);
                //foliaConnectionField = foliaRegoinizedServerClass.getDeclaredField("connections");
                //foliaConnectionField.setAccessible(true);
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
//        Class<?> helloPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.PacketLoginInStart");
//        Objects.requireNonNull(helloPacketClass, "helloPacketClass");
//        helloPacketConstructor = Arrays.stream(helloPacketClass.getConstructors())
//                .filter(constructor -> constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0] == String.class && constructor.getParameterTypes()[1] == UUID.class)
//                .findFirst()
//                .orElseThrow();

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

        Class<?> customLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.DollLoginListener");
        customLoginListenerConstructor = customLoginListenerClass.getConstructors()[0];

    }

    @SuppressWarnings("unchecked")
    public static List<Object> getServerConnectionList() {
        Object instance;
        if (ISFOLIA) {
            instance = ReflectionUtil.getFoliaRegoinizedServerInstance();
        } else {
            instance = ReflectionUtil.getField(vanillaConnectionListenerField, ReflectionUtil.getDedicatedServerInstance());
        }
        return (List<Object>) ReflectionUtil.getField(connectionListField, instance);
    }

    public static boolean startCursedConnection(String clientAddress, GameProfile profile, Player caller) {
        //final GameProfile profile = new GameProfile(UUID,name);
        List<Object> serverConnectionList = getServerConnectionList();
        synchronized (serverConnectionList) {
            for (Object connections : serverConnectionList) {
                String addressServer = getChannel(connections).remoteAddress().toString();
                if (clientAddress.equals(addressServer)) {
                    if (!loginListenerClass.isInstance(getPacketListener(connections))) {
                        //System.out.println("Not login listener");
                        return false;
                    }
                    new DollPacketInjector(getChannel(connections));
                    //System.out.println("set Login Listener");
                    // Skin skin here
                    profile.getProperties().clear();
                    DollConfig dollConfig = DollConfig.getTemporaryConfig(profile.getName());
                    profile.getProperties().put("textures", new Property("textures", dollConfig.skinProperty.getValue(), dollConfig.skinSignature.getValue()));

                    setPacketListener(connections, newServerLoginListener(connections, profile, caller));
                    return true;
                }
            }
        }
        return false;
        /*
        int loop = 0;
        while (loop < 10) {

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

         */
    }
    // ServerConnectionListener -> Connection (NetworkManager) -> PacketListener (Field)
    public static void setPacketListener(Object connection, Object listener) {
        ReflectionUtil.setField(connectionPacketListenerField, connection, listener);
    }
    // ServerConnectionListener -> Connection (NetworkManager) -> getPacketListener (Method)
    public static Object getPacketListener(Object connection) {
        return ReflectionUtil.getField(connectionPacketListenerField, connection);
    }


    // ServerConnectionListener -> Connection (NetworkManager) -> Channel (Field)
    public static Channel getChannel(Object connection) {
        return ReflectionUtil.getField(Channel.class, connectionChannelField, connection);
    }

//    public static void connectionSend(Object connection, Object packet) {
//        if (connectionSendPacketMethod == null) {
//            connectionSendPacketMethod = Arrays.stream(connection.getClass().getMethods())
//                    .filter(field -> field.getReturnType() == void.class && field.getParameterCount() == 1 && field.getParameterTypes()[0] == ReflectionUtil.getClass("net.minecraft.network.protocol.Packet"))
//                    .findFirst()
//                    .orElseThrow();
//        }
//        try {
//            connectionSendPacketMethod.invoke(connection, packet);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    private static Object helloPacketNewInstance(String s, UUID uuid) {
//        try {
//            return helloPacketConstructor.newInstance(s,uuid);
//        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static Object newServerLoginListener(Object serverConnection, GameProfile profile, Player caller) {
        return ReflectionUtil.newInstance(customLoginListenerConstructor, ReflectionUtil.getDedicatedServerInstance(), serverConnection, profile, caller);
    }
}