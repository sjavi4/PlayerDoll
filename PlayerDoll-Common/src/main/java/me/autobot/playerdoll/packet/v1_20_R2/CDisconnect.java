package me.autobot.playerdoll.packet.v1_20_R2;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CDisconnect extends CPackets {

    @Override
    public void handle(DataInputStream data) throws IOException {

    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            PlayerDoll.LOGGER.info("Client Disconnected " + socketReader.getCurrentState());
            socketReader.endStream();
        };
    }
}
