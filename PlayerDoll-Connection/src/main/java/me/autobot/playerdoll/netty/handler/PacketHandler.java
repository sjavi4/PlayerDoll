package me.autobot.playerdoll.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.netty.packet.ClientPackets;
import me.autobot.playerdoll.netty.packet.ServerPackets;
import net.minecraft.network.PacketCompressor;
import net.minecraft.network.PacketDecompressor;
import org.bukkit.Bukkit;

public class PacketHandler extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ClientPackets.commonKeepAlivePacketClass.isInstance(msg)) {
            ctx.channel().writeAndFlush(ServerPackets.createKeepAlivePacket(ClientPackets.getKeepAliveId(msg)));
        } else if (ClientPackets.commonDisconnectPacketClass.isInstance(msg)) {
            Bukkit.getLogger().info(String.format("[PlayerDoll] Doll disconnected (Common): %s", ClientPackets.getCommonDisconnectReason(msg)));
            ctx.close();
        } else if (ClientPackets.loginDisconnectPacketClass.isInstance(msg)) {
            Bukkit.getLogger().info(String.format("[PlayerDoll] Doll disconnected (Login): %s", ClientPackets.getLoginDisconnectReason(msg)));
            ctx.close();
        } else if (ClientPackets.compressionPacketClass.isInstance(msg)) {
            int i = ClientPackets.getCompressionThreshold(msg);
            if (i >= 0) {
                Bukkit.getLogger().info("[PlayerDoll] Setup Compression: " + i);
                ChannelPipeline pipeline = ctx.pipeline();
                if (DollConnection.PRE_1_20_4) {
                    pipeline.addBefore("decoder", "decompress", new PacketDecompressor(i, false));
                    pipeline.addBefore("encoder", "compress", new PacketCompressor(i));
                } else {
                    pipeline.addAfter("splitter", "decompress", new PacketDecompressor(i, false));
                    pipeline.addAfter("prepender", "compress", new PacketCompressor(i));
                }
            }
        } else if (ClientPackets.loginPacketClass.isInstance(msg)) {
            ctx.pipeline().addLast("play_handler", new GamePlayHandler());
        } else if (ClientPackets.resourcePackPacketClass != null && ClientPackets.resourcePackPacketClass.isInstance(msg)) {
            ctx.writeAndFlush(ServerPackets.createResourcePackResponsePacket(msg));
        } else if (ClientPackets.resourcePackPushPacketClass != null && ClientPackets.resourcePackPushPacketClass.isInstance(msg)) {
            ctx.writeAndFlush(ServerPackets.createResourcePackResponsePacket(msg));
        }
//        else if (ClientPackets.loginCustomPayloadClass != null && ClientPackets.loginCustomPayloadClass.isInstance(msg)) {
//            ctx.channel().writeAndFlush(ServerPackets.createLoginCustomPayloadAnswerPacket(ClientPackets.getLoginCustomPayloadId(msg)));
//        }
        else if (ClientPackets.selectKnownPacksPacketClass != null && ClientPackets.selectKnownPacksPacketClass.isInstance(msg)) {
            ctx.channel().writeAndFlush(ServerPackets.createSelectKnownPacksPacket(ClientPackets.getSelectKnownPacks(msg)));
        }
        super.channelRead(ctx, msg);

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Bukkit.getLogger().warning("[PlayerDoll] Doll Client: Exception Caught");
        cause.printStackTrace();
        Bukkit.getLogger().warning("[PlayerDoll] " + cause.getMessage());
        Bukkit.getLogger().warning("[PlayerDoll] Client Shutdown");
        ctx.close();
    }
}
