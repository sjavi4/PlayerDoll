package me.autobot.playerdoll.api.connection;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.resolver.Connections;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class ConnectionFetcher {

    @SuppressWarnings("unchecked")
    public static List<Object> getServerConnectionList() {
        Object instance;
        if (Connections.FOLIA) {
            instance = ReflectionUtil.getFoliaRegoinizedServerInstance();
        } else {
            instance = ReflectionUtil.getField(Connections.vanillaConnectionListenerField, ReflectionUtil.getDedicatedServerInstance());
        }
        return (List<Object>) ReflectionUtil.getField(Connections.connectionListField, instance);
    }

    public static boolean startCursedConnection(String clientAddress, GameProfile profile, Player caller) {
        List<Object> serverConnectionList = getServerConnectionList();
        synchronized (serverConnectionList) {
            for (Object connections : serverConnectionList) {
                Channel channel = getChannel(connections);
                if (channel == null) {
                    continue;
                }
                String addressServer = channel.remoteAddress().toString();
                boolean checkAddress = clientAddress.equals(addressServer);
                if (checkAddress) {
                    if (!Connections.NMSLoginListenerClass.isInstance(getPacketListener(connections))) {
                        return false;
                    }
                    new DollPacketInjector(channel);
                    setPacketListener(connections, newDollCustomServerLoginListener(connections, profile, caller));
                    return true;
                }
            }
        }
        return false;
    }
    // ServerConnectionListener -> Connection (NetworkManager) -> PacketListener (Field)
    public static void setPacketListener(Object connection, Object listener) {
        ReflectionUtil.setField(Connections.connectionPacketListenerField, connection, listener);
    }
    // ServerConnectionListener -> Connection (NetworkManager) -> getPacketListener (Method)
    public static Object getPacketListener(Object connection) {
        return ReflectionUtil.getField(Connections.connectionPacketListenerField, connection);
    }


    // ServerConnectionListener -> Connection (NetworkManager) -> Channel (Field)
    public static Channel getChannel(Object connection) {
        return (Channel) ReflectionUtil.getField(Connections.connectionChannelField, connection);
    }


    public static void setDollCustomLoginListener(Constructor<?> constructor) {
        Objects.requireNonNull(constructor, "Class Constructor cannot be Null.");
        if (constructor.getParameterCount() != 4) {
            throw new IllegalArgumentException("Arguments Must Contain {MinecraftServer.class, Connection.class, GameProfile.class, Player.class}");
        }
        Class<?>[] params = constructor.getParameterTypes();
        if (params[0] == ReflectionUtil.getDedicatedServerClass().getSuperclass() && params[2] == GameProfile.class && params[3] == Player.class) {
            Connections.dollCustomLoginListenerConstructor = constructor;
            Connections.dollCustomLoginListenerClass = constructor.getDeclaringClass();
        } else {
            throw new IllegalArgumentException("Wrong Argument Types {MinecraftServer.class, Connection.class, GameProfile.class, Player.class}");
        }
    }

    private static Object newDollCustomServerLoginListener(Object serverConnection, GameProfile profile, Player caller) {
        Objects.requireNonNull(Connections.dollCustomLoginListenerConstructor, "Doll Login Listener not Set.");
        return ReflectionUtil.newInstance(Connections.dollCustomLoginListenerConstructor, ReflectionUtil.getDedicatedServerInstance(), serverConnection, profile, caller);
    }
}