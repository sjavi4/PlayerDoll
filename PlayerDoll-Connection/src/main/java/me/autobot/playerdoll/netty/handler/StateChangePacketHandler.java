package me.autobot.playerdoll.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.netty.decoder.configuration.Post1_20_R4_ConfigurationDecoder;
import me.autobot.playerdoll.netty.decoder.configuration.Pre1_20_R4_ConfigurationDecoder;
import me.autobot.playerdoll.netty.decoder.play.Post1_20_R4_PlayDecoder;
import me.autobot.playerdoll.netty.decoder.play.Pre1_20_R4_PlayDecoder;
import me.autobot.playerdoll.netty.encoder.configuration.Post1_20_R4_ConfigurationEncoder;
import me.autobot.playerdoll.netty.encoder.configuration.Pre1_20_R4_ConfigurationEncoder;
import me.autobot.playerdoll.netty.encoder.play.Post1_20_R4_PlayEncoder;
import me.autobot.playerdoll.netty.encoder.play.Pre1_20_R4_PlayEncoder;
import me.autobot.playerdoll.netty.packet.ClientPackets;
import me.autobot.playerdoll.netty.packet.ServerPackets;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;

public class StateChangePacketHandler extends SimpleChannelInboundHandler<Packet<?>> {

    private Channel channel;
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> packet) {
        if (ctx.channel().isOpen()) {
            if (DollConnection.PRE_1_20_4) {
                if (ClientPackets.gameProfilePacketClass.isInstance(packet)) {
                    Bukkit.getLogger().info("[PlayerDoll] Login Ack packet received, switch state to Configuration");
                    pre_1_20_4_loginAck();
                } else if (ClientPackets.configFinishPacketClass.isInstance(packet)) {
                    Bukkit.getLogger().info("[PlayerDoll] Config Ack packet received, switch state to Game");
                    pre_1_20_4_finishConfig();
                }
            } else {
                if (ClientPackets.gameProfilePacketClass.isInstance(packet)) {
                    Bukkit.getLogger().info("[PlayerDoll] Login Ack packet received, switch state to Configuration");
                    post_1_20_4_loginAck();
                } else if (ClientPackets.configFinishPacketClass.isInstance(packet)) {
                    Bukkit.getLogger().info("[PlayerDoll] Config Ack packet received, switch state to Game");
                    post_1_20_4_finishConfig();
                }
            }
        }
    }


    private void pre_1_20_4_loginAck() {
        channel.attr(Pre1_20_R4_Protocols.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(Pre1_20_R4_Protocols.CLIENT_CONFIGURATION_PROTOCOL);
        channel.pipeline().replace("decoder", "decoder", new Pre1_20_R4_ConfigurationDecoder());
        channel.writeAndFlush(ServerPackets.createLoginAckPacket());
        channel.attr(Pre1_20_R4_Protocols.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(Pre1_20_R4_Protocols.SERVER_CONFIGURATION_PROTOCOL);
        channel.pipeline().replace("encoder", "encoder", new Pre1_20_R4_ConfigurationEncoder());
    }
    private void post_1_20_4_loginAck() {
        channel.pipeline().replace("decoder", "decoder", new Post1_20_R4_ConfigurationDecoder());
        channel.writeAndFlush(ServerPackets.createLoginAckPacket());
        channel.pipeline().replace("encoder", "encoder", new Post1_20_R4_ConfigurationEncoder());
    }

    private void pre_1_20_4_finishConfig() {
        channel.attr(Pre1_20_R4_Protocols.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(Pre1_20_R4_Protocols.CLIENT_PLAY_PROTOCOL);
        channel.pipeline().replace("decoder", "decoder", new Pre1_20_R4_PlayDecoder());
        channel.writeAndFlush(ServerPackets.createFinishConfigPacket());
        channel.attr(Pre1_20_R4_Protocols.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(Pre1_20_R4_Protocols.SERVER_PLAY_PROTOCOL);
        channel.pipeline().replace("encoder", "encoder", new Pre1_20_R4_PlayEncoder());
        channel.pipeline().remove(this);
    }

    private void post_1_20_4_finishConfig() {
        channel.pipeline().replace("decoder", "decoder", new Post1_20_R4_PlayDecoder());
        channel.writeAndFlush(ServerPackets.createFinishConfigPacket());
        channel.pipeline().replace("encoder", "encoder", new Post1_20_R4_PlayEncoder());
        channel.pipeline().remove(this);
    }
}
