package me.autobot.playerdoll.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.FoliaSupport;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class FoliaDollImpl extends AbstractDoll {
    Class<?> regionizedServerFolia;
    public FoliaDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }

    @Override
    public void spawnToWorld() {
        try {
            Method placeNewPlayerFolia = this.server.getPlayerList().getClass().getMethod("placeNewPlayer", Connection.class, ServerPlayer.class, CompoundTag.class, String.class, Location.class);
            placeNewPlayerFolia.invoke(this.server.getPlayerList(), this.dollNetworkManager, this, this.server.getPlayerList().load(this), this.getGameProfile().getName(), this.getBukkitEntity().getLocation());
            this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
            this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            e.printStackTrace();
        }
        this.spawnIn(this.serverLevel());
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
    public void teleportTo() {
        try {
            Method teleportToFolia = this.getClass().getMethod("teleportTo", ServerLevel.class, double.class, double.class, double.class, Set.class, float.class, float.class, PlayerTeleportEvent.TeleportCause.class);
            var pos = this.position();
            teleportToFolia.invoke(this, this.serverLevel(), pos.x, pos.y, pos.z, Set.of(), (float) yaw, (float) pitch, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void setDollLookAt() {
        Runnable r = () -> {
            this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, (byte) yaw));
            this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), (byte) yaw, (byte) pitch, true));
        };
        try {
            long tick = getCurrentTickFolia();
            Object rServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
            regionizedServerFolia.getMethod("addTask", Runnable.class).invoke(rServer, new TickTask(Math.toIntExact(tick) + 2, r));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void tick() {
        long tickCount = getCurrentTickFolia();
        if (tickCount % 10 == 0) {
            connection.resetPosition();
            this.serverLevel().getChunkSource().move(this);
            if (noPhantom) this.getBukkitEntity().setStatistic(Statistic.TIME_SINCE_REST,0);
        }
        super.tick();
    }

    @Override
    public void getFoliaRegionizedServer() {
        try {
            regionizedServerFolia = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), r);
            }
        }
        return damaged;
    }

    private long getCurrentTickFolia() {
        try {
            return (long) regionizedServerFolia.getMethod("getCurrentTick").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void foliaDisconnect(boolean remove) {
        if (remove) {
            FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), this::kill);
        } else {
            FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), ()->this.connection.disconnect(Component.literal("despawn")));
        }
    }
}
