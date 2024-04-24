package me.autobot.playerdoll.v1_20_R3.Network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;

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
        if (suspendPacketSend) {
            if (
                    msg instanceof ClientboundKeepAlivePacket ||
                            msg instanceof ClientboundDisconnectPacket ||
                            msg instanceof ClientboundResourcePackPopPacket ||
                            msg instanceof ClientboundResourcePackPushPacket
            ) {
                super.write(ctx, msg, promise);
            }
            /*
            else {
                if (msg instanceof ClientboundPlayerPositionPacket packet) {
                    super.write(ctx, msg, promise);
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),() ->{
                        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl)serverConnection.getPacketListener();
                        listener.handleAcceptTeleportPacket(new ServerboundAcceptTeleportationPacket(packet.getId()));
                        ServerPlayer player = listener.player;
                        listener.handleMovePlayer(new ServerboundMovePlayerPacket.PosRot(
                                player.getX() + packet.getX(),
                                player.getY() + packet.getY(),
                                player.getZ() + packet.getZ(),
                                player.getXRot() + packet.getXRot(),
                                player.getYRot() + packet.getYRot(),
                                false
                        ));
                    },5);

                }
            }

             */
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
