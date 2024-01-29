package me.autobot.playerdoll.v1_20_R1.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FoliaDollImpl extends AbstractDoll {
    public FoliaDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }
    @Override
    public void spawnToWorld() {
        DollManager.Folia_HandleAcceptedLogin(this.dollNetworkManager,this,this.server.getPlayerList(),serverLevel().getChunkSource(),spawnPacketTask);
        /*
        this.helper = new FoliaDollHelper();

        helper.handleAcceptedLogin(this.dollNetworkManager,this,this.server.getPlayerList(),serverLevel().getChunkSource(),() -> {
            sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
            sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
            //this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
            //this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
        });

         */
    }
    @Override
    public void teleportTo() {
        PlayerDoll.getFoliaHelper().entityTeleportTo(this.getBukkitEntity(), player.getBukkitEntity().getLocation());
        //helper.teleportTo(this.getBukkitEntity(),player.getBukkitEntity().getLocation());
    }
    @Override
    public void setDollLookAt() {
        PlayerDoll.getFoliaHelper().setDollLookAt(player.getBukkitEntity(),lookAtPacketTask);
        /*
        helper.setDollLookAt(2,() -> {
            sendPacket(new ClientboundRotateHeadPacket(this, PacketYaw));
            sendPacket(new ClientboundMoveEntityPacket.Rot(this.getId(), PacketYaw, PacketPitch, true));
        });

         */
    }
    @Override
    public void disconnect() {
        super.disconnect();
        this.connection.onDisconnect(Component.literal("Disconnected"));
    }
}
