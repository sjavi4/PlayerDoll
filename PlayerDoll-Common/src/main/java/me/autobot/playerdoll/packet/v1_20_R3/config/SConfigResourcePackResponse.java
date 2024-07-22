package me.autobot.playerdoll.packet.v1_20_R3.config;

import me.autobot.playerdoll.packet.PacketUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SConfigResourcePackResponse extends me.autobot.playerdoll.packet.v1_20_R2.config.SConfigResourcePackResponse {

    protected final UUID uuid;
    public SConfigResourcePackResponse(UUID uuid, boolean forced) {
        super(forced);
        this.uuid = uuid;
    }

    @Override
    public byte[] write() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream respond = new DataOutputStream(buffer);

        PacketUtil.writeVarInt(respond, 0x13); // length of compressed size + packet id + packet data
        PacketUtil.writeVarInt(respond, 0x00); // no compression
        PacketUtil.writeVarInt(respond, getPacketID());// state == SocketReader.ConnectionState.CONFIGURATION ? 0x06 : 0x28); // id
        // UUID
        respond.writeLong(uuid.getMostSignificantBits());
        respond.writeLong(uuid.getLeastSignificantBits());
        // reply
        PacketUtil.writeVarInt(respond, forced ? 0x00 : 0x01);


        return buffer.toByteArray();
    }
}
