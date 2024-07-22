package me.autobot.playerdoll.packet.v1_20_R3.play;

import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.IOException;
import java.util.function.Consumer;

public class CPlayKeepAlive extends me.autobot.playerdoll.packet.v1_20_R2.play.CPlayKeepAlive {

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
