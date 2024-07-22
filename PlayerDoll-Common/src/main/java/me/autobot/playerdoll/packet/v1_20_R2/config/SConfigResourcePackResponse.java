package me.autobot.playerdoll.packet.v1_20_R2.config;

import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.packet.SPackets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SConfigResourcePackResponse extends SPackets {
    protected boolean forced;
    public SConfigResourcePackResponse(boolean forced) {
        this.forced = forced;
    }


    @Override
    protected int getPacketID() {
        return 0x05;
    }

    @Override
    public byte[] write() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream respond = new DataOutputStream(buffer);

        PacketUtil.writeVarInt(respond, 0x03); // length of compressed size + packet id + packet data
        PacketUtil.writeVarInt(respond, 0x00); // no compression
        PacketUtil.writeVarInt(respond, getPacketID());// state == SocketReader.ConnectionState.CONFIGURATION ? 0x06 : 0x28); // id
        // reply
        PacketUtil.writeVarInt(respond, forced ? 0x00 : 0x01);


        return buffer.toByteArray();
    }
}
