package me.autobot.playerdoll.packet.v1_20_R2.play;

import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.packet.SPackets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SPlayKeepAlive extends SPackets {

    protected final long keepAliveId;
    public SPlayKeepAlive(long keepAliveId) {
        this.keepAliveId = keepAliveId;
    }
    @Override
    protected int getPacketID() {
        return 0x14;
    }

    @Override
    public byte[] write() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream respond = new DataOutputStream(buffer);

        PacketUtil.writeVarInt(respond, 0x0A); // length of compressed size + packet id + packet data
        PacketUtil.writeVarInt(respond, 0x00); // no compression
        PacketUtil.writeVarInt(respond, getPacketID());
        // id
        respond.writeLong(keepAliveId);

        return buffer.toByteArray();
    }
}
