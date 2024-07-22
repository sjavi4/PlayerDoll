package me.autobot.playerdoll.packet;

import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public abstract class CPackets {

    public abstract void handle(DataInputStream data) throws IOException;
    public abstract Consumer<SocketReader> reply();

}
