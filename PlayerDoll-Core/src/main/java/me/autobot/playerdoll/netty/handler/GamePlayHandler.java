package me.autobot.playerdoll.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.resolver.ClientPackets;
import me.autobot.playerdoll.api.resolver.ServerPackets;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GamePlayHandler extends ChannelDuplexHandler {

    private final UUID uuid;
    private int lastAcceptedId = -1;

    public GamePlayHandler(UUID uuid) {
        this.uuid = uuid;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (ClientPackets.gamePlayerPosition.isInstance(msg)) {
            int id = ClientPackets.getPlayerPositionPacketId(msg);
            if (lastAcceptedId != id) {
                lastAcceptedId = id;
                PlayerDollAPI.getScheduler().entityTaskDelayed(() -> channel.writeAndFlush(ServerPackets.createAcceptTeleportPacket(id)), Bukkit.getPlayer(uuid), 1);
            }
        } else if (ClientPackets.gameDeathScreen.isInstance(msg)) {
            PlayerDollAPI.getScheduler().entityTaskDelayed(() -> channel.writeAndFlush(ServerPackets.createPerformRespawnPacket()), Bukkit.getPlayer(uuid), 2);
            PlayerDollAPI.getScheduler().entityTaskDelayed(channel::close, Bukkit.getPlayer(uuid), 5);
        } else if (ClientPackets.gameEvent.isInstance(msg)) {
            PlayerDollAPI.getScheduler().entityTaskDelayed(() -> channel.writeAndFlush(ServerPackets.createPerformRespawnPacket()), Bukkit.getPlayer(uuid), 2);
        }
        super.channelRead(ctx, msg);
    }


}
