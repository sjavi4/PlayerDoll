package me.autobot.playerdoll.packet.v1_20_R3.config;

import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class CConfigResourcePackPush extends me.autobot.playerdoll.packet.v1_20_R2.config.CConfigResourcePackPush {

    protected UUID uuid;

    @Override
    public void handle(DataInputStream data) throws IOException {
        uuid = PacketUtil.readUUID(data);
        super.handle(data);
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            try {
                socketReader.getOutput().write(new SConfigResourcePackResponse(uuid, forced).write());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
