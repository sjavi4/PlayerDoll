package me.autobot.playerdoll.Dolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkHandler;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkManager;
import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class DollManager extends ServerPlayer {
    private static final boolean onlinemode = Bukkit.getOnlineMode();

    public ChunkPos dollChunkPos;
    ServerPlayer owner;
    ServerPlayer serverPlayer;
    ServerLevel serverLevel;
    MinecraftServer minecraftServer;
    UUID dollUUID;
    DollNetworkHandler dollNetworkHandler;
    DollNetworkManager dollNetworkManager;
    public YamlConfiguration dollConfig;
    String dollName;
    String dollSkin;
    GameProfile dollProfile;
    EntityPlayerActionPack actionPack;
    DollManager serverPlayerDoll;
    DollConfigManager configManager;
    Monitor monitor;

    boolean noPhantom;
    final boolean isSpigot = PlayerDoll.isSpigot;
    final boolean isPaperSeries = PlayerDoll.isPaperSeries;
    final boolean isFolia = PlayerDoll.isFolia;

    Class<?> regionizedServerFolia;

    public DollManager(MinecraftServer server, ServerLevel level, GameProfile profile, ServerPlayer player) {
        super(server, level, profile,ClientInformation.createDefault());
        this.minecraftServer = server;
        this.serverLevel = level;
        this.dollProfile = profile;
        this.dollUUID = profile.getId();

        String prefix = PlayerDoll.getDollPrefix();
        if (prefix == null || prefix.isEmpty()) prefix = "BOT-";
        this.dollName = profile.getName().substring(prefix.length());
        //this.dollName = profile.getName().substring(this.length());
        //this.dollSkin = skinName;


        this.serverPlayer = player;

        this.spawnIn(this.serverLevel);
        //this.gameMode.setLevel((ServerLevel)this.level());


        dollConfig = YAMLManager.getConfig(this.dollName);

        this.owner = (ServerPlayer) serverLevel().getPlayerByUUID(UUID.fromString( dollConfig.getString("Owner.UUID")));
        boolean initial = dollConfig.getBoolean("Initial");
        if (!initial) {
            this.server.getPlayerList().load(this);
        } else {
            dollConfig.set("Initial", false);
        }

        if (isFolia) {
            try {
                regionizedServerFolia = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        //YamlConfiguration dollConfig = YAMLManager.getConfig(this.dollName);
        //this.owner = (ServerPlayer) serverLevel.getPlayerByUUID(UUID.fromString(dollConfig.getString("Owner")));


        this.serverPlayerDoll = this;

        double yaw = player.getRotationVector().y;
        double pitch = player.getRotationVector().x;


        configManager = new DollConfigManager(dollConfig);
        monitor = new Monitor();
        configManager.addListener(monitor);

        var globalConfig = YAMLManager.getConfig("config");
        if (!globalConfig.getBoolean("RestrictSkin")) {
            setDollSkin();
        }
        if (globalConfig.getBoolean("DollNotCountSleeping")) {
            this.getBukkitEntity().setSleepingIgnored(true);
        }

        dollNetworkManager = new DollNetworkManager(PacketFlow.CLIENTBOUND);
        dollNetworkManager.setPlayer(this);
        //connection = new DollNetworkHandler(server, dollNetworkManager, this);
        //connection = new ServerGamePacketListenerImpl(server,dollNetworkManager,this);


        this.serverPlayerDoll.updateServerPlayerList();

        if (!isFolia) {
            dollNetworkHandler = new DollNetworkHandler(server,dollNetworkManager,this);
            connection = dollNetworkHandler;
            this.server.getPlayerList().respawn(this, serverLevel, true, serverPlayer.getBukkitEntity().getLocation(), true, PlayerRespawnEvent.RespawnReason.PLUGIN);
        } else {
            try {
                Method placeNewPlayerFolia = this.server.getPlayerList().getClass().getMethod("placeNewPlayer", Connection.class, ServerPlayer.class, CompoundTag.class, String.class, Location.class);
                placeNewPlayerFolia.invoke(this.server.getPlayerList(), dollNetworkManager, this, this.server.getPlayerList().load(this), this.getGameProfile().getName(), serverPlayer.getBukkitEntity().getLocation());

            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
                System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            }

        }
        //serverLevel.getChunkSource().chunkMap.getDistanceManager().addPlayer(this.getLastSectionPos(),this);
        connection.send(new ClientboundLevelChunkWithLightPacket(new EmptyLevelChunk(serverLevel,this.chunkPosition(), serverLevel().getBiome(this.getOnPos())), serverLevel.getLightEngine(), null, null ));
        this.serverPlayerDoll.setHealth(20.0f);
        this.serverPlayerDoll.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.serverPlayerDoll.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0D);

        this.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier

        //Paper Chunk Loading
        if (isPaperSeries || isFolia) {
            try {
                this.getClass().getField("isRealPlayer").setBoolean(this,true);
                Object playerChunkLoader = serverLevel.getClass().getField("playerChunkLoader").get(serverLevel);
                playerChunkLoader.getClass().getDeclaredMethod("addPlayer", ServerPlayer.class).invoke(playerChunkLoader,this);
            } catch (NoSuchMethodException | NoSuchFieldException ignored) {
            } catch (InvocationTargetException | IllegalAccessException e) {
                System.out.println("Exception while invoking Paper's playerChunkLoader");
            }
        }


        connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
        connection.send(new ClientboundSetCarriedItemPacket(this.getInventory().selected));
        this.server.invalidateStatus();


        //Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(this.getBukkitEntity(), null));
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }

        if (!isFolia) {
            this.serverPlayerDoll.teleportTo(serverLevel, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, (float) yaw, (float) pitch);
        } else {
            try {
                Method teleportToFolia = this.getClass().getMethod("teleportTo", ServerLevel.class, double.class, double.class, double.class, Set.class, float.class, float.class, PlayerTeleportEvent.TeleportCause.class);
                var pos = this.position();
                teleportToFolia.invoke(this, serverLevel, pos.x, pos.y, pos.z, Set.of(), (float) yaw, (float) pitch, PlayerTeleportEvent.TeleportCause.PLUGIN);

            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        this.serverPlayerDoll.setPos(serverPlayer.position());
        this.serverPlayerDoll.setPose(serverPlayer.getPose());
        this.dollChunkPos = this.serverPlayerDoll.chunkPosition();

        serverPlayerDoll.getEntityData().refresh(serverPlayerDoll);


        //this.craftPlayerDoll.setNoDamageTicks(0);
        this.setHealth(20.0f);
        this.getFoodData().setFoodLevel(20);
        this.getFoodData().setExhaustion(0.0f);
        this.getFoodData().setSaturation(0.0f);
        this.getBukkitEntity().setCollidable(false);
        //this.setMaxUpStep(0.6f);
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);

        this.serverPlayerDoll.setDollLookAt();

        this.actionPack = new EntityPlayerActionPack(serverPlayerDoll);

    }

    private void setDollSkin() {
        if (!onlinemode) {
            return;
        }
        String skinName = dollSkin;
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
            dollProfile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8)), dollSkinData.getString("Signature")));
        }

    }

    public void setDollLookAt() {
        //craftPlayerDoll.setRotation(serverPlayer.getBukkitEntity().getLocation().getYaw(), serverPlayer.getBukkitEntity().getLocation().getPitch());

        float yaw = ((serverPlayer.getBukkitEntity().getLocation().getYaw() % 360) * 256 / 360);
        float pitch = ((serverPlayer.getBukkitEntity().getLocation().getPitch() % 360) * 256 / 360);

        Runnable r = () -> {
            this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, (byte) yaw));
            this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), (byte) yaw, (byte) pitch, true));
        };
        if (!isFolia) {
            this.server.tell(new TickTask(this.server.getTickCount() + 2, r));
        } else {
            try {
                long tick = getCurrentTickFolia();
                Object rServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
                regionizedServerFolia.getMethod("addTask", Runnable.class).invoke(rServer, new TickTask(Math.toIntExact(tick) + 2, r));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateServerPlayerList() {
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
        //this.server.getPlayerList().broadcastAll(new ClientboundAddPlayerPacket(this));
    }


    @Override
    public void tick() {
        if (!isFolia) {
            if (this.getServer().getTickCount() % 10 == 0) {
                connection.resetPosition();
                this.serverLevel().getChunkSource().move(this);
                if (noPhantom) this.getBukkitEntity().setStatistic(Statistic.TIME_SINCE_REST,0);
            }
        } else {
            long tickCount = getCurrentTickFolia();
            if (tickCount % 10 == 0) {
                connection.resetPosition();
                this.serverLevel().getChunkSource().move(this);
                if (noPhantom) this.getBukkitEntity().setStatistic(Statistic.TIME_SINCE_REST,0);
            }
        }
        try {
            this.actionPack.onUpdate();
            super.tick();
            this.doTick();
            /*
            if (serverLevel != this.serverLevel()) {
                serverLevel = this.serverLevel();
                if (isFolia) {
                    try {
                        Method teleportAsync = this.getBukkitEntity().getClass().getMethod("teleportAsync", Location.class);
                        teleportAsync.invoke(this.getBukkitEntity(),this.getBukkitEntity().getLocation());
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    //FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), () -> this.changeDimension(serverLevel))
                } else this.changeDimension(serverLevel);
            }

             */
        } catch (NullPointerException ignored) {
        }
    }
/*
    public void dollRespawn() {
        connection.onDisconnect(Component.literal("Teleport"));
        new DollManager(serverPlayer.getServer(), serverPlayer.serverLevel(), this.dollProfile, this.serverPlayer);
    }


 */
    private long getCurrentTickFolia() {
        try {
            return (long) regionizedServerFolia.getMethod("getCurrentTick").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getIpAddress() {
        return "127.0.0.1";
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
                if (!isFolia) {
                    this.server.tell(new TickTask(this.server.getTickCount() + 1, r));
                } else {
                    FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), r);
                }
            }
        }
        return damaged;
    }


    private boolean checkBlocking(DamageSource damagesource) {

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
        return YAMLManager.getConfig(this.dollName).getBoolean("setting.Hostility.toggle") && super.canBeSeenAsEnemy();
    }

    public void foliaDisconnect(boolean remove) {
        if (remove) {
            FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), this::kill);
        } else {
            FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), ()->this.connection.disconnect(Component.literal("despawn")));
        }
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
        if (!isFolia) {
            serverLevel.removePlayerImmediately(this, RemovalReason.DISCARDED);
            connection.onDisconnect(Component.literal("Disconnected"));
        } else {
            //disconnectSafely
            //serverLevel.removePlayerImmediately(this, RemovalReason.DISCARDED);
            connection.onDisconnect(Component.literal("Disconnected"));
            //connection.processedDisconnect = false;
        }
        monitor.property.removeListener(monitor);
        //configManager.removeListener(monitor);
        if (isPaperSeries) {
            try {
                Object playerChunkLoader = serverLevel.getClass().getField("playerChunkLoader").get(serverLevel);
                playerChunkLoader.getClass().getDeclaredMethod("removePlayer", ServerPlayer.class).invoke(playerChunkLoader, this);
            } catch (NoSuchMethodException | NoSuchFieldException ignored) {
            } catch (InvocationTargetException | IllegalAccessException e) {
                System.out.println("Exception while invoking Paper's playerChunkLoader");
                throw new RuntimeException(e);
            }
        }
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

        //super.changeDimension(serverLevel);
        if (wonGame) {
            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
            connection.handleClientCommand(p);
        }
        if (connection.player.isChangingDimension()) {
            //serverLevel.getChunkSource().removeRegionTicket(TicketType.PLAYER, this.dollChunkPos, Bukkit.getViewDistance(), this.dollChunkPos);
            connection.player.hasChangedDimension();
        }
        return this;
    }

    public EntityPlayerActionPack getActionPack() {
        return this.actionPack;
    }

    public String getDollName() {
        return this.dollName;
    }
    public Player getOwner() {return this.owner.getBukkitEntity();}
    public DollConfigManager getConfigManager() {return this.configManager;}
    public void setNoPhantom(boolean b) {
        this.noPhantom = b;
    }

    class Monitor implements PropertyChangeListener {
        private DollConfigManager property;
        public Monitor() {
            property = DollManager.this.configManager;
            property.addListener(this);
        }
        final Map<String, Consumer<Boolean>> settings = new HashMap<>() {{
            put("setting.Invulnerable", (b)->serverPlayerDoll.getBukkitEntity().setNoDamageTicks(b?Integer.MAX_VALUE:0));
            put("setting.Glow", (b)->serverPlayerDoll.setGlowingTag(b));
            put("setting.Large Step Size", (b)->serverPlayerDoll.setMaxUpStep(b?1.0f:0.6f));
            put("setting.Pushable", (b)->serverPlayerDoll.getBukkitEntity().setCollidable(b));
            put("setting.Gravity", (b)->serverPlayerDoll.setNoGravity(!b));
            put("setting.Phantom", (b)->serverPlayerDoll.setNoPhantom(!b));
        }};
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            //Trigger when changes
            //settings.get(evt.getPropertyName()).accept((Boolean) evt.getNewValue());
            //System.out.println(settings.keySet());
            if (settings.containsKey(evt.getPropertyName())) {
                settings.get(evt.getPropertyName()).accept((Boolean) evt.getNewValue());
            }
        }
    }
}
