package me.autobot.playerdoll.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.protocol.Packet;

import static me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols.getCodec;
import static me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols.getEncode;

public abstract class Post_1_20_R4_AbstractPacketEncoder extends MessageToByteEncoder<Packet<?>> {

    protected abstract Object getProtocol();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) {
        getEncode(getCodec(getProtocol()), byteBuf, packet);
    }
}
