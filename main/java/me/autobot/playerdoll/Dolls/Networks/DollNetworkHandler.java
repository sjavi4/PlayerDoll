package me.autobot.playerdoll.Dolls.Networks;

import me.autobot.playerdoll.Dolls.DollManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_20_R1.util.Waitable;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DollNetworkHandler extends ServerGamePacketListenerImpl {
    public DollNetworkHandler(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
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
}
