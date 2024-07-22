package me.autobot.playerdoll.packet.v1_20_R2.config;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CConfigKeepAlive extends CPackets {
    protected long keepAliveId;
    @Override
    public void handle(DataInputStream data) throws IOException {
        keepAliveId = data.readLong();
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            try {
                socketReader.getOutput().write(new SConfigKeepAlive(keepAliveId).write());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
