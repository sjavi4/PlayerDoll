package me.autobot.playerdoll.packet.v1_20_R2.config;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CConfigFinish extends CPackets {
    @Override
    public void handle(DataInputStream data) throws IOException {

    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            try {
                socketReader.getOutput().write(new SConfigFinish().write());
                socketReader.nextState();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
