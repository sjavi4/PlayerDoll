package me.autobot.addonDoll.player;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.event.doll.DollJoinEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ServerDoll extends ExtServerPlayer implements Doll {
    private Player caller;
    private long dollTickCount = 0L;
    public final DollConfig dollConfig = DollConfig.getOnlineConfig(this);

    public static ServerDoll callSpawn(GameProfile profile, ServerPlayer serverPlayer) {
        MinecraftServer server = (MinecraftServer) ReflectionUtil.getDedicatedServerInstance();
        return new ServerDoll(server,server.overworld(), profile, serverPlayer);
    }
    public ServerDoll(MinecraftServer server, ServerLevel level, GameProfile profile, ServerPlayer serverPlayer) {
        super(server, level, profile, serverPlayer);
        setClientLoaded(true);
    }

    public void setup(Player caller) {
        this.caller = caller == null ? getBukkitPlayer() : caller;
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
    }

    public void callDollJoinEvent() {
        Bukkit.getPluginManager().callEvent(new DollJoinEvent(getBukkitPlayer(), caller, this));
    }

    @Override
    public boolean isDoll() {
        return true;
    }

    @Override
    public void dollDisconnect() {
        Connection.DOLL_CONNECTIONS.get(uuid).close();
    }

    @Override
    public void setDollMaxUpStep(double f) {
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(f);
        // IDK
        // Paper Here NoSuchFieldError: Class org.bukkit.attribute.Attribute does not have member field 'org.bukkit.attribute.Attribute GENERIC_STEP_HEIGHT'
        //getBukkitPlayer().getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(f);
    }

    @Override
    public Player getCaller() {
        return caller;
    }


    @Override
    void beforeTick() {
        if (!dollConfig.dollRealPlayerTickAction.getValue()) {
            // Entity tick
            updateActionPack();
        } else {
            // Add Network task
            if (PlayerDollAPI.getServerBranch() == AbsServerBranch.FOLIA) {
                // Run on entityTask as Folia's implementation
                PlayerDollAPI.getScheduler().entityTaskDelayed(this::updateActionPack, getBukkitPlayer(), 1L);
            } else {
                server.schedule(server.wrapRunnable(this::updateActionPack));
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
            Doll.resetPhantomStatistic(getBukkitPlayer());
        }
    }

//    private void shakeOff() {
//        if (getVehicle() instanceof Player) {
//            stopRiding();
//        }
//        for (Entity passenger : getIndirectPassengers()) {
//            if (passenger instanceof Player) {
//                passenger.stopRiding();
//            }
//        }
//    }

    @Override
    public void tick() {
        dollTickCount = Doll.getTickCount(getBukkitPlayer());
        super.tick();
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return dollConfig.dollHostility.getValue() && super.canBeSeenAsEnemy();
    }

    @Override
    public boolean hurtServer(ServerLevel worldserver, DamageSource damageSource, float f) {
        // To fix the knockback in weird way (not projectile nor explosion, but regular damage)
        Vec3 ov = getDeltaMovement(); // original movement before damage
        boolean hurt = super.hurtServer(worldserver,damageSource,f);
        // true for valid attack,
        // false for invalid attack (using shield / invincible)
        if (hurt) {
            Vec3 v = getDeltaMovement(); // processed movement after super.hurt();
            if (ov == v) {
                return true;
            }
            PlayerDollAPI.getScheduler().entityTask(() -> setDeltaMovement(v), getBukkitPlayer());
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
    public void die(DamageSource cause)
    {
        //shakeOff();
        super.die(cause);
        //setHealth(20);
        //this.foodData = new FoodData();
        //dollDisconnect();
    }
    @Override
    public boolean isLocalPlayer() {
        // Fix boat ticking
        return getVehicle() != null;
    }

    @Override
    public boolean hasClientLoaded() {
        return true;
    }
}
