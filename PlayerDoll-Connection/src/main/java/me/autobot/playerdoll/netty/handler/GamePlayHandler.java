package me.autobot.playerdoll.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.netty.packet.ClientPackets;
import me.autobot.playerdoll.netty.packet.ServerPackets;

public class GamePlayHandler extends ChannelDuplexHandler {
    private int lastAcceptedId = -1;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (ClientPackets.playerPositionPacketClass.isInstance(msg)) {
            int id = ClientPackets.getPlayerPositionPacketId(msg);
            if (lastAcceptedId != id) {
                lastAcceptedId = id;
                channel.writeAndFlush(ServerPackets.createAcceptTeleportPacket(id));
            }
        } else if (ClientPackets.deathScreenPacketClass.isInstance(msg)) {
            channel.writeAndFlush(ServerPackets.createPerformRespawnPacket());
        } else if (ClientPackets.gameEventPacketClass.isInstance(msg)) {
            channel.writeAndFlush(ServerPackets.createPerformRespawnPacket());
        }
        super.channelRead(ctx, msg);
    }


}
