package me.autobot.playerdoll;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.socket.ClientSocket;
import me.autobot.playerdoll.socket.SocketHelper;
import me.autobot.playerdoll.socket.io.SocketReader;
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
                ClientSocket socket = SocketHelper.DOLL_CLIENTS.get(dollUUID);
                // Awake the thread to continue Login
                SocketReader socketReader = socket.getSocketReader();
                synchronized (socketReader) {
                    PlayerDoll.LOGGER.info("Notify Wait Thread");
                    socketReader.notify();
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
                SocketReader reader = SocketHelper.DOLL_CLIENTS.get(dollUUID).getSocketReader();
                synchronized (reader) {
                    reader.notify();
                }
            }
        }
    }
}
