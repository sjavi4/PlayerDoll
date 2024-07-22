package me.autobot.playerdoll.packet.v1_20_R2.play;

import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.packet.SPackets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SAcceptTeleport extends SPackets {

    protected final int tpId;
    public SAcceptTeleport(int tpId) {
        this.tpId = tpId;
    }
    @Override
    protected int getPacketID() {
        return 0x00;
    }

    @Override
    public byte[] write() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream respond = new DataOutputStream(buffer);

        PacketUtil.writeVarInt(respond, 0x03); // length of compressed size + packet id + packet data
        PacketUtil.writeVarInt(respond, 0x00); // no compression
        PacketUtil.writeVarInt(respond, getPacketID()); // id
        // id
        PacketUtil.writeVarInt(respond, tpId); // tp id

        return buffer.toByteArray();
    }
}
