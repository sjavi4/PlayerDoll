package me.autobot.playerdoll;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.netty.channel.Channel;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.netty.handler.HandshakeHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PluginMessenger implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
        if (!channel.equals("playerdoll:doll")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(msg);
        int id = input.readInt();
        switch (id) {
            // Start Find Login Listener
            case 0 -> {
                PlayerDoll.LOGGER.info("Start capture Login Listener");
                UUID dollUUID = UUID.fromString(input.readUTF());
                Channel dollChannel = DollConnection.DOLL_CONNECTIONS.get(dollUUID);
                HandshakeHandler handler = dollChannel.pipeline().get(HandshakeHandler.class);
                // Awake the thread to continue Login
                if (handler != null) {
                    synchronized (handler) {
                        PlayerDoll.LOGGER.info("Notify Wait Thread");
                        handler.notify();
                    }
                }


            }
            // Set last join server
            case 1 -> {
                PlayerDoll.LOGGER.info("Plugin set last Join Server");
                UUID dollUUID = UUID.fromString(input.readUTF());
                String serverName = input.readUTF();
                DollManager.DOLL_BUNGEE_SERVERS.put(dollUUID, serverName);
            }
            // Finish create DollData Start Handshake
            case 2 -> {
                PlayerDoll.LOGGER.info("Plugin Start Handshake");
                UUID dollUUID = UUID.fromString(input.readUTF());
                Channel dollChannel = DollConnection.DOLL_CONNECTIONS.get(dollUUID);
                HandshakeHandler handler = dollChannel.pipeline().get(HandshakeHandler.class);
                // Awake the thread to continue Login
                if (handler != null) {
                    synchronized (handler) {
                        handler.notify();
                    }
                }
            }
        }
    }
}
