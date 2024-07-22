package me.autobot.playerdoll.packet.v1_21_R1;

import me.autobot.playerdoll.socket.io.SocketReader;

public class Factory extends me.autobot.playerdoll.packet.v1_20_R4.Factory {

    public Factory(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 767;
    }
}
