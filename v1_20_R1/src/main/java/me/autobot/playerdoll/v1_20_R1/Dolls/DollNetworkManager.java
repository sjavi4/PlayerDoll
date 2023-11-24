package me.autobot.playerdoll.v1_20_R1.Dolls;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;

import java.net.SocketAddress;


public class DollNetworkManager extends Connection {
    public DollNetworkManager(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
        channel = new EmbeddedChannel();
    }


    @Override
    public void setReadOnly() {}

    @Override
    public void handleDisconnection() {
    }

    @Override
    public void setListener(PacketListener packetListener) {}

    @Override
    public SocketAddress getRemoteAddress() {
        return this.address;
    }
}
