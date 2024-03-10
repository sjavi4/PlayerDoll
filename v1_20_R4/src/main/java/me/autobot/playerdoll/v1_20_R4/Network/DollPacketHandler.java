package me.autobot.playerdoll.v1_20_R4.Network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.logging.Level;

public class DollPacketHandler extends ChannelDuplexHandler {
    //public final ServerPlayer player;
    private boolean suspendPacketSend = false;
    public DollPacketHandler() {
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (suspendPacketSend) {
            if (
                    msg instanceof ClientboundKeepAlivePacket ||
                    msg instanceof ClientboundDisconnectPacket ||
                    msg instanceof ClientboundResourcePackPopPacket ||
                    msg instanceof ClientboundResourcePackPushPacket
            ) {
                super.write(ctx, msg, promise);
            }
            return;
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServerboundFinishConfigurationPacket) {
            suspendPacketSend = true;
            PlayerDoll.getPluginLogger().log(Level.INFO, "Doll Joined Successfully, Suspend Server-side Packet Sending.");
        }
        super.channelRead(ctx, msg);
    }
}
