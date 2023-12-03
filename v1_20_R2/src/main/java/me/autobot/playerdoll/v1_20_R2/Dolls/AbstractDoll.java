package me.autobot.playerdoll.v1_20_R2.Dolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.Dolls.DollSettingMonitor;
import me.autobot.playerdoll.v1_20_R2.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public abstract class AbstractDoll extends ServerPlayer implements IDoll {
    DollConfigManager configManager;
    DollNetworkManager dollNetworkManager;
    ServerPlayer player;
    float TPYaw;
    float TPPitch;
    byte PacketYaw;
    byte PacketPitch;
    boolean noPhantom;
    DollSettingMonitor dollSettingMonitor;
    EntityPlayerActionPack actionPack;

    public static IDoll callSpawn(Object _player, String name) {
        ServerPlayer player = (ServerPlayer) _player;
        MinecraftServer server;
        ServerLevel serverLevel;
        if (player == null) {
            server = MinecraftServer.getServer();
            serverLevel = server.overworld();
        } else {
            server = player.server;
            serverLevel = player.serverLevel();
        }
        GameProfile profile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(name),name);
        if (PlayerDoll.isSpigot) return new SpigotDollImpl(server, serverLevel, profile, player);
        else if (PlayerDoll.isPaperSeries) return new PaperDollImpl(server, serverLevel, profile, player);
        else if (PlayerDoll.isFolia) return new FoliaDollImpl(server, serverLevel, profile, player);
        return null;
    }
    public AbstractDoll(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, ClientInformation.createDefault());

        this.player = player == null ? this : player;

        getFoliaRegionizedServer();
        //setConfigInformation();

        IDoll.setConfigInformation(this.getBukkitEntity());
        configManager = DollConfigManager.dollConfigManagerMap.get(this.getBukkitEntity());
        dollSettingMonitor = new DollSettingMonitor(this.getBukkitEntity(),this);
        configManager.addListener(dollSettingMonitor);

        if (IDoll.canSetSkin()) IDoll.setSkin(this.getBukkitEntity(),this);
        dollNetworkManager = new DollNetworkManager(PacketFlow.CLIENTBOUND);

        //IDoll.initialDoll(this.configManager,this.stringUUID);
        //initDoll();
        spawnToWorld();

/*
        this.setHealth(20.0f);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0D);
        this.getFoodData().setFoodLevel(20);
        this.getFoodData().setExhaustion(0.0f);
        this.getFoodData().setSaturation(0.0f);
        this.getBukkitEntity().setCollidable(false);
        this.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier


 */
        this.unsetRemoved();
        //this.connection.send(new ClientboundSetCarriedItemPacket(this.getInventory().selected));
        //this.server.invalidateStatus();

        TPYaw = this.player.getRotationVector().y;
        TPPitch = this.player.getRotationVector().x;
        PacketYaw = (byte) ((this.player.getBukkitEntity().getLocation().getYaw() % 360) * 256 / 360);
        PacketPitch = (byte) ((this.player.getBukkitEntity().getLocation().getPitch() % 360) * 256 / 360);


        this.setPos(this.player.position());
        this.setPose(this.player.getPose());

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
        //this.getEntityData().refresh(this);

        setDollLookAt();

        this.actionPack = new EntityPlayerActionPack(this);

        teleportTo();
    }
    public void spawnToWorld() {
        this.server.getPlayerList().placeNewPlayer(this.dollNetworkManager,this, CommonListenerCookie.createInitial(this.getGameProfile()));
    }
    @Override
    public void setDollSkin(String property, String signature) {
        this.getGameProfile().getProperties().put("textures", new Property("textures", property, signature));
    }
    @Override
    public void teleportTo() {
        this.teleportTo(player.serverLevel(), player.position().x, player.position().y, player.position().z, TPYaw, TPPitch);
    }
    @Override
    public void setDollLookAt() {
        Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),()->{
            this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, PacketYaw));
            this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), PacketYaw, PacketPitch, true));
        },2);
    }

    @Override
    public void tick() {
        try {
            this.actionPack.onUpdate();
            super.tick();
            this.doTick();
        } catch (NullPointerException ignored) {}
    }

    public boolean checkBlocking(DamageSource damagesource) {
        Entity entity = damagesource.getDirectEntity();
        boolean flag = false;
        if (entity instanceof Arrow entityarrow) {
            if (entityarrow.getPierceLevel() > 0) {
                flag = true;
            }
        }
        if (this.isBlocking() && !flag) {
            Vec3 vec3d = damagesource.getSourcePosition();
            if (vec3d != null) {
                Vec3 vec3d1 = this.getViewVector(1.0F);
                Vec3 vec3d2 = vec3d.vectorTo(this.getDeltaMovement()).normalize();
                vec3d2 = new Vec3(vec3d2.x, 0.0D, vec3d2.z);
                return vec3d2.dot(vec3d1) < 0.0D;
            }
        }
        return false;
    }
    @Override
    public boolean canBeSeenAsEnemy() {
        return (boolean)configManager.getData().get("setting.Hostility") && super.canBeSeenAsEnemy();
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
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(this.getUUID())));
        //dollSettingMonitor.property.removeListener(dollSettingMonitor);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0, y, 0.0, onGround);
    }

    @Override
    public void setPortalCooldown() {
        super.setPortalCooldown();
        this.changeDimension(this.serverLevel());
    }
    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        if (wonGame) {
            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
            connection.handleClientCommand(p);
        }
        if (connection.player.isChangingDimension()) {
            connection.player.hasChangedDimension();
        }
        return this;
    }
    public abstract void getFoliaRegionizedServer();

    public EntityPlayerActionPack getActionPack() {
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
        return Bukkit.getOfflinePlayer(UUID.fromString((String) configManager.getData().get("Owner.UUID")));
    }
    @Override
    public boolean _isCrouching() {
        return isCrouching();
    }
    @Override
    public boolean _isSprinting() {
        return isSprinting();
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
    public DollConfigManager getConfigManager() {
        return this.configManager;
    }
}
