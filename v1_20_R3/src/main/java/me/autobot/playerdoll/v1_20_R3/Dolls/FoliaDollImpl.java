package me.autobot.playerdoll.v1_20_R3.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Dolls.Folia.v1_20_R2_HandleAcceptedLogin;
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
        new v1_20_R2_HandleAcceptedLogin(this.dollNetworkManager,this, this.listenerCookie,this.server.getPlayerList(),serverLevel().getChunkSource(),spawnPacketTask);
    }
    @Override
    public void teleportTo() {
        PlayerDoll.getFoliaHelper().entityTeleportTo(this.getBukkitEntity(), player.getBukkitEntity().getLocation());
    }
    @Override
    public void setDollLookAt() {
        PlayerDoll.getFoliaHelper().setDollLookAt(player.getBukkitEntity(),lookAtPacketTask);
    }
    @Override
    public void disconnect() {
        super.disconnect();
        this.connection.onDisconnect(Component.literal("Disconnected"));
    }
}
