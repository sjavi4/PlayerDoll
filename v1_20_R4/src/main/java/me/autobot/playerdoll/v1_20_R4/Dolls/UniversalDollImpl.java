package me.autobot.playerdoll.v1_20_R4.Dolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.Dolls.DollConfig;

import me.autobot.playerdoll.Dolls.IDollLegacy;
import me.autobot.playerdoll.Dolls.IServerDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.CarpetMod.NMSPlayerEntityActionPack;
import me.autobot.playerdoll.v1_20_R4.Network.CursedConnections;
import me.autobot.playerdoll.v1_20_R4.player.ServerPlayerExt;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.UUID;

public class UniversalDollImpl extends ServerPlayer implements IDollLegacy {
    DollConfig dollConfig;
    public Connection serverConnection;
    ServerPlayer player;
    float TPYaw;
    float TPPitch;
    byte PacketYaw;
    byte PacketPitch;
    boolean noPhantom;
    public CommonListenerCookie listenerCookie;
    NMSPlayerEntityActionPack actionPack;
    final Runnable updateActionTask = () -> {
        actionPack.onUpdate();
    };
    int dollTickCount = -1;
    public static IDollLegacy callSpawn(String name, UUID uuid) {
        MinecraftServer server = CursedConnections.server;
        ServerLevel serverLevel = server.overworld();
        GameProfile profile = new GameProfile(uuid,name);
        return new UniversalDollImpl(server,serverLevel,profile);
    }
    public UniversalDollImpl(ServerPlayer realPlayer) {
        super(realPlayer.server, realPlayer.serverLevel(), realPlayer.getGameProfile(), realPlayer.clientInformation());
    }
    public UniversalDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile, ClientInformation.createDefault());
    }


    public void setup(Player caller) {
        this.player = caller == null ? this : ((CraftPlayer)caller).getHandle();

        //Bukkit.getPluginManager().callEvent(new DollJoinEvent(this.getBukkitEntity(), player == null ? null : player.getBukkitEntity(), this));
        //this.dollConfig = DollConfig.getOnlineDollConfig(this.uuid);

        this.unsetRemoved();

        TPYaw = this.player.getRotationVector().y;
        TPPitch = this.player.getRotationVector().x;
        Location loc = this.player.getBukkitEntity().getLocation();
        PacketYaw = (byte) ((loc.getYaw() % 360) * 256 / 360);
        PacketPitch = (byte) ((loc.getPitch() % 360) * 256 / 360);

        //this.setPos(this.player.position());
        this.setPose(this.player.getPose());

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
        //setDollLookAt();
        this.listenerCookie = CommonListenerCookie.createInitial(getGameProfile());


        this.actionPack = new NMSPlayerEntityActionPack(this);

        IDollLegacy.setSkin(this.getBukkitEntity(), this);
    }

    protected void sendPacket(Packet<?> packet) {
        this.server.getPlayerList().broadcastAll(packet);
    }
    @Override
    public void setDollSkin(String property, String signature) {
        this.getGameProfile().getProperties().put("textures", new Property("textures", property, signature));
    }
    @Override
    public void tick() {
        if (PlayerDoll.isFolia) {
            dollTickCount = PlayerDoll.getFoliaHelper().getTick();
        } else {
            dollTickCount = this.server.getTickCount();
        }
        try {
            if (!dollConfig.dollRealPlayerTickAction.getValue()) {
                updateActionTask.run();
            } else {
                if (PlayerDoll.isFolia) {
                    PlayerDoll.getFoliaHelper().addTask(updateActionTask);
                    //PlayerDoll.getFoliaHelper().entityTask(this.getBukkitEntity(),updateActionTask,1);
                } else {
                    server.tell(server.wrapRunnable(updateActionTask));
                }
            }
            super.tick();
            if (!dollConfig.dollRealPlayerTickUpdate.getValue()) {
                this.doTick();
            }
            if (dollTickCount % 10 == 0) {
                connection.resetPosition();
                this.serverLevel().getChunkSource().move(this);
                if (noPhantom) IDollLegacy.resetPhantomStatistic(this.getBukkitEntity());
            }
        } catch (NullPointerException ignored) {
            //System.out.println(ignored);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return IDollLegacy.executeHurt(this,getBukkitEntity(),super.hurt(damageSource,f));
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
        //this.saveFlags();
        this.setHealth(20.0f);
        super.disconnect();
        sendPacket(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(this.getUUID())));
        /*
        Runnable kickTask = () -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "kick " + this.getBukkitEntity().getName());

        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().globalTask(kickTask);
        } else {
            kickTask.run();
        }

         */
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0, y, 0.0, onGround);
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


    public NMSPlayerEntityActionPack getActionPack() {
        return this.actionPack;
    }
    @Override
    public void setNoPhantom(boolean b) {
        this.noPhantom = b;
    }
    @Override
    public boolean getNoPhantom() {
        return noPhantom;
    }
    @Override
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(UUID.fromString(dollConfig.ownerUUID.getValue()));
        //return Bukkit.getOfflinePlayer(UUID.fromString(configManager.config.getString("Owner.UUID")));
    }


    @Override
    public void _kill() {
        kill();
    }
    @Override
    public void _disconnect() {
        disconnect();
    }
    @Override
    public void _setPos(double x, double y, double z) {
        setPos(x,y,z);
    }
    @Override
    public void _setMaxUpStep(float h) {
        this.setMaxUpStep(h);
    }
    @Override
    public DollConfig getDollConfig() {
        return dollConfig;
    }
    @Override
    public boolean getHurtMarked() {
        return this.hurtMarked;
    }
    @Override
    public void setHurtMarked(boolean b) {
        this.hurtMarked = b;
    }
    @Override
    public void _resetLastActionTime() {
        this.resetLastActionTime();
    }

    @Override
    public void _resetAttackStrengthTicker() {
        this.resetAttackStrengthTicker();
    }

    @Override
    public void _setJumping(boolean b) {
        this.setJumping(b);
    }

    @Override
    public void _jumpFromGround() {
        this.jumpFromGround();
    }

    @Override
    public Player getBukkitPlayer() {
        return this.getBukkitEntity();
    }

    @Override
    public Player getCaller() {
        return this.player.getBukkitEntity();
    }

    @Override
    public void setDollConfig(DollConfig config) {
        this.dollConfig = config;
    }

}
