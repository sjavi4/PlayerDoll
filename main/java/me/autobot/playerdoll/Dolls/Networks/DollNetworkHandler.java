package me.autobot.playerdoll.Dolls.Networks;

import io.netty.channel.local.LocalAddress;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;

import java.net.SocketAddress;
import java.util.Set;

public class DollNetworkHandler extends ServerGamePacketListenerImpl {
    public DollNetworkHandler(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer, CommonListenerCookie.createInitial(entityplayer.getGameProfile()));
    }

    @Override
    public void send(Packet<?> packet) {}

    @Override
    public void teleport(double d, double e, double f, float g, float h, Set<RelativeMovement> set)
    {
        super.teleport(d, e, f, g, h, set);
        if (player.serverLevel().getPlayerByUUID(player.getUUID()) != null) {
            resetPosition();
            player.serverLevel().getChunkSource().move(player);
        }
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return new LocalAddress("local");
    }
}
