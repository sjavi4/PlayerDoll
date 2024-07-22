package me.autobot.playerdoll.packet.v1_20_R2.play;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CGameEvent extends CPackets {

    protected byte event;
    @Override
    public void handle(DataInputStream data) throws IOException {
        event = data.readByte();
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            if (event == 0x04) {
                try {
                    socketReader.getOutput().write(new SRequestRespawn().write());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
