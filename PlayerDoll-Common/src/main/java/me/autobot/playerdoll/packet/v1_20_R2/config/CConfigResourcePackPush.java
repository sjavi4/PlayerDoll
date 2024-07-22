package me.autobot.playerdoll.packet.v1_20_R2.config;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CConfigResourcePackPush extends CPackets {
    protected boolean forced;
    @Override
    public void handle(DataInputStream data) throws IOException {
        String url = PacketUtil.readString(data);
        String hash = PacketUtil.readString(data);
        forced = data.readBoolean();
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            try {
                socketReader.getOutput().write(new SConfigResourcePackResponse(forced).write());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
