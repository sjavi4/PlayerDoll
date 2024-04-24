package me.autobot.playerdoll.v1_20_R4.player;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.CustomEvent.DollJoinEvent;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.IServerDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.CarpetMod.NMSPlayerEntityActionPack;
import me.autobot.playerdoll.v1_20_R4.Network.CursedConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.UUID;

public class ServerDoll extends ServerPlayerExt implements IServerDoll {
    ServerPlayer caller;
    int dollTickCount = 0;
    DollConfig dollConfig;
    public Connection serverConnection;
    public static ServerPlayerExt callSpawn(String name, UUID uuid) {
        MinecraftServer server = CursedConnections.server;
        ServerLevel serverLevel = server.overworld();
        GameProfile profile = new GameProfile(uuid,name);
        return new ServerDoll(server,serverLevel,profile);
    }
    public ServerDoll(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile, ClientInformation.createDefault());
    }
    public void setup(Player caller) {
        this.caller = caller == null ? this : ((CraftPlayer)caller).getHandle();

        Bukkit.getPluginManager().callEvent(new DollJoinEvent(this.getBukkitEntity(), caller, this));

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
        this.actionPack = new NMSPlayerEntityActionPack(this);
        //IDoll.setSkin(this.getBukkitEntity(), this);
    }

    @Override
    public void tick() {
        if (PlayerDoll.isFolia) {
            dollTickCount = PlayerDoll.getFoliaHelper().getTick();
        } else {
            dollTickCount = this.server.getTickCount();
        }
        super.tick();
    }
    @Override
    protected void beforeTick() {
        if (!dollConfig.dollRealPlayerTickAction.getValue()) {
            updateActionTask.run();
        } else {
            if (PlayerDoll.isFolia) {
                PlayerDoll.getFoliaHelper().addTask(updateActionTask);
            } else {
                server.tell(server.wrapRunnable(updateActionTask));
            }
        }
        updateActionTask.run();
    }

    @Override
    protected void afterTick() {
        if (!dollConfig.dollRealPlayerTickUpdate.getValue()) {
            this.doTick();
        }
        if (dollTickCount % 10 == 0) {
            connection.resetPosition();
            this.serverLevel().getChunkSource().move(this);
            if (!dollConfig.dollPhantom.getValue()) IServerDoll.resetPhantomStatistic(this.getBukkitEntity());
        }
    }
    @Override
    public boolean isDoll() {
        return true;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return dollConfig != null && dollConfig.dollHostility.getValue() && super.canBeSeenAsEnemy();
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.disconnect();
    }

    @Override
    public void disconnect() {
        this.setHealth(20.0f);
        super.disconnect();
        sendPacket(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(this.getUUID())));
    }
    // IDK-why-Way to fix portal cooldown counter not working
    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        if (wonGame) {
            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
            connection.handleClientCommand(p);
        }
        if (connection.player.isChangingDimension()) {
            connection.player.hasChangedDimension();
        }
        return connection.player;
    }
    @Override
    public Entity changeDimension(ServerLevel serverLevel, PlayerTeleportEvent.TeleportCause cause) {
        super.changeDimension(serverLevel, cause);
        return this.changeDimension(serverLevel);
    }
    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return IServerDoll.executeHurt(this,getBukkitEntity(),super.hurt(damageSource,f));
    }
    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0, y, 0.0, onGround);
    }
    @Override
    public DollConfig getDollConfig() {
        return dollConfig;
    }

    @Override
    public void setDollConfig(DollConfig dollConfig) {
        this.dollConfig = dollConfig;
    }

    @Override
    public void setDollMaxUpStep(float h) {
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(h);
    }

    @Override
    public Player getCaller() {
        return this.caller.getBukkitEntity();
    }

    @Override
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(UUID.fromString(dollConfig.ownerUUID.getValue()));
    }

    @Override
    public void dollDisconnect() {
        disconnect();
    }

    @Override
    public void dollKill() {
        kill();
    }

    @Override
    public boolean getDollHurtMarked() {
        return this.hurtMarked;
    }

    @Override
    public void setDollHurtMarked(boolean b) {
        this.hurtMarked = b;
    }
}
