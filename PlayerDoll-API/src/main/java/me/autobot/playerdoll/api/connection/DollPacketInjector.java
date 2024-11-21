package me.autobot.playerdoll.api.connection;

import io.netty.channel.*;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.resolver.ClientPackets;


public class DollPacketInjector extends ChannelDuplexHandler {
    public boolean allowPacketSend = true;

    public DollPacketInjector(Channel connectionChannel) {
        ChannelPipeline pipeline = connectionChannel.pipeline();
        if (pipeline.get("doll_packet_injector") != null) {
            return;
        }
        connectionChannel.pipeline().addBefore("packet_handler", "doll_packet_injector", this);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (allowPacketSend && ClientPackets.gameLogin.isInstance(msg)) {
            allowPacketSend = false;
            super.write(ctx, msg, promise);
            PlayerDollAPI.getLogger().info("Doll Joined Successfully, Suspend Server-side Packet Sending.");
            return;
        } else if (!allowPacketSend) {
            if (ClientPackets.packetSet.contains(msg.getClass())) {
                super.write(ctx, msg, promise);
            }
            return;
        }
        super.write(ctx, msg, promise);
    }
}
