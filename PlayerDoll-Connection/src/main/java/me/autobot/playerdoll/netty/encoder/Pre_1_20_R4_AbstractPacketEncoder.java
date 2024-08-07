package me.autobot.playerdoll.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;
import me.autobot.playerdoll.util.PacketUtil;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

import java.io.IOException;

import static me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols.getPacketId;

public abstract class Pre_1_20_R4_AbstractPacketEncoder extends MessageToByteEncoder<Packet<?>> {

    protected abstract Object getProtocol();
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf byteBuf) throws Exception {
        Attribute<?> attr = ctx.channel().attr(Pre1_20_R4_Protocols.ATTRIBUTE_SERVERBOUND_PROTOCOL);
        Object codec = attr.get();
        if (codec == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        } else {
            int packetId = getPacketId(codec, packet);
            if (packetId == -1) {
                throw new IOException("Can't serialize unregistered packet");
            } else {
                Object friendlyBuf = Pre1_20_R4_Protocols.createFriendlyByteBuf(byteBuf);
                PacketUtil.writeVarInt((ByteBuf) friendlyBuf, packetId);

                int writerIndex = ((ByteBuf)friendlyBuf).writerIndex();
                packet.a((PacketDataSerializer) friendlyBuf);
                int index = ((ByteBuf)friendlyBuf).writerIndex() - writerIndex;
                if (index > 8388608) {
                    throw new IllegalArgumentException("Packet too big (is " + index + ", should be less than 8388608): " + packet);
                }
            }
        }

    }
}
