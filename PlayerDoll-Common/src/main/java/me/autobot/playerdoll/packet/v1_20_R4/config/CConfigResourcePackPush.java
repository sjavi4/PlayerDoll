package me.autobot.playerdoll.packet.v1_20_R4.config;

import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.IOException;
import java.util.function.Consumer;

public class CConfigResourcePackPush extends me.autobot.playerdoll.packet.v1_20_R3.config.CConfigResourcePackPush {

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
