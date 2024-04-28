package me.autobot.playerdoll.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class RawPacketReader extends ChannelDuplexHandler {
    public RawPacketReader(Channel connectionChannel) {
        connectionChannel.pipeline().addBefore("encoder","raw_packet_reader", this);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("Server Send Raw: "+ msg);
        if (msg instanceof ByteBuf buf) {
            byte[] bytes = new byte[buf.capacity()];
            char[] chars = new char[buf.capacity()];
            for (int i = 0; i < buf.capacity(); i++) {
                chars[i] = buf.getChar(i);
                //bytes[i] = buf.getByte(i);
            }
            System.out.println(chars);
            //System.out.println(Arrays.toString(new ByteArrayInputStream(bytes).readAllBytes()));
            //ByteBufInputStream inputStream = new ByteBufInputStream(buf);
            //System.out.println(Arrays.toString(inputStream.readAllBytes()));
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
