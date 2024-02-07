package me.autobot.playerdoll.v1_20_R2.Dolls;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;


public class DollNetworkManager extends Connection {
    public final DollNetworkHandler handler;
    public DollNetworkManager(DollNetworkHandler handler) {
        super(PacketFlow.SERVERBOUND);
        channel = new EmbeddedChannel();
        this.address = channel.remoteAddress();
        this.handler = handler;
        setPlay(channel);
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

    public void setPlay(Channel channel) {
        channel.attr(ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.SERVERBOUND));
        channel.attr(ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.CLIENTBOUND));
    }
    @Override
    public void setListener(PacketListener packetListener) {
        super.setListener(handler);
    }
    /*
    @Override
    public SocketAddress getRemoteAddress() {
        return this.address;
    }

     */
}
