package me.autobot.playerdoll.Dolls.Networks;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;

public class DollNetworkManager extends Connection {
    public DollNetworkManager(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
        this.channel = new EmbeddedChannel();
    }
    @Override
    public void setReadOnly() {}

    @Override
    public void handleDisconnection() {}

    @Override
    public void setListener(PacketListener packetListener) {}
}
