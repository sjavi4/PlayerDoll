package me.autobot.playerdoll.netty.handler;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.connection.ConnectionFetcher;
import me.autobot.playerdoll.api.connection.PacketVarIntHelper;
import me.autobot.playerdoll.connection.DollConnection;
import me.autobot.playerdoll.netty.decoder.login.Post1_20_R4_LoginDecoder;
import me.autobot.playerdoll.netty.decoder.login.Pre1_20_R4_LoginDecoder;
import me.autobot.playerdoll.netty.encoder.login.Post1_20_R4_LoginEncoder;
import me.autobot.playerdoll.netty.encoder.login.Pre1_20_R4_LoginEncoder;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class HandshakeHandler extends ChannelDuplexHandler {
    private final GameProfile profile;
    private final InetSocketAddress address;
    private final Player caller;

    public HandshakeHandler(InetSocketAddress address, GameProfile profile, Player caller) {
        this.profile = profile;
        this.address = address;
        this.caller = caller;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        DollConnection.DOLL_CONNECTIONS.put(profile.getId(), ctx.channel());
        PlayerDollAPI.getLogger().log(Level.INFO, "Doll client {0} started", profile.getName());

        Connection connection = PlayerDollAPI.getConnection();
        
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(0x00); //packet id for handshake
        PacketVarIntHelper.writeVarInt(byteBuf, connection.protocolNumber());
        PacketVarIntHelper.writeVarInt(byteBuf, address.getHostName().length());
        byteBuf.writeCharSequence(address.getHostName(), StandardCharsets.UTF_8);
        byteBuf.writeShort(address.getPort());
        PacketVarIntHelper.writeVarInt(byteBuf, 2);

        ctx.channel().writeAndFlush(byteBuf);
        
        int count = 0;
        while (!ConnectionFetcher.startCursedConnection(ctx.channel().localAddress().toString(), profile, caller)) {
            count++;
            if (count >= 10) {
                PlayerDollAPI.getLogger().info(String.format("Client %s wait too Long", profile.getName()));
                ctx.close();
                return;
            }
            try {
                PlayerDollAPI.getLogger().info(String.format("%s Wait for Login Listener", profile.getName()));
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }

        ByteBuf loginHelloBuf = Unpooled.buffer();
        loginHelloBuf.writeByte(0x00); //packet id for handshake
        PacketVarIntHelper.writeVarInt(loginHelloBuf, profile.getName().length());
        loginHelloBuf.writeCharSequence(profile.getName(), StandardCharsets.UTF_8);
        loginHelloBuf.writeLong(profile.getId().getMostSignificantBits());
        loginHelloBuf.writeLong(profile.getId().getLeastSignificantBits());

        ctx.channel().writeAndFlush(loginHelloBuf);

        if (connection.pre_1204()) {
            ctx.pipeline().replace("encoder", "encoder", new Pre1_20_R4_LoginEncoder());
            ctx.pipeline().replace("decoder", "decoder", new Pre1_20_R4_LoginDecoder());
            ctx.channel().attr(Pre1_20_R4_Protocols.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(Pre1_20_R4_Protocols.CLIENT_LOGIN_PROTOCOL);
            ctx.channel().attr(Pre1_20_R4_Protocols.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(Pre1_20_R4_Protocols.SERVER_LOGIN_PROTOCOL);

        } else {
            ChannelHandler handler = ctx.pipeline().get("inbound_config");
            // Remove to prevent exception
            if (handler != null) {
                ctx.pipeline().replace("encoder", "encoder", new Post1_20_R4_LoginEncoder());
                ctx.pipeline().replace(handler, "decoder", new Post1_20_R4_LoginDecoder());
            }
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DollConnection.DOLL_CONNECTIONS.remove(profile.getId());
        PlayerDollAPI.getLogger().log(Level.INFO, "Doll client {0} ended", profile.getName());
        super.channelInactive(ctx);
    }
}
