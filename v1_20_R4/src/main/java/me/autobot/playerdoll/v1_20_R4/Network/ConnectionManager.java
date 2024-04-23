package me.autobot.playerdoll.v1_20_R4.Network;

import me.autobot.playerdoll.Dolls.IConnectionManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntent;

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
                        if (connections.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).get() == ClientIntent.LOGIN.protocol().codec(PacketFlow.CLIENTBOUND)) {
                            if (connections.isConnected()) {
                                if (connections.channel.pipeline().get("doll_player_injector") == null) {
                                    PlayerDoll.getPluginLogger().log(Level.INFO, "Capture Player Connection");
                                    new DollPlayerInjector(connections);
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
