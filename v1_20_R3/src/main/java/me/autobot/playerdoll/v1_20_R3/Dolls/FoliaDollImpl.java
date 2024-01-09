package me.autobot.playerdoll.v1_20_R3.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Dolls.FoliaDollHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FoliaDollImpl extends AbstractDoll {
    FoliaDollHelper helper;
    public FoliaDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }
    @Override
    public void spawnToWorld() {
        this.helper = new FoliaDollHelper();
        helper.handleAcceptedLogin(this.dollNetworkManager,this,this.server.getPlayerList(),serverLevel().getChunkSource(),() -> {
            sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
            sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
        });
    }
    @Override
    public void teleportTo() {
        helper.teleportTo(this.getBukkitEntity(),player.getBukkitEntity().getLocation());
    }
    @Override
    public void setDollLookAt() {
        helper.setDollLookAt(2,() -> {
            sendPacket(new ClientboundRotateHeadPacket(this, PacketYaw));
            sendPacket(new ClientboundMoveEntityPacket.Rot(this.getId(), PacketYaw, PacketPitch, true));
        });
    }
    @Override
    public void tick() {
        foliaTickCount = helper.getCurrentTick();
        super.tick();
    }
    @Override
    public void disconnect() {
        super.disconnect();
        this.connection.onDisconnect(Component.literal("Disconnected"));
    }
}
