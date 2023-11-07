package me.autobot.playerdoll.Dolls.Networks;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;

import java.net.SocketAddress;


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
