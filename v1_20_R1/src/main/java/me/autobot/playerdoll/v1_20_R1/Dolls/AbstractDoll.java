package me.autobot.playerdoll.v1_20_R1.Dolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.Dolls.DollSettingMonitor;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R1.CarpetMod.NMSPlayerEntityActionPack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    NMSPlayerEntityActionPack actionPack;
    static long foliaTickCount = -1;
    static int nonFoliaTickCount = -1;
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
        super(minecraftserver, worldserver, gameprofile);

        this.player = player == null ? this : player;

        //setConfigInformation();

        IDoll.setConfigInformation(this.getBukkitEntity());
        configManager = DollConfigManager.dollConfigManagerMap.get(this.getBukkitEntity());
        dollSettingMonitor = new DollSettingMonitor(this.getBukkitEntity(),this);
        configManager.addListener(dollSettingMonitor);

        if (IDoll.canSetSkin()) IDoll.setSkin(this.getBukkitEntity(),this);

        dollNetworkManager = new DollNetworkManager(PacketFlow.SERVERBOUND);

        spawnToWorld();

        this.unsetRemoved();
        //this.connection.send(new ClientboundSetCarriedItemPacket(this.getInventory().selected));
        //this.server.invalidateStatus();

        TPYaw = this.player.getRotationVector().y;
        TPPitch = this.player.getRotationVector().x;
        Location loc = this.player.getBukkitEntity().getLocation();
        PacketYaw = (byte) ((loc.getYaw() % 360) * 256 / 360);
        PacketPitch = (byte) ((loc.getPitch() % 360) * 256 / 360);

        this.setPos(this.player.position());
        this.setPose(this.player.getPose());

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
        //this.getEntityData().refresh(this);

        setDollLookAt();

        this.actionPack = new NMSPlayerEntityActionPack(this);

        teleportTo();

    }
    public void spawnToWorld() {
        this.server.getPlayerList().placeNewPlayer(this.dollNetworkManager,this);
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
            sendPacket(new ClientboundRotateHeadPacket(this, PacketYaw));
            sendPacket(new ClientboundMoveEntityPacket.Rot(this.getId(), PacketYaw, PacketPitch, true));
            //this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, PacketYaw));
            //this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), PacketYaw, PacketPitch, true));
        },2);
    }

    @Override
    public void tick() {
        try {
            this.actionPack.onUpdate();
            super.tick();
            this.doTick();
            if (checkTick()) {
                connection.resetPosition();
                this.serverLevel().getChunkSource().move(this);
                if (noPhantom) IDoll.resetPhantomStatistic(this.getBukkitEntity());
            }
        } catch (NullPointerException ignored) {}
    }
    private boolean checkTick() {
        return (foliaTickCount % 10 == 0) || (nonFoliaTickCount % 10 == 0);
    }
    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return IDoll.executeHurt(this,getBukkitEntity(),super.hurt(damageSource,f));
    }
    @Override
    public boolean canBeSeenAsEnemy() {
        return (boolean)configManager.getDollSetting().get("hostility") && super.canBeSeenAsEnemy();
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
        //this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(this.getUUID())));
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
        return connection.player;
    }
    protected void sendPacket(Packet<?> packet) {
        this.server.getPlayerList().broadcastAll(packet);
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
        return Bukkit.getOfflinePlayer(UUID.fromString(configManager.config.getString("Owner.UUID")));
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

}
