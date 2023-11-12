package me.autobot.playerdoll.Dolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkManager;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractDoll extends ServerPlayer implements IDoll{
    DollConfigManager configManager;
    public String realDollName;
    DollNetworkManager dollNetworkManager;
    public YamlConfiguration dollConfig;
    ServerPlayer player;
    double yaw;
    double pitch;
    boolean noPhantom;
    Monitor monitor;
    EntityPlayerActionPack actionPack;
    public AbstractDoll(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile);//, ClientInformation.createDefault());
        this.player = player;

        getFoliaRegionizedServer();
        setConfigInformation();

        dollNetworkManager = new DollNetworkManager(PacketFlow.CLIENTBOUND);

        spawnToWorld();
        initDoll();

        this.setHealth(20.0f);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0D);
        this.getFoodData().setFoodLevel(20);
        this.getFoodData().setExhaustion(0.0f);
        this.getFoodData().setSaturation(0.0f);
        this.getBukkitEntity().setCollidable(false);
        this.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier

        this.connection.send(new ClientboundSetCarriedItemPacket(this.getInventory().selected));
        this.server.invalidateStatus();

        yaw = player.getRotationVector().y;
        pitch = player.getRotationVector().x;

        this.setPos(player.position());
        this.setPose(player.getPose());
        this.getEntityData().refresh(this);

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);

        setDollLookAt();

        this.actionPack = new EntityPlayerActionPack(this);
        teleportTo();
        createChunkLoader();
    }

    @Override
    public void setConfigInformation() {
        String prefix = PlayerDoll.getDollPrefix();
        if (prefix == null || prefix.isEmpty()) prefix = "BOT-";
        this.realDollName = getGameProfile().getName().substring(prefix.length());
        dollConfig = YAMLManager.getConfig(this.realDollName);
        configManager = new DollConfigManager(dollConfig);
        monitor = new Monitor();
        configManager.addListener(monitor);

        YamlConfiguration globalConfig = YAMLManager.getConfig("config");
        if (globalConfig != null) {
            if (!globalConfig.getBoolean("Global.RestrictSkin")) {
                setDollSkin();
            }
            if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
                Bukkit.setMaxPlayers(Bukkit.getMaxPlayers() + 1);
            }
            this.getBukkitEntity().setSleepingIgnored(globalConfig.getBoolean("Global.DollNotCountSleeping"));
        }
    }

    @Override
    public void initDoll() {
        boolean initial = dollConfig.getBoolean("Initial");
        if (!initial) {
            this.server.getPlayerList().load(this);
        } else {
            dollConfig.set("Initial", false);
        }
    }
    @Override
    public void setDollSkin() {
        if (!Bukkit.getOnlineMode()) {
            return;
        }
        String skinName = dollConfig.getString("SkinData.Name");
        var dollSkinData = dollConfig.getConfigurationSection("SkinData");
        if (dollSkinData != null && (skinName == null || dollSkinData.getString("Name").equalsIgnoreCase(skinName))) {
            String model = "";
            if (dollSkinData.getString("Model").equalsIgnoreCase("slim")) {
                model = """
                              "metadata" : {
                                "model" : "slim"
                              }
                        """;
            }
            String cape = "";
            if (!dollSkinData.getString("Cape").equalsIgnoreCase("")) {
                cape = ",\n    \"CAPE\" : {\n" +
                        "      \"url\" : \""+ new String(Base64.getDecoder().decode(dollSkinData.getString("Cape")), StandardCharsets.UTF_8) +"\"\n" +
                        "    }";
            }
            String jsonData = "{\n" +
                    "  \"timestamp\" : "+ dollSkinData.getString("timestamp") + ",\n" +
                    "  \"profileId\" : \""+ dollSkinData.getString("profileId") +"\",\n" +
                    "  \"profileName\" : \""+ dollSkinData.getString("Name") +"\",\n" +
                    "  \"signatureRequired\" : true,\n" +
                    "  \"textures\" : {\n" +
                    "    \"SKIN\" : {\n" +
                    "      \"url\" : \"" + new String(Base64.getDecoder().decode(dollSkinData.getString("Skin")),StandardCharsets.UTF_8) + "\",\n" +
                    model +
                    "    }" +
                    cape + "\n" +
                    "  }\n" +
                    "}";
            this.getGameProfile().getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8)), dollSkinData.getString("Signature")));
        }
    }
    @Override
    public void teleportTo() {
        this.teleportTo(this.serverLevel(), this.position().x, this.position().y, this.position().z, (float) yaw, (float) pitch);
    }
    @Override
    public void setDollLookAt() {
        Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),()->{
            this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, (byte) yaw));
            this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), (byte) yaw, (byte) pitch, true));
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
        return YAMLManager.getConfig(this.realDollName).getBoolean("setting.Hostility.toggle") && super.canBeSeenAsEnemy();
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
        this.connection.onDisconnect(Component.literal("Disconnected"));
        monitor.property.removeListener(monitor);
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
    public abstract void spawnToWorld();
    public abstract void createChunkLoader();

    public EntityPlayerActionPack getActionPack() {
        return this.actionPack;
    }
    @Override
    public void setNoPhantom(boolean b) {
        this.noPhantom = b;
    }
    @Override
    public String getDollName() {
        return this.realDollName;
    }
    @Override
    public Player getOwner() {
        return Bukkit.getPlayer(UUID.fromString( dollConfig.getString("Owner.UUID")));
    }
    @Override
    public DollConfigManager getConfigManager() {
        return this.configManager;
    }
    class Monitor implements PropertyChangeListener {
        public DollConfigManager property;
        public Monitor() {
            property = AbstractDoll.this.configManager;
            property.addListener(this);
        }

        final Map<String, Consumer<Boolean>> settings = new HashMap<>() {{
            put("setting.Invulnerable", (b) -> AbstractDoll.this.getBukkitEntity().setNoDamageTicks(b ? Integer.MAX_VALUE : 0));
            put("setting.Glow", AbstractDoll.this::setGlowingTag);
            put("setting.Large Step Size", (b) -> AbstractDoll.this.setMaxUpStep(b ? 1.0f : 0.6f));
            put("setting.Pushable", (b) -> AbstractDoll.this.getBukkitEntity().setCollidable(b));
            put("setting.Gravity", (b) -> AbstractDoll.this.setNoGravity(!b));
            put("setting.Phantom", (b) -> AbstractDoll.this.setNoPhantom(!b));
        }};
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (settings.containsKey(evt.getPropertyName())) {
                settings.get(evt.getPropertyName()).accept((Boolean) evt.getNewValue());
            }
        }
    }
}
