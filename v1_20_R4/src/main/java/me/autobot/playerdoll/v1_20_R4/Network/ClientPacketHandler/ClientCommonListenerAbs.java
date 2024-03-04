package me.autobot.playerdoll.v1_20_R4.Network.ClientPacketHandler;

import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.*;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;

public abstract class ClientCommonListenerAbs implements ClientCommonPacketListener {
    public final Connection connection;

    protected ClientCommonListenerAbs(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleResourcePackPush(ClientboundResourcePackPushPacket clientboundResourcePackPushPacket) {
        // Handle
        PlayerDoll.getPluginLogger().log(Level.INFO,"Fake Client Received ResourcePack Push Packet");
        send(new ServerboundResourcePackPacket(clientboundResourcePackPushPacket.id(), ServerboundResourcePackPacket.Action.ACCEPTED));
        send(new ServerboundResourcePackPacket(clientboundResourcePackPushPacket.id(), ServerboundResourcePackPacket.Action.DOWNLOADED));
        Runnable task = () -> send(new ServerboundResourcePackPacket(clientboundResourcePackPushPacket.id(), ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().globalTaskDelayed(task,25L);
        } else {
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),task,25L);
        }
    }

    @Override
    public void handleUpdateTags(ClientboundUpdateTagsPacket clientboundUpdateTagsPacket) {
        // Not Handle This
    }

    public void handleResourcePackPop(ClientboundResourcePackPopPacket clientboundResourcePackPopPacket) {
        // Not Handle This
        PlayerDoll.getPluginLogger().log(Level.INFO,"Fake Client Received ResourcePack Pop Packet");
        clientboundResourcePackPopPacket.id().ifPresent((packID) -> send(new ServerboundResourcePackPacket(packID, ServerboundResourcePackPacket.Action.ACCEPTED)));
    }
    public void handlePing(ClientboundPingPacket clientboundPingPacket) {
    }
    public void handleKeepAlive(ClientboundKeepAlivePacket clientboundKeepAlivePacket) {
        this.sendWhen(new ServerboundKeepAlivePacket(clientboundKeepAlivePacket.getId()), () -> true, Duration.ofMinutes(1L));
    }

    public void onDisconnect(Component component) {
        this.connection.disconnect(component);
    }
    public void send(Packet<?> packet) {
        this.connection.send(packet);
    }
    private void sendWhen(Packet<? extends ServerboundPacketListener> packet, BooleanSupplier booleanSupplier, Duration duration) {
        if (booleanSupplier.getAsBoolean()) {
            this.send(packet);
        }
    }
}
