package me.autobot.playerdoll.packet.v1_20_R2.login;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CSetCompression extends CPackets {
    protected int compressionThreshold;
    @Override
    public void handle(DataInputStream data) throws IOException {
        compressionThreshold = PacketUtil.readVarInt(data);
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> socketReader.setCompressionThreshold(compressionThreshold);
    }
}
