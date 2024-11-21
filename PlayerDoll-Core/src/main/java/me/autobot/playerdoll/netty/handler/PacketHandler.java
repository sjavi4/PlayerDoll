package me.autobot.playerdoll.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.resolver.ClientPackets;
import me.autobot.playerdoll.api.resolver.ServerPackets;
import net.minecraft.network.PacketCompressor;
import net.minecraft.network.PacketDecompressor;

import java.util.UUID;
import java.util.logging.Level;

public class PacketHandler extends ChannelDuplexHandler {

    private final UUID uuid;
    public PacketHandler(UUID uuid) {
        this.uuid = uuid;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ClientPackets.commonKeepAlive.isInstance(msg)) {
            ctx.channel().writeAndFlush(ServerPackets.createKeepAlivePacket(ClientPackets.getKeepAliveId(msg)));
        } else if (ClientPackets.commonDisconnect.isInstance(msg)) {
            PlayerDollAPI.getLogger().log(Level.INFO, "Doll disconnected (Common): {0}", ClientPackets.getCommonDisconnectReason(msg));
            ctx.close();
        } else if (ClientPackets.loginDisconnect.isInstance(msg)) {
            PlayerDollAPI.getLogger().log(Level.INFO, "Doll disconnected (Login): {0}", ClientPackets.getLoginDisconnectReason(msg));
            ctx.close();
        } else if (ClientPackets.loginCompression.isInstance(msg)) {
            int i = ClientPackets.getCompressionThreshold(msg);
            if (i >= 0) {
                PlayerDollAPI.getLogger().info("Setup Compression: " + i);
                ChannelPipeline pipeline = ctx.pipeline();
                if (PlayerDollAPI.getConnection().pre_1204()) {
                    pipeline.addBefore("decoder", "decompress", new PacketDecompressor(i, false));
                    pipeline.addBefore("encoder", "compress", new PacketCompressor(i));
                } else {
                    pipeline.addAfter("splitter", "decompress", new PacketDecompressor(i, false));
                    pipeline.addAfter("prepender", "compress", new PacketCompressor(i));
                }
            }
        } else if (ClientPackets.gameLogin.isInstance(msg)) {
            ctx.pipeline().addLast("play_handler", new GamePlayHandler(uuid));
        } else if (ClientPackets.commonResourcePack_1202 != null && ClientPackets.commonResourcePack_1202.isInstance(msg)) {
            ctx.writeAndFlush(ServerPackets.createResourcePackResponsePacket(msg));
        } else if (ClientPackets.commonResourcePackPush_1204 != null && ClientPackets.commonResourcePackPush_1204.isInstance(msg)) {
            ctx.writeAndFlush(ServerPackets.createResourcePackResponsePacket(msg));
        }
//        else if (ClientPackets.loginCustomPayloadClass != null && ClientPackets.loginCustomPayloadClass.isInstance(msg)) {
//            ctx.channel().writeAndFlush(ServerPackets.createLoginCustomPayloadAnswerPacket(ClientPackets.getLoginCustomPayloadId(msg)));
//        }
        else if (ClientPackets.configSelectKnownPacks != null && ClientPackets.configSelectKnownPacks.isInstance(msg)) {
            ctx.channel().writeAndFlush(ServerPackets.createSelectKnownPacksPacket(ClientPackets.getSelectKnownPacks(msg)));
        }
        super.channelRead(ctx, msg);

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        PlayerDollAPI.getLogger().warning("Doll Client: Exception Caught");
        cause.printStackTrace();
        PlayerDollAPI.getLogger().warning("" + cause.getMessage());
        PlayerDollAPI.getLogger().warning("Client Shutdown");
        ctx.close();
    }
}
