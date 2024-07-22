package me.autobot.playerdoll.packet.v1_20_R2.config;

import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.packet.SPackets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SConfigFinish extends SPackets {

    @Override
    protected int getPacketID() {
        return 0x02;
    }

    @Override
    public byte[] write() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream ack = new DataOutputStream(buffer);

        PacketUtil.writeVarInt(ack, 0x02); // length of compressed size + packet id
        PacketUtil.writeVarInt(ack, 0x00); // no compression
        PacketUtil.writeVarInt(ack, getPacketID());

        return buffer.toByteArray();
    }

}
