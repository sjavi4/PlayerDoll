package me.autobot.playerdoll.v1_20_R1.Dolls;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;


public class DollNetworkManager extends Connection {
    public DollNetworkHandler handler;
    public DollNetworkManager() {
        super(PacketFlow.SERVERBOUND);
        channel = new EmbeddedChannel();
        this.address = channel.remoteAddress();
    }


    @Override
    public void setReadOnly() {}

    @Override
    public void handleDisconnection() {
    }
    @Override
    public void send(Packet<?> packet, PacketSendListener listener) {
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void setListener(PacketListener packetListener) {
        if (handler == null) {
            return;
        }
        super.setListener(handler);
    }

/*
    @Override
    public SocketAddress getRemoteAddress() {
        return this.address;
    }

 */
}
