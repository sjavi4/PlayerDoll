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
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class CursedConnections {
    public static final MinecraftServer server = MinecraftServer.getServer();
    private static final String serverIP = server.getLocalIp();
    private static final int serverPort = server.getPort();
    private static final InetSocketAddress serverAddress = new InetSocketAddress(serverIP,serverPort);
    public static void doConnection(Player caller, String name, UUID uuid) {
        CursedClientConnection clientConnection = CursedClientConnection.connectToServer(serverAddress);

        clientConnection.runOnceConnected((c) -> {
            c.setListener(new ClientHandshakeListener(clientConnection));
            PlayerDoll.getPluginLogger().log(Level.INFO,"Client Connected");
                ClientIntentionPacket intentionPacket = new ClientIntentionPacket(SharedConstants.getProtocolVersion(), serverIP, serverPort, ClientIntent.LOGIN);
                c.send(intentionPacket);
        });

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            startCursedConnection(clientConnection,name,uuid,caller);
        });
        thread.start();
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

        List<Connection> serverConnectionList = getServerConnectionList();
        synchronized (serverConnectionList) {
            for (Connection connections : serverConnectionList) {
                String addressClient = clientConnection.channel.localAddress().toString();
                String addressServer = connections.channel.remoteAddress().toString();

                if (addressClient.equals(addressServer)) {
                    PlayerDoll.getPluginLogger().log(Level.INFO, "Found Connection");
                    int index = serverConnectionList.lastIndexOf(connections);
                    Connection con = serverConnectionList.get(index);
                    con.setListener(new ServerLoginListener(server,con,profile,caller));
                    while (con.getPacketListener() == null || !(con.getPacketListener() instanceof ServerLoginListener)) {
                        PlayerDoll.getPluginLogger().log(Level.INFO,"Waiting For Server Login Listener");
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    clientConnection.send(new ServerboundHelloPacket(name,UUID));
                }
            }
        }
    }
}
