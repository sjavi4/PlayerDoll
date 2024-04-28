package me.autobot.playerdoll.v1_20_R4.Network;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.folia.FoliaHelper;
import me.autobot.playerdoll.network.DollPacketInjector;
import me.autobot.playerdoll.v1_20_R4.Network.ClientPacketHandler.ClientHandshakeListener;
import me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler.ServerLoginListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CursedConnections {
    public static final MinecraftServer server = MinecraftServer.getServer();
    private static final String serverIP = server.getLocalIp();
    private static final int serverPort = server.getPort();
    private static final InetSocketAddress serverAddress = new InetSocketAddress(serverIP,serverPort);

    private static Field connectionFieldFolia;
    private static Field connectionPacketListenerField;

    static {
        if (PlayerDoll.isFolia) {
            try {
                Class<?> clazz = FoliaHelper.FOLIA_REGIONIZED_SERVER;
                connectionFieldFolia = clazz.getDeclaredField("connections");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        for (Field field : Connection.class.getDeclaredFields()) {
            if (field.getType() == PacketListener.class) {
                connectionPacketListenerField = field;
            }
        }
    }

    public static void doConnection(Player caller, String name, UUID uuid) {
        CursedClientConnection clientConnection = CursedClientConnection.connectToServer(serverAddress);
        CompletableFuture<Void> afterConnected = CompletableFuture.supplyAsync(() -> {
            PlayerDoll.getPluginLogger().log(Level.INFO,"Client Connected");
            String spoofedIP = serverIP;
            if (PlayerDoll.useBungeeCord) {
                spoofedIP = serverIP + '\u0000' + serverIP + '\u0000' + uuid.toString().replaceAll("-","");
            }
            //clientConnection.initiateServerboundPlayConnection(spoofedIP, serverPort, LoginProtocols.SERVERBOUND, LoginProtocols.CLIENTBOUND, new ClientHandshakeListener(clientConnection), false);
            clientConnection.initiateServerboundPlayConnection(spoofedIP, serverPort, new ClientHandshakeListener(clientConnection));

            return null;
        });
        afterConnected.thenRunAsync(() -> {
            startCursedConnection(clientConnection, name, uuid, caller);
        });
    }

    @SuppressWarnings("unchecked")
    public static List<Connection> getServerConnectionList() {
        if (PlayerDoll.isFolia) {
            if (connectionFieldFolia == null) {
                return null;
            } else {
                try {
                    connectionFieldFolia.setAccessible(true);
                    return (List<Connection>) connectionFieldFolia.get(FoliaHelper.REGOINIZED_SERVER);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return server.getConnection().getConnections();
        }
    }

    public static void startCursedConnection(Connection clientConnection, String name, UUID UUID, Player caller) {
        final GameProfile profile = new GameProfile(UUID,name);
        int loop = 0;
        while (loop < 10) {
            List<Connection> serverConnectionList = getServerConnectionList();
            synchronized (serverConnectionList) {
                loop++;
                for (Connection connections : serverConnectionList) {
                    String addressClient = clientConnection.channel.localAddress().toString();
                    String addressServer = connections.channel.remoteAddress().toString();

                    if (addressClient.equals(addressServer)) {
                        loop = 10;
                        CompletableFuture<Connection> waitForLoginListener = CompletableFuture.supplyAsync(() -> {
                            while (!(connections.getPacketListener() instanceof ServerLoginPacketListenerImpl)) {
                                PlayerDoll.getPluginLogger().log(Level.INFO, "Wait For Listener");
                                try {
                                    Thread.sleep(200L);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return connections;
                        });
                        waitForLoginListener.thenAccept((serverConnection) -> {
                            PlayerDoll.getPluginLogger().log(Level.INFO, "Found Connection");
                            DollPacketInjector injector = new DollPacketInjector(connections.channel);
                            injector.sendPacketTask = (msg) -> {
                                if (injector.allowPacketSend && msg instanceof ClientboundLoginPacket) {
                                    injector.allowPacketSend = false;
                                    PlayerDoll.getPluginLogger().log(Level.INFO, "Doll Joined Successfully, Suspend Server-side Packet Sending.");
                                } else if (!injector.allowPacketSend) {
                                    return msg instanceof ClientboundKeepAlivePacket || msg instanceof ClientboundDisconnectPacket;
                                }
                                return injector.allowPacketSend;
                            };
                            //new DollPacketInjector(connections);
                            setPacketListener(connections, new ServerLoginListener(server, connections, profile, caller));
                            //connections.setupInboundProtocol(LoginProtocols.SERVERBOUND, new ServerLoginListener(server, connections, profile, caller));
                            clientConnection.send(new ServerboundHelloPacket(name, UUID));
                            //connections.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
                        });
                        break;
                    }
                }
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void setPacketListener(Connection connection, PacketListener listener) {
        connectionPacketListenerField.setAccessible(true);
        try {
            connectionPacketListenerField.set(connection, listener);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
