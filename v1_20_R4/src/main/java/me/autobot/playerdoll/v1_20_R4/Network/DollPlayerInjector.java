package me.autobot.playerdoll.v1_20_R4.Network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler.PlayerLoginListener;
import me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler.ServerLoginListener;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import java.util.logging.Level;

public class DollPlayerInjector extends ChannelDuplexHandler {
    public final Connection serverConnection;
    public final DollPlayerInjector playerPacketHandler;
    public DollPlayerInjector(Connection connection) {
        this.serverConnection = connection;
        this.playerPacketHandler = this;
        serverConnection.channel.pipeline().addBefore("packet_handler", "doll_player_injector", playerPacketHandler);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServerboundLoginAcknowledgedPacket) {
            PlayerDoll.getPluginLogger().log(Level.INFO, "Found Acknowledged Packet for Player");
            if (serverConnection.getPacketListener() instanceof ServerLoginListener) {
                // Doll login
                serverConnection.channel.pipeline().remove("doll_player_injector");
                super.channelRead(ctx, msg);
                return;
            }
            serverConnection.suspendInboundAfterProtocolChange();
            ServerLoginPacketListenerImpl oldListener = (ServerLoginPacketListenerImpl) serverConnection.getPacketListener();
            ServerPlayer player = PlayerLoginListener.getPlayer(oldListener);
            serverConnection.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.LOGIN.codec(PacketFlow.SERVERBOUND));
            PlayerLoginListener listener = new PlayerLoginListener(CursedConnections.server,serverConnection, player);
            this.serverConnection.setListener(listener);
            serverConnection.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.CONFIGURATION.codec(PacketFlow.SERVERBOUND));
            serverConnection.resumeInboundAfterProtocolChange();
            serverConnection.channel.pipeline().remove("doll_player_injector");
        }
        super.channelRead(ctx, msg);
    }
}
