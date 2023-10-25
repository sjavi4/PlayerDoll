package me.autobot.playerdoll.Dolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkHandler;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkManager;
import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class DollManager extends ServerPlayer {
    private static final boolean onlinemode = Bukkit.getOnlineMode();

    public ChunkPos dollChunkPos;

    //public boolean enableInventory = true;
    //public boolean enableEnderChest = true;
    //public boolean enableHostility = true;

    ServerPlayer owner;
    ServerPlayer serverPlayer;
    ServerLevel serverLevel;
    MinecraftServer minecraftServer;
    UUID dollUUID;
    public YamlConfiguration dollConfig;
    String dollName;
    String dollSkin;
    GameProfile dollProfile;
    EntityPlayerActionPack actionPack;
    DollManager serverPlayerDoll;
    DollConfigManager configManager;
    Monitor monitor;

    public DollManager(MinecraftServer server, ServerLevel level, GameProfile profile, ServerPlayer player) {
        super(server, level, profile, ClientInformation.createDefault());
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


        //YamlConfiguration dollConfig = YAMLManager.getConfig(this.dollName);
        //this.owner = (ServerPlayer) serverLevel.getPlayerByUUID(UUID.fromString(dollConfig.getString("Owner")));



        //if (!dollConfig.getBoolean("Remove")) {
        //    this.server.getPlayerList().load(this);
        //} else {
        //    dollConfig.set("Remove", false);
        //}

        this.serverPlayerDoll = this;

        double yaw = player.getRotationVector().y;
        double pitch = player.getRotationVector().x;


        configManager = new DollConfigManager(dollConfig);
        monitor = new Monitor();
        configManager.addListener(monitor);

        /*
        if (!ReflectionHelper.getRestrictSkin()) {
            setDollSkin();
        }

         */
        DollNetworkManager dollNetworkManager = new DollNetworkManager(PacketFlow.CLIENTBOUND);
        this.serverPlayerDoll.connection = new DollNetworkHandler(minecraftServer, dollNetworkManager, serverPlayerDoll);


        this.connection.send(new ClientboundLevelChunkWithLightPacket(new EmptyLevelChunk(serverLevel,this.chunkPosition(), serverLevel().getBiome(this.getOnPos())), serverLevel.getLightEngine(), null, null ));

        this.serverPlayerDoll.setHealth(20.0f);
        this.serverPlayerDoll.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.serverPlayerDoll.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0D);

        this.getBukkitEntity().setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier
        //this.getBukkitEntity().setMetadata("DOLL", new FixedMetadataValue(PlayerDoll.getPlugin(), "DOLL")); //Doll identifier



        this.serverPlayerDoll.updateServerPlayerList();

        this.server.getPlayerList().respawn(this, serverLevel, true, serverPlayer.getBukkitEntity().getLocation(), true, PlayerRespawnEvent.RespawnReason.PLUGIN);

        //serverLevel.getChunkSource().chunkMap.getDistanceManager().addPlayer(this.getLastSectionPos(),this);

        //Paper Chunk Loading
        try {
            this.getClass().getField("isRealPlayer").setBoolean(this,true);
            Object playerChunkLoader = serverLevel.getClass().getField("playerChunkLoader").get(serverLevel);
            playerChunkLoader.getClass().getDeclaredMethod("addPlayer", ServerPlayer.class).invoke(playerChunkLoader,this);
        } catch (NoSuchMethodException | NoSuchFieldException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception while invoking Paper's playerChunkLoader");
        }

        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
        this.connection.send(new ClientboundSetCarriedItemPacket(this.getInventory().selected));
        this.server.invalidateStatus();


        Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(this.getBukkitEntity(), null));


        this.serverPlayerDoll.teleportTo(serverLevel, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, (float) yaw, (float) pitch);
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

        //this.loadFlags();
        //this.serverLevel.getChunkSource().addRegionTicket(TicketType.PLAYER, this.chunkPosition(), Bukkit.getViewDistance(), this.chunkPosition());

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
        /*
        skinName = dollSkin == null ? owner.displayName : dollSkin;
        try {
            URL url_playerName = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName);
            String uuid = JsonParser.parseReader(new InputStreamReader(url_playerName.openStream())).getAsJsonObject().get("id").getAsString();

            URL url_skinTexture = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            JsonObject textureProperty = JsonParser.parseReader(new InputStreamReader(url_skinTexture.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            JsonObject profile = JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(texture)))).getAsJsonObject();
            JsonObject profileTexture = profile.getAsJsonObject("textures");
            JsonObject profileSkin = profileTexture.getAsJsonObject("SKIN");


            String skinImage = profileSkin.get("url").getAsString();
            String model = profileSkin.has("metadata")? "slim" : "";
            String capeImage = profileTexture.has("CAPE") ? profileTexture.getAsJsonObject("CAPE").get("url").getAsString() : null;
            String profileId = profile.get("profileId").getAsString();
            String timestamp = profile.get("timestamp").getAsString();

            dollProfile.getProperties().put("textures", new Property("textures", texture, signature));
            //dollProfile.getProperties().put("textures", new Property("textures", texture, null));
            skinName = profile.get("profileName").getAsString();
            skinData.put("Name",skinName);
            skinData.put("Skin",Base64.getEncoder().encodeToString(skinImage.getBytes(StandardCharsets.UTF_8)));
            if (capeImage != null) {
                skinData.put("Cape", Base64.getEncoder().encodeToString(capeImage.getBytes(StandardCharsets.UTF_8)));
            } else {
                skinData.put("Cape", "");
            }
            skinData.put("Model",model);
            skinData.put("Signature",signature);
            skinData.put("profileId",profileId);
            skinData.put("timestamp",timestamp);
            this.dollSkin = skinName;
            dollConfig.set("SkinData",skinData);

        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
        }

         */

    }

    public void setDollLookAt() {
        //craftPlayerDoll.setRotation(serverPlayer.getBukkitEntity().getLocation().getYaw(), serverPlayer.getBukkitEntity().getLocation().getPitch());

        float yaw = ((serverPlayer.getBukkitEntity().getLocation().getYaw() % 360) * 256 / 360);
        float pitch = ((serverPlayer.getBukkitEntity().getLocation().getPitch() % 360) * 256 / 360);
        this.server.tell(new TickTask(this.server.getTickCount()+2, ()->{
            this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, (byte) yaw));
            this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), (byte) yaw, (byte) pitch, true));
        }));
    }

    private void updateServerPlayerList() {
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
        //this.server.getPlayerList().broadcastAll(new ClientboundAddPlayerPacket(this));
    }


    @Override
    public void tick() {
        if (this.getServer().getTickCount() % 10 == 0) {
            this.connection.resetPosition();
            this.serverLevel().getChunkSource().move(this);
        }
        try {
            this.actionPack.onUpdate();
            super.tick();
            this.doTick();
            if (this.serverPlayerDoll.chunkPosition() != this.dollChunkPos) {
                //this.serverLevel.getChunkSource().removeRegionTicket(TicketType.PLAYER, this.dollChunkPos, Bukkit.getViewDistance(), this.dollChunkPos);
                this.dollChunkPos = this.chunkPosition();
                //this.serverLevel.getChunkSource().addRegionTicket(TicketType.PLAYER, this.dollChunkPos, Bukkit.getViewDistance(), this.dollChunkPos);
            }
            if (serverLevel != this.serverLevel()) {
                serverLevel = this.serverLevel();
                this.changeDimension(serverLevel);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public void dollRespawn() {
        this.connection.onDisconnect(Component.literal("Teleport"));
        new DollManager(serverPlayer.getServer(), serverPlayer.serverLevel(), this.dollProfile, this.serverPlayer);
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

                try {
                    Class<?> clazz = Class.forName("me.autobot.playerdoll.PlayerDoll");
                    Plugin plugin = (Plugin) clazz.getMethod("getPlugin").invoke(clazz);
                    Bukkit.getScheduler().runTask(plugin, () -> DollManager.this.hurtMarked = true);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
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
        return super.canBeSeenAsEnemy();
        //return YAMLManager.getConfig(this.dollName).getBoolean("setting.Hostility.toggle") && super.canBeSeenAsEnemy();
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
        serverLevel.removePlayerImmediately(this, RemovalReason.DISCARDED);
        this.connection.onDisconnect(Component.literal("Disconnected"));
        monitor.property.removeListener(monitor);
        //configManager.removeListener(monitor);
        try {
            Object playerChunkLoader = serverLevel.getClass().getField("playerChunkLoader").get(serverLevel);
            playerChunkLoader.getClass().getDeclaredMethod("removePlayer", ServerPlayer.class).invoke(playerChunkLoader,this);
        } catch (NoSuchMethodException | NoSuchFieldException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception while invoking Paper's playerChunkLoader");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0,y,0.0,onGround);
    }

    @Override
    public Entity changeDimension(ServerLevel serverLevel) {

        super.changeDimension(serverLevel);
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
    public DollConfigManager getConfigManager() {return this.configManager;};

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
