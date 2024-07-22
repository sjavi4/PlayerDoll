package me.autobot.playerdoll.packet.v1_20_R4.play;

import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.IOException;
import java.util.function.Consumer;

public class CGameEvent extends me.autobot.playerdoll.packet.v1_20_R2.play.CGameEvent {
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
