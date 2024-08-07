package me.autobot.playerdoll.netty;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConnectionFetcher {
    private static Field vanillaConnectionListenerField;
    private static Field connectionListField;
    public static Plugin plugin;
    private static boolean isFolia;
    public static boolean isBungeeCord;

    private static final Class<?> C_PACKET_LISTENER = ReflectionUtil.getClass("net.minecraft.network.PacketListener");
    private static Field connectionPacketListenerField;
    private static Field connectionChannelField;
//    private static Method connectionSendPacketMethod;

    private static final Class<?> loginListenerClass = ReflectionUtil.getNMSClass("network.LoginListener");
    // ServerboundHelloPacket (mojang) / PacketLoginInStart
//    private static final Constructor<?> helloPacketConstructor;

    private static Constructor<?> customLoginListenerConstructor;

    public static void init(Plugin p, boolean folia, boolean bungeeCord) {
        plugin = p;
        isFolia = folia;
        isBungeeCord = bungeeCord;

        if (isFolia) {
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

        Class<?> customLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.DollLoginListener");
        customLoginListenerConstructor = customLoginListenerClass.getConstructors()[0];
    }


    @SuppressWarnings("unchecked")
    public static List<Object> getServerConnectionList() {
        Object instance;
        if (isFolia) {
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
            //PlayerDoll.LOGGER.info("Connection Size: " + serverConnectionList.size());
            for (Object connections : serverConnectionList) {
                Channel channel = getChannel(connections);
                if (channel == null) {
                    continue;
                }
                String addressServer = channel.remoteAddress().toString();
                boolean checkAddress;
                if (isBungeeCord) {
                    int clientPort = Integer.parseInt(clientAddress.split(":")[1]);
                    int checkingPort = Integer.parseInt(addressServer.split(":")[1]);
                    checkAddress = clientPort < checkingPort && checkingPort - clientPort <= 5;
                } else {
                    checkAddress = clientAddress.equals(addressServer);
                }
                if (checkAddress) {
                    if (!loginListenerClass.isInstance(getPacketListener(connections))) {
                        //System.out.println("Not login listener");
                        return false;
                    }
                    new DollPacketInjector(channel);
                    setPacketListener(connections, newServerLoginListener(connections, profile, caller));
                    return true;
                }
            }
        }
        return false;
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
        return (Channel) ReflectionUtil.getField(connectionChannelField, connection);
    }


    private static Object newServerLoginListener(Object serverConnection, GameProfile profile, Player caller) {
        return ReflectionUtil.newInstance(customLoginListenerConstructor, ReflectionUtil.getDedicatedServerInstance(), serverConnection, profile, caller);
    }
}