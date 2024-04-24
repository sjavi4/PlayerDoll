package me.autobot.playerdoll.v1_20_R4.Network;

import me.autobot.playerdoll.Dolls.IConnectionManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import me.autobot.playerdoll.network.PlayerConvertInjector;
import me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler.PlayerLoginListener;
import me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler.ServerLoginListener;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import java.util.List;
import java.util.logging.Level;

public class ConnectionManager implements IConnectionManager {
    public static ConnectionManager INSTANCE;
    public final Thread runner;
    static {
        if (BasicConfig.get().convertPlayer.getValue()) {
            PlayerDoll.getPluginLogger().log(Level.INFO, "Start up Player Connection Listener");
            INSTANCE = new ConnectionManager();
            instances.add(INSTANCE);
        }
    }

    private ConnectionManager() {
        this.runner = new Thread( () -> {
            while (!Thread.currentThread().isInterrupted()) {
                List<Connection> connectionList = CursedConnections.getServerConnectionList();
                synchronized (connectionList) {
                    for (Connection connections : connectionList) {
                        if (connections.channel == null) {
                            continue;
                        }
                        PacketListener listener = connections.getPacketListener();
                        if (listener != null && listener.protocol() == ConnectionProtocol.LOGIN) {
                            if (connections.isConnected()) {
                                if (connections.channel.pipeline().get("player_convert_injector") == null) {
                                    PlayerDoll.getPluginLogger().log(Level.INFO, "Capture Player Connection");
                                    new PlayerConvertInjector(connections.channel, (msg) -> {
                                        if (msg instanceof ServerboundLoginAcknowledgedPacket) {
                                            PlayerDoll.getPluginLogger().log(Level.INFO, "Found Acknowledged Packet for Player");
                                            if (connections.getPacketListener() instanceof ServerLoginListener) {
                                                // Doll login
                                                connections.channel.pipeline().remove("player_convert_injector");
                                                return;
                                            }
                                            ServerLoginPacketListenerImpl oldListener = (ServerLoginPacketListenerImpl) connections.getPacketListener();
                                            ServerPlayer player = PlayerLoginListener.getPlayer(oldListener);
                                            CursedConnections.setPacketListener(connections, new PlayerLoginListener(CursedConnections.server,connections, player, oldListener.isTransferred()));
                                            //connections.setupInboundProtocol(LoginProtocols.SERVERBOUND, new PlayerLoginListener(CursedConnections.server,connections, player, oldListener.isTransferred()));
                                            connections.channel.pipeline().remove("player_convert_injector");
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        });
        runner.start();
    }

    @Override
    public Thread getThread() {
        return this.runner;
    }
}
