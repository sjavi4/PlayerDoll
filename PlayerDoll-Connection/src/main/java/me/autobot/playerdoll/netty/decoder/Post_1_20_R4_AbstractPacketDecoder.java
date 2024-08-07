package me.autobot.playerdoll.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.protocol.Packet;

import java.io.IOException;
import java.util.List;

import static me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols.getCodec;
import static me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols.getDecode;

public abstract class Post_1_20_R4_AbstractPacketDecoder extends ByteToMessageDecoder {

    protected abstract Object getProtocol();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int i = byteBuf.readableBytes();
        if (i != 0) {
            Packet<?> packet = (Packet<?>) getDecode(getCodec(getProtocol()), byteBuf);
            if (byteBuf.readableBytes() > 0) {
                throw new IOException("Packet is larger than expected");
            } else {
                list.add(packet);
            }
        }
    }
}
