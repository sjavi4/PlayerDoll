package me.autobot.playerdoll.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkHandler;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.reflect.InvocationTargetException;

public class PaperDollImpl extends AbstractDoll {
    public PaperDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }

    @Override
    public void spawnToWorld() {
        this.connection = new DollNetworkHandler(server,dollNetworkManager,this);
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
        this.server.getPlayerList().respawn(this, this.serverLevel(), true, this.getBukkitEntity().getLocation(), true, PlayerRespawnEvent.RespawnReason.PLUGIN);
    }

    @Override
    public void createChunkLoader() {
        try {
            this.getClass().getField("isRealPlayer").setBoolean(this,true);
            Object playerChunkLoader = this.serverLevel().getClass().getField("playerChunkLoader").get(this.serverLevel());
            playerChunkLoader.getClass().getDeclaredMethod("addPlayer", ServerPlayer.class).invoke(playerChunkLoader,this);
        } catch (NoSuchMethodException | NoSuchFieldException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception while invoking Paper's playerChunkLoader");
        }
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
                Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),r);
            }
        }
        return damaged;
    }
    @Override
    public void disconnect() {
        super.disconnect();
        try {
            Object playerChunkLoader = this.serverLevel().getClass().getField("playerChunkLoader").get(this.serverLevel());
            playerChunkLoader.getClass().getDeclaredMethod("removePlayer", ServerPlayer.class).invoke(playerChunkLoader, this);
        } catch (NoSuchMethodException | NoSuchFieldException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception while invoking Paper's playerChunkLoader");
            throw new RuntimeException(e);
        }

    }

    @Override
    public void getFoliaRegionizedServer() {
    }
}
