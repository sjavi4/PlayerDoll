package me.autobot.playerdoll.v1_20_R4.Network;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Folia.FoliaHelper;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Network.ClientPacketHandler.ClientHandshakeListener;
import me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler.ServerLoginListener;
import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
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
    public static void doConnection(Player caller, String name, UUID uuid) {
        CursedClientConnection clientConnection = CursedClientConnection.connectToServer(serverAddress);

        CompletableFuture<Void> afterConnected = CompletableFuture.supplyAsync(() -> {
            clientConnection.runOnceConnected((c) -> {
                c.setListener(new ClientHandshakeListener(clientConnection));
                PlayerDoll.getPluginLogger().log(Level.INFO,"Client Connected");
                ClientIntentionPacket intentionPacket = new ClientIntentionPacket(SharedConstants.getProtocolVersion(), serverIP, serverPort, ClientIntent.LOGIN);
                c.send(intentionPacket);
            });
            return null;
        });
        afterConnected.thenRunAsync(() -> startCursedConnection(clientConnection, name, uuid, caller));
        /*
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            startCursedConnection(clientConnection,name,uuid,caller);
        });
        thread.start();

         */
    }

    @SuppressWarnings("unchecked")
    private static List<Connection> getServerConnectionList() {
        if (PlayerDoll.isFolia) {
            try {
                Class<?> clazz = FoliaHelper.FOLIA_REGIONIZED_SERVER;
                Object serverInstance = FoliaHelper.REGOINIZED_SERVER;
                Field connections = clazz.getDeclaredField("connections");
                connections.setAccessible(true);
                return (List<Connection>) connections.get(serverInstance);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return server.getConnection().getConnections();
        }
    }
    private static void startCursedConnection(Connection clientConnection, String name, UUID UUID, Player caller) {
        final GameProfile profile = new GameProfile(UUID,name);
        boolean loop = true;
        while (loop) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Connection> serverConnectionList = getServerConnectionList();
            synchronized (serverConnectionList) {

                for (Connection connections : serverConnectionList) {
                    String addressClient = clientConnection.channel.localAddress().toString();
                    String addressServer = connections.channel.remoteAddress().toString();

                    if (addressClient.equals(addressServer)) {
                        loop = false;
                        int index = serverConnectionList.lastIndexOf(connections);
                        Connection con = serverConnectionList.get(index);
                        CompletableFuture<Connection> waitForLoginListener = CompletableFuture.supplyAsync(() -> {
                            while (!(con.getPacketListener() instanceof ServerLoginPacketListenerImpl)) {
                                PlayerDoll.getPluginLogger().log(Level.INFO, "Wait For Listener");
                                try {
                                    Thread.sleep(200L);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return con;
                        });
                        waitForLoginListener.thenAccept((serverConnection) -> {
                            PlayerDoll.getPluginLogger().log(Level.INFO, "Found Connection");
                            new DollPacketInjector(con);
                            con.setListener(new ServerLoginListener(server, con, profile, caller));
                            clientConnection.send(new ServerboundHelloPacket(name, UUID));
                        });
                        break;
                    }
                }
            }
        }
    }
}
