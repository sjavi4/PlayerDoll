package me.autobot.playerdoll.packet.v1_20_R2.play;

import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CSyncPosition extends CPackets {

    protected int tpId;

    @Override
    public void handle(DataInputStream data) throws IOException {
        double x = data.readDouble();
        double y = data.readDouble();
        double z = data.readDouble();
        float yaw = data.readFloat();
        float pitch = data.readFloat();
        byte flag = data.readByte();
        tpId = PacketUtil.readVarInt(data);
    }

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            if (socketReader.lastAcceptTpId != tpId) {
                socketReader.lastAcceptTpId = tpId;
                try {
                    socketReader.getOutput().write(new SAcceptTeleport(tpId).write());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
