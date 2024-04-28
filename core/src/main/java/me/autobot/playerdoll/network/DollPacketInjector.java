package me.autobot.playerdoll.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.function.Function;

public class DollPacketInjector extends ChannelDuplexHandler {
    public Function<Object, Boolean> sendPacketTask;
    public boolean allowPacketSend = true;
    public DollPacketInjector(Channel connectionChannel) {
        connectionChannel.pipeline().addBefore("packet_handler", "doll_packet_injector", this);
        //new RawPacketReader(connectionChannel);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //System.out.println("Server Send: " + msg);
        if (sendPacketTask.apply(msg)) {
            super.write(ctx, msg, promise);
        }
        /*
                msg instanceof ClientboundKeepAlivePacket ||
                msg instanceof ClientboundDisconnectPacket ||
                msg instanceof ClientboundResourcePackPopPacket ||
                msg instanceof ClientboundResourcePackPushPacket
         */
    }
}
