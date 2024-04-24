package me.autobot.playerdoll.v1_20_R4.Network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.UnconfiguredPipelineHandler;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

import java.util.logging.Level;

public class DollPacketInjector extends ChannelDuplexHandler {
    public final Connection serverConnection;
    public final DollPacketInjector dollPacketHandler;
    public DollPacketInjector(Connection connection) {
        this.serverConnection = connection;
        this.dollPacketHandler = this;
        serverConnection.channel.pipeline().addBefore("packet_handler", "doll_packet_injector", dollPacketHandler);
    }

    private boolean suspendPacketSend = false;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!suspendPacketSend && msg instanceof ClientboundLoginPacket) {
            suspendPacketSend = true;
            PlayerDoll.getPluginLogger().log(Level.INFO, "Doll Joined Successfully, Suspend Server-side Packet Sending.");
        } else if (suspendPacketSend) {
            if (msg instanceof ClientboundKeepAlivePacket) {
                super.write(ctx, msg, promise);
            }
            /*
            if (
                    msg instanceof ClientboundKeepAlivePacket ||
                            msg instanceof ClientboundDisconnectPacket ||
                            msg instanceof ClientboundResourcePackPopPacket ||
                            msg instanceof ClientboundResourcePackPushPacket ||
                            msg instanceof ClientboundSelectKnownPacks
            ) {
                super.write(ctx, msg, promise);
            }

             */
            return;
        }
        //System.out.println("Server Send: "+ msg);
        super.write(ctx, msg, promise);
    }
/*
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServerboundFinishConfigurationPacket) {
            suspendPacketSend = true;
            PlayerDoll.getPluginLogger().log(Level.INFO, "Doll Joined Successfully, Suspend Server-side Packet Sending.");
        }
        //System.out.println("Server Read: " + msg);
        super.channelRead(ctx, msg);
    }

 */
}
