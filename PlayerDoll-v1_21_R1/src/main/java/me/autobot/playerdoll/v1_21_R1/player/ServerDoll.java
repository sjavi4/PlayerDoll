package me.autobot.playerdoll.v1_21_R1.player;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.event.DollJoinEvent;
import me.autobot.playerdoll.util.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;

import javax.annotation.Nullable;


public class ServerDoll extends ExtServerPlayer implements Doll {
    private ServerPlayer caller;
    private long dollTickCount = 0L;
    public final DollConfig dollConfig = DollConfig.getDollConfigForOnline(this, getGameProfile().getName(), uuid);

    public static ServerDoll callSpawn(GameProfile profile) {
        MinecraftServer server = (MinecraftServer) ReflectionUtil.getDedicatedServerInstance();
        return new ServerDoll(server,server.overworld(),profile);
    }
    public ServerDoll(MinecraftServer server, ServerLevel level, GameProfile profile) {
        super(server, level, profile);
    }

    public void setup(org.bukkit.entity.Player caller) {
        this.caller = caller == null ? this : ((CraftPlayer)caller).getHandle();

        PlayerDoll.callSyncEvent(new DollJoinEvent(this.getBukkitEntity(), caller, this));

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
        //IDoll.setSkin(this.getBukkitEntity(), this);
    }

    @Override
    public boolean isDoll() {
        return true;
    }

    @Override
    public void dollDisconnect(String r) {
        shakeOff();
        Runnable t = () -> this.connection.disconnect(Component.literal(r));
        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
            PlayerDoll.scheduler.entityTask(t, getBukkitPlayer());
        } else {
            t.run();
        }
        //this.connection.disconnect(r);
        //this.connection.onDisconnect(Component.literal(r));
        //this.connection.disconnect(r);
//        Runnable t = () -> this.connection.onDisconnect(Component.literal(r));
//        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
//            PlayerDoll.scheduler.entityTask(t, getBukkitPlayer());
//        } else {
//            t.run();
//        }
    }
//
//    @Override
//    public void dollKill() {
//        kill();
//    }

    @Override
    public void setDollMaxUpStep(double d) {
        getBukkitPlayer().getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(d);
    }

    @Override
    public org.bukkit.entity.Player getCaller() {
        return caller.getBukkitEntity();
    }


    @Override
    void beforeTick() {
        if (!dollConfig.dollRealPlayerTickAction.getValue()) {
            // Entity tick
            updateActionPack();
        } else {
            // Add Network task
            if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
                ReflectionUtil.foliaAddTask(this::updateActionPack);
                //PlayerDoll.scheduler.entityTask(this::updateActionPack, getBukkitPlayer());
            } else {
                server.tell(server.wrapRunnable(this::updateActionPack));
            }
        }
    }

    @Override
    void afterTick() {
        if (!dollConfig.dollRealPlayerTickUpdate.getValue()) {
            this.doTick();
        }
        if (dollTickCount % 10 == 0) {
            connection.resetPosition();
            serverLevel().getChunkSource().move(this);
        }
        if (dollTickCount % 24000 == 0 && dollConfig.dollPhantom.getValue()) {
            Doll.resetPhantomStatistic(getBukkitEntity());
        }
    }

    private void shakeOff() {
        if (getVehicle() instanceof Player) {
            stopRiding();
        }
        for (Entity passenger : getIndirectPassengers()) {
            if (passenger instanceof Player) {
                passenger.stopRiding();
            }
        }
    }

    @Override
    public void tick() {
        dollTickCount = Doll.getTickCount(getBukkitPlayer());
        super.tick();
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return dollConfig.dollHostility.getValue() && super.canBeSeenAsEnemy();
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionTransition dimensiontransition) {
        super.changeDimension(dimensiontransition);
        if (wonGame) {
            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
            connection.handleClientCommand(p);
        }
        if (connection.player.isChangingDimension()) {
            connection.player.hasChangedDimension();
        }
        return connection.player;
    }


//    @Override
//    public Entity changeDimension(ServerLevel serverLevel) {
//        if (wonGame) {
//            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
//            connection.handleClientCommand(p);
//        }
//        if (connection.player.isChangingDimension()) {
//            connection.player.hasChangedDimension();
//        }
//        return connection.player;
//    }
//    @Override
//    public Entity changeDimension(ServerLevel serverLevel, PlayerTeleportEvent.TeleportCause cause) {
//        super.changeDimension(serverLevel, cause);
//        return this.changeDimension(serverLevel);
//    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        // To fix the knockback in weird way (not projectile nor explosion, but regular damage)
        Vec3 ov = getDeltaMovement(); // original movement before damage
        boolean hurt = super.hurt(damageSource,f);
        // true for valid attack,
        // false for invalid attack (using shield / invincible)
        if (hurt) {
            Vec3 v = getDeltaMovement(); // processed movement after super.hurt();
            if (ov == v) {
                return true;
            }
            PlayerDoll.scheduler.entityTask(() -> setDeltaMovement(v), getBukkitPlayer());
            return true;
        }
        setDeltaMovement(ov);
        return false;
    }
    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0, y, 0.0, onGround);
    }
    @Override
    public void kill() {
        dollDisconnect("Killed");
    }
    @Override
    public void die(DamageSource cause)
    {
        shakeOff();
        super.die(cause);
        //setHealth(20);
        //this.foodData = new FoodData();
        dollDisconnect(getCombatTracker().getDeathMessage().getString());
    }
}
