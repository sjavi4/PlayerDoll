package me.autobot.playerdoll.v1_20_R3.Network;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.folia.FoliaHelper;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.network.DollPacketInjector;
import me.autobot.playerdoll.v1_20_R3.Network.ClientPacketHandler.ClientHandshakeListener;
import me.autobot.playerdoll.v1_20_R3.Network.ServerPacketHandler.ServerLoginListener;
import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
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
    //private static final String serverIP = BasicConfig.get().proxyIP.getValue().equalsIgnoreCase("localhost") ? server.getLocalIp() : BasicConfig.get().proxyIP.getValue();
    //private static final int serverPort = BasicConfig.get().proxyPort.getValue() == -1 ? server.getPort() : BasicConfig.get().proxyPort.getValue();
    private static final String serverIP = server.getLocalIp();
    private static final int serverPort = server.getPort();
    private static final InetSocketAddress serverAddress = new InetSocketAddress(serverIP,serverPort);

    private static Field connectionFieldFolia;

    static {
        if (PlayerDoll.isFolia) {
            try {
                Class<?> clazz = FoliaHelper.FOLIA_REGIONIZED_SERVER;
                connectionFieldFolia = clazz.getDeclaredField("connections");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void doConnection(Player caller, String name, UUID uuid) {
        CursedClientConnection clientConnection = CursedClientConnection.connectToServer(serverAddress);
        CompletableFuture<Void> afterConnected = CompletableFuture.supplyAsync(() -> {
            clientConnection.runOnceConnected((c) -> {
                c.setListener(new ClientHandshakeListener(clientConnection));
                PlayerDoll.getPluginLogger().log(Level.INFO,"Client Connected");
                String spoofedIP = serverIP;
                if (PlayerDoll.useBungeeCord) {
                    spoofedIP = serverIP + '\u0000' + serverIP + '\u0000' + uuid.toString().replaceAll("-","");
                }
                ClientIntentionPacket intentionPacket = new ClientIntentionPacket(SharedConstants.getProtocolVersion(), spoofedIP, serverPort, ClientIntent.LOGIN);
                c.send(intentionPacket);
            });
            return null;
        });
        afterConnected.thenRunAsync(() -> {
            /*
            if (PlayerDoll.useBungeeCord) {
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeInt(1);
                output.writeUTF(clientConnection.channel.localAddress().toString()); // Client Address
                output.writeUTF(uuid.toString()); // UUID
                output.writeUTF(name); // Name

                Runnable task = () -> Bukkit.getServer().sendPluginMessage(PlayerDoll.getPlugin(),"playerdoll:doll", output.toByteArray());
                if (PlayerDoll.isFolia) {
                    PlayerDoll.getFoliaHelper().globalTaskDelayed(task, 40);
                } else {
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),task,40);
                }
                PlayerDoll.getPluginLogger().log(Level.INFO, "BungeeCord Connection");

                //new DollPacketInjector(clientConnection);
                //con.setListener(new ServerLoginListener(server, con, profile, caller));

                //clientConnection.send(new ServerboundHelloPacket(name, uuid));
                //startCursedBungeeCordConnection(name, uuid, caller);
                //pendingConnections.put(uuid, new Pair<>(caller,name));
                startCursedConnection(clientConnection, name, uuid, caller);
            } else {
                startCursedConnection(clientConnection, name, uuid, caller);
            }

             */
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
                            me.autobot.playerdoll.network.DollPacketInjector injector = new DollPacketInjector(connections.channel);
                            injector.sendPacketTask = (msg) -> {
                                if (injector.allowPacketSend && msg instanceof ClientboundLoginPacket) {
                                    injector.allowPacketSend = false;
                                    PlayerDoll.getPluginLogger().log(Level.INFO, "Doll Joined Successfully, Suspend Server-side Packet Sending.");
                                } else if (!injector.allowPacketSend) {
                                    return msg instanceof ClientboundKeepAlivePacket || msg instanceof ClientboundDisconnectPacket;
                                }
                                return injector.allowPacketSend;
                            };
                            connections.setListener(new ServerLoginListener(server, connections, profile, caller));
                            clientConnection.send(new ServerboundHelloPacket(name, UUID));
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
/*
    public static void startCursedBungeeCordConnection(String name, UUID UUID, Player caller) {
        final GameProfile profile = new GameProfile(UUID,name);
        boolean get = false;
        PlayerDoll.getPluginLogger().log(Level.INFO, "Finding Bungee Connection");
        while (!get) {
            List<Connection> serverConnectionList = getServerConnectionList();
            synchronized (serverConnectionList) {
                for (Connection connections : serverConnectionList) {
                    boolean passConnection;
                    passConnection = connections.spoofedUUID.equals(UUID);

                    if (passConnection) {
                        PlayerDoll.getPluginLogger().log(Level.INFO, "Bungee Connection Found");
                        new DollPacketInjector(connections);
                        ServerLoginListener listener = new ServerLoginListener(server, connections, profile, caller);
                        connections.setListener(listener);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        listener.handleHello(new ServerboundHelloPacket(name,UUID));
                        get = true;
                        break;
                    }
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //clientConnection.disconnect(Component.literal("Timed out"));
    }

 */
}
