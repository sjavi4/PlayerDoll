package me.autobot.playerdoll.netty.handler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.netty.ConnectionFetcher;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.netty.decoder.login.Post1_20_R4_LoginDecoder;
import me.autobot.playerdoll.netty.decoder.login.Pre1_20_R4_LoginDecoder;
import me.autobot.playerdoll.netty.encoder.login.Post1_20_R4_LoginEncoder;
import me.autobot.playerdoll.netty.encoder.login.Pre1_20_R4_LoginEncoder;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

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
        Bukkit.getLogger().info(String.format("[PlayerDoll] Doll client %s started", profile.getName()));

        if (ConnectionFetcher.isBungeeCord) {
            setupBungeeDollData(ctx.channel().localAddress().toString());
            // Make sure no packet delay
            synchronized (this) {
                Thread.currentThread().wait(10000);
            }
        }

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(0x00); //packet id for handshake
        PacketUtil.writeVarInt(byteBuf, DollConnection.protocolNumber);
        PacketUtil.writeVarInt(byteBuf, address.getHostName().length());
        byteBuf.writeCharSequence(address.getHostName(), StandardCharsets.UTF_8);
        byteBuf.writeShort(address.getPort());
        PacketUtil.writeVarInt(byteBuf, 2);

        ctx.channel().writeAndFlush(byteBuf);

        if (ConnectionFetcher.isBungeeCord) {
            synchronized (this) {
                Thread.currentThread().wait(10000);
            }
        }
        int count = 0;
        while (!ConnectionFetcher.startCursedConnection(ctx.channel().localAddress().toString(), profile, caller)) {
            count++;
            if (count >= 10) {
                Bukkit.getLogger().info(String.format("[PlayerDoll] Client %s wait too Long", profile.getName()));
                ctx.close();
                return;
            }
            try {
                Bukkit.getLogger().info(String.format("[PlayerDoll] %s Wait for Login Listener", profile.getName()));
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }

        ByteBuf loginHelloBuf = Unpooled.buffer();
        loginHelloBuf.writeByte(0x00); //packet id for handshake
        PacketUtil.writeVarInt(loginHelloBuf, profile.getName().length());
        loginHelloBuf.writeCharSequence(profile.getName(), StandardCharsets.UTF_8);
        loginHelloBuf.writeLong(profile.getId().getMostSignificantBits());
        loginHelloBuf.writeLong(profile.getId().getLeastSignificantBits());

        ctx.channel().writeAndFlush(loginHelloBuf);

        if (DollConnection.PRE_1_20_4) {
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
        Bukkit.getLogger().info(String.format("[PlayerDoll] Doll client %s ended", profile.getName()));
        super.channelInactive(ctx);
    }

    private void setupBungeeDollData(String localAddress) {

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(0);
        output.writeUTF(localAddress);
        output.writeUTF(profile.getId().toString());
        output.writeUTF(profile.getName());
        output.writeUTF(BasicConfig.get().dollIdentifier.getValue());

        Player caller = this.caller;
        output.writeBoolean(caller == null);
        if (caller == null) {
            FileUtil fileUtil = FileUtil.INSTANCE;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(fileUtil.getFile(fileUtil.getDollDir(), profile.getName()));
            output.writeUTF(config.getString("last-join-server", ""));
        } else {
            output.writeUTF(caller.getUniqueId().toString());
        }
        Bukkit.getServer().sendPluginMessage(ConnectionFetcher.plugin, "playerdoll:doll", output.toByteArray());

    }
}
