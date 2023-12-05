package me.autobot.playerdoll.v1_20_R2.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;

public class SpigotDollImpl extends AbstractDoll {
    public SpigotDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }
    @Override
    public void spawnToWorld() {
        //this.connection = new DollNetworkHandler(server,dollNetworkManager,this);
        super.spawnToWorld();
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));

        //this.server.getPlayerList().respawn(this, this.serverLevel(), true, this.getBukkitEntity().getLocation(), true, PlayerRespawnEvent.RespawnReason.PLUGIN);

    }

    @Override
    public void tick() {
        if (this.getServer().getTickCount() % 10 == 0) {
            connection.resetPosition();
            this.serverLevel().getChunkSource().move(this);
            if (noPhantom) this.getBukkitEntity().setStatistic(Statistic.TIME_SINCE_REST,0);
        }
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.checkBlocking(damageSource)) {
            //this.attackBlocked = true;
            this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.serverLevel().random.nextFloat() * 0.4F);
        }
        boolean damaged = super.hurt(damageSource, f);
        if (damaged) {
            if (this.hurtMarked) {
                this.hurtMarked = false;
                Runnable r = () -> this.hurtMarked = true;
                Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),r);            }
        }
        return damaged;
    }

    @Override
    public void disconnect() {
        super.disconnect();
        this.serverLevel().removePlayerImmediately(this, RemovalReason.DISCARDED);
        connection.onDisconnect(Component.literal("Disconnected"));
    }

    @Override
    public void foliaDisconnect(boolean force) {

    }

    @Override
    public void getFoliaRegionizedServer() {
    }
}
