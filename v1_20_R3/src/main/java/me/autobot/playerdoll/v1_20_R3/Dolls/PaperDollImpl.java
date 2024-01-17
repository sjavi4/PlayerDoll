package me.autobot.playerdoll.v1_20_R3.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Dolls.IDoll;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class PaperDollImpl extends AbstractDoll {
    public PaperDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }
    @Override
    public void spawnToWorld() {
        super.spawnToWorld();
        sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
        sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
    }

    @Override
    public void tick() {
        nonFoliaTickCount = this.getServer().getTickCount();
        super.tick();
    }
    @Override
    public void disconnect() {
        super.disconnect();
        this.serverLevel().removePlayerImmediately(this, RemovalReason.DISCARDED);
        connection.onDisconnect(Component.literal("Disconnected"));
        IDoll.PaperRemoveChunkLoader(this.serverLevel(),this);
    }
}
