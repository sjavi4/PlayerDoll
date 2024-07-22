package me.autobot.playerdoll.packet.v1_20_R2.play;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CPlayKeepAlive extends CPackets {
    protected long keepAliveId;
    @Override
    public void handle(DataInputStream data) throws IOException {
        keepAliveId = data.readLong();
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            try {
                socketReader.getOutput().write(new SPlayKeepAlive(keepAliveId).write());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
