package me.autobot.playerdoll.Dolls.Networks;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.local.LocalAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.Arrays;


public class DollNetworkManager extends Connection {
    ServerPlayer player;
    public DollNetworkManager(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
        channel = new EmbeddedChannel();
        //this.channel.bind(new LocalAddress("BOT"));
        //try {
        //    this.address = new InetSocketAddress(new ServerSocket(0).getLocalPort());
        //} catch (IOException e) {
        //}
    }


    @Override
    public void setReadOnly() {}

    @Override
    public void handleDisconnection() {
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }
    public ServerPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void setListener(PacketListener packetListener) {}

    @Override
    public SocketAddress getRemoteAddress() {
        return this.address;
    }
}
