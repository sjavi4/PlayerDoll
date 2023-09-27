package me.autobot.playerdoll.Dolls.Networks;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.local.LocalAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;


public class DollNetworkManager extends Connection {
    public DollNetworkManager(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
        this.channel = new EmbeddedChannel();
        this.channel.bind(new LocalAddress("BOT"));
        try {
            this.address = new InetSocketAddress(new ServerSocket(0).getLocalPort());
        } catch (IOException e) {

        }

    }
    @Override
    public void setReadOnly() {}

    @Override
    public void handleDisconnection() {}

    @Override
    public void setListener(PacketListener packetListener) {}

    @Override
    public SocketAddress getRemoteAddress() {
        return this.address;
    }

}
