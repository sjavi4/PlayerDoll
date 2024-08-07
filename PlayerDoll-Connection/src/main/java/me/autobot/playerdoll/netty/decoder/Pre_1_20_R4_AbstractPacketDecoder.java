package me.autobot.playerdoll.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;
import me.autobot.playerdoll.util.PacketUtil;
import net.minecraft.network.protocol.Packet;

import java.io.IOException;
import java.util.List;

import static me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols.createFriendlyByteBuf;
import static me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols.createPacket;

public abstract class Pre_1_20_R4_AbstractPacketDecoder extends ByteToMessageDecoder {

    protected abstract Object getProtocol();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int var3 = byteBuf.readableBytes();
        if (var3 != 0) {
            Attribute<?> var4 = ctx.channel().attr(Pre1_20_R4_Protocols.ATTRIBUTE_CLIENTBOUND_PROTOCOL);
            Object var5 = var4.get();
            Object friendlyBuf = createFriendlyByteBuf(byteBuf);
            int var7 = PacketUtil.readVarInt((ByteBuf) friendlyBuf);
            Packet<?> var8 = createPacket(var5, var7, friendlyBuf);
            if (var8 == null) {
                throw new IOException("Bad packet id " + var7);
            } else {
                if (((ByteBuf)friendlyBuf).readableBytes() > 0) {
                    throw new IOException("Packet " + var7 + " (" + var8.getClass().getSimpleName() + ") was larger than I expected, found " + ((ByteBuf)friendlyBuf).readableBytes() + " bytes extra whilst reading packet " + var7);
                } else {
                    list.add(var8);
                }
            }
        }
    }
}
