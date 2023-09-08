package me.autobot.playerdoll.Dolls;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkHandler;
import me.autobot.playerdoll.Dolls.Networks.DollNetworkManager;
import me.autobot.playerdoll.GUI.Menus.Inventories.*;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class DollManager extends ServerPlayer {
    private static final boolean onlinemode = Bukkit.getOnlineMode();
    public ChunkPos dollChunkPos;
    public boolean enableChunkLoad = true;
    //public boolean enableInventory = true;
    public boolean enableEnderChest = true;
    public boolean enableHostility = true;
    private int chunkLoadSize = YAMLManager.getConfig("config").getInt("ChunkLoadArea");
    CraftPlayer craftOwner;
    ServerPlayer owner;
    ServerPlayer serverPlayer;
    ServerLevel serverLevel;
    MinecraftServer minecraftServer;
    UUID dollUUID;
    String dollName;
    String dollSkin;
    GameProfile dollProfile;
    EntityPlayerActionPack actionPack;
    DollManager serverPlayerDoll;
    CraftPlayer craftPlayerDoll;

    public DollManager(MinecraftServer server, ServerLevel level, GameProfile profile, ServerPlayer player, String skinName) {
        super(server, level, profile);
        this.minecraftServer = server;
        this.serverLevel = level;
        this.dollProfile = profile;
        this.dollUUID = profile.getId();
        this.dollName = profile.getName().substring(PlayerDoll.getDollPrefix().length());
        this.dollSkin = skinName;

        this.serverPlayer = player;


        YamlConfiguration dollConfig = YAMLManager.getConfig(this.dollName);
        this.owner = (ServerPlayer) serverLevel.getPlayerByUUID(UUID.fromString(dollConfig.getString("Owner")));
        this.craftOwner = this.owner.getBukkitEntity();


        if (!dollConfig.getBoolean("Remove")) {
            this.server.getPlayerList().load(this);
        } else {
            dollConfig.set("Remove", false);
        }

        if (this.chunkLoadSize == -1) {
            this.chunkLoadSize = Bukkit.getViewDistance()+1;
        }

        this.serverPlayerDoll = this;
        this.craftPlayerDoll = serverPlayerDoll.getBukkitEntity();

        double yaw = player.getRotationVector().y;
        double pitch = player.getRotationVector().x;


        this.serverPlayerDoll.setDollSkin();
        this.serverPlayerDoll.connection = new DollNetworkHandler(minecraftServer, new DollNetworkManager(PacketFlow.CLIENTBOUND), serverPlayerDoll);

        this.serverPlayerDoll.setHealth(20.0f);
        this.serverPlayerDoll.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.serverPlayerDoll.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4.0D);

        this.craftPlayerDoll.setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier
        this.craftPlayerDoll.setMetadata("DOLL", new FixedMetadataValue(PlayerDoll.getPlugin(), "DOLL")); //Doll identifier

        //this.craftPlayerDoll.getPersistentDataContainer().set(new NamespacedKey(PlayerDoll.getPlugin(), "DOLL"), PersistentDataType.STRING, serverPlayerDoll.displayName);
        //this.craftPlayerDoll.getPersistentDataContainer().set(new NamespacedKey(PlayerDoll.getPlugin(), "OWNER"), PersistentDataType.STRING, owner.getStringUUID());

        this.serverPlayerDoll.initialDollInventoryMetadata();
        this.serverPlayerDoll.updateServerPlayerList();

        this.server.getPlayerList().respawn(this, serverLevel, true, serverPlayer.getBukkitEntity().getLocation(), true, PlayerRespawnEvent.RespawnReason.PLUGIN);

        PlayerDoll.dollManagerMap.put(this.dollProfile.getName(), serverPlayerDoll);


        Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(craftPlayerDoll, null));


        this.serverPlayerDoll.teleportTo(serverLevel, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, (float) yaw, (float) pitch);
        this.serverPlayerDoll.setPos(serverPlayer.position());
        this.serverPlayerDoll.setPose(serverPlayer.getPose());
        this.dollChunkPos = this.serverPlayerDoll.chunkPosition();

        //this.connection.send(new ClientboundSetChunkCacheCenterPacket(this.chunkPosition().x,this.chunkPosition().z));
        //this.connection.send(new ClientboundSetSimulationDistancePacket(Bukkit.getSimulationDistance()));
        //this.connection.send(new ClientboundSetChunkCacheRadiusPacket(Bukkit.getViewDistance()));


        if (this.enableChunkLoad) {
            if (this.chunkLoadSize > 0) {
                this.serverLevel.getChunkSource().addRegionTicket(TicketType.PLAYER, this.chunkPosition(), this.chunkLoadSize, this.chunkPosition());
            }
        }

        serverPlayerDoll.getEntityData().refresh(serverPlayerDoll);


        this.craftPlayerDoll.setNoDamageTicks(0);
        this.setHealth(20.0f);
        this.getFoodData().setFoodLevel(20);
        this.getFoodData().setExhaustion(0.0f);
        this.getFoodData().setSaturation(0.0f);
        this.craftPlayerDoll.setCollidable(false);
        this.setMaxUpStep(0.6f);
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);

        this.serverPlayerDoll.setDollLookAt();

        this.actionPack = new EntityPlayerActionPack(serverPlayerDoll);

        this.loadFlags();

        System.out.println(serverPlayerDoll);
    }

    private void setDollSkin() {
        if (!onlinemode) {
            return;
        }
        String skin = dollSkin;
        YamlConfiguration dollConfig = YAMLManager.getConfig(this.dollName);
        List<String> skinData = new ArrayList<>();



        List<String> dollSkinData = dollConfig.getStringList("SkinData");


        if (!dollSkinData.isEmpty() && (skin == null || dollConfig.getStringList("SkinData").get(0).equalsIgnoreCase(skin))) {
            dollProfile.getProperties().put("textures", new Property("textures", dollSkinData.get(1), dollSkinData.get(2)));
            this.dollSkin = dollSkinData.get(0);
            return;
        }
        skin = dollSkin == null ? owner.displayName : dollSkin;
        try {
            URL url_playerName = new URL("https://api.mojang.com/users/profiles/minecraft/" + skin);
            InputStreamReader reader_playerName = new InputStreamReader(url_playerName.openStream());

            String uuid = new JsonParser().parse(reader_playerName).getAsJsonObject().get("id").getAsString();
            URL url_playerTexture = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_playerTexture = new InputStreamReader(url_playerTexture.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_playerTexture).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            dollProfile.getProperties().put("textures", new Property("textures", texture, signature));
            skinData.add(skin);
            skinData.add(texture);
            skinData.add(signature);
            dollConfig.set("SkinData", skinData);
            this.dollSkin = skin;
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
        }

    }

    private void initialDollInventoryMetadata() {
        ////craftPlayerDoll.setMetadata("DollArmorMenu", new FixedMetadataValue(PlayerDoll.getPlugin(), new ArmorMenu(craftPlayerDoll)));
        //craftPlayerDoll.setMetadata("DollHotbarMenu", new FixedMetadataValue(PlayerDoll.getPlugin(), new HotbarMenu(craftPlayerDoll)));
        //craftPlayerDoll.setMetadata("DollInvenMenu", new FixedMetadataValue(PlayerDoll.getPlugin(), new BackpackMenu(craftPlayerDoll)));
        craftPlayerDoll.setMetadata("DollInvenMenu", new FixedMetadataValue(PlayerDoll.getPlugin(), new BackpackMenu_(craftPlayerDoll)));
        craftPlayerDoll.setMetadata("DollEnderChestMenu", new FixedMetadataValue(PlayerDoll.getPlugin(), new EnderChestMenu(craftPlayerDoll)));
    }

    public void setDollLookAt() {
        craftPlayerDoll.setRotation(serverPlayer.getBukkitEntity().getLocation().getYaw(), serverPlayer.getBukkitEntity().getLocation().getPitch());

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
        this.server.getPlayerList().broadcastAll(new ClientboundAddPlayerPacket(this));
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
            if (this.serverPlayerDoll.chunkPosition() != this.dollChunkPos && this.enableChunkLoad && this.chunkLoadSize > 0) {
                this.serverLevel.getChunkSource().removeRegionTicket(TicketType.PLAYER, this.dollChunkPos, this.chunkLoadSize, this.dollChunkPos);
                this.dollChunkPos = this.chunkPosition();
                this.serverLevel.getChunkSource().addRegionTicket(TicketType.PLAYER, this.dollChunkPos, this.chunkLoadSize, this.dollChunkPos);

                //this.connection.send(new ClientboundSetChunkCacheCenterPacket(this.chunkPosition().x,this.chunkPosition().z));
            }
            if (serverLevel != this.serverLevel()) {
                serverLevel = this.serverLevel();
                this.changeDimension(serverLevel);

            }
        } catch (NullPointerException ignored) {
            // happens with that paper port thingy - not sure what that would fix, but hey
            // the game not gonna crash violently.
        }
    }

    public void dollRespawn() {
        this.connection.onDisconnect(Component.literal("Teleport"));
        new DollManager(serverPlayer.getServer(), serverPlayer.serverLevel(), this.dollProfile, this.serverPlayer, this.dollSkin);
    }

    @Override
    public String getIpAddress() {
        return "127.0.0.1";
    }


    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.checkBlocking(damageSource)) {
            //this.attackBlocked = true;
            this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
        }
        boolean damaged = super.hurt(damageSource, f);
        if (damaged) {
            if (this.hurtMarked) {
                this.hurtMarked = false;
                Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), () -> DollManager.this.hurtMarked = true);
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
        return this.enableHostility && super.canBeSeenAsEnemy();
    }


    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.disconnect();
    }

    @Override
    public void disconnect() {
        this.saveFlags();
        this.setHealth(20.0f);
        super.disconnect();
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(this.getUUID())));
        //serverLevel.getChunkSource().removeRegionTicket(TicketType.PLAYER, this.dollChunkPos, this.chunkLoadSize, this.dollChunkPos);
        serverLevel.removePlayerImmediately(this, RemovalReason.DISCARDED);
        this.connection.onDisconnect(Component.literal("Disconnected"));
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0, y, 0.0, onGround);
    }

    @Override
    public Entity changeDimension(ServerLevel serverLevel) {

        super.changeDimension(serverLevel);
        if (wonGame) {
            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
            connection.handleClientCommand(p);
        }
        if (connection.player.isChangingDimension()) {
            //this.connection.send(new ClientboundSetChunkCacheCenterPacket(this.chunkPosition().x,this.chunkPosition().z));
            //this.connection.send(new ClientboundSetChunkCacheRadiusPacket(Bukkit.getViewDistance()));
            serverLevel.getChunkSource().removeRegionTicket(TicketType.PLAYER, this.dollChunkPos, this.chunkLoadSize, this.dollChunkPos);
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
    public Player getOwner() {return this.craftOwner;}

    private void loadFlags() {
        YAMLManager.getConfig(this.dollName).getConfigurationSection("setting").getValues(true).forEach((k,v) -> {
            if (k.endsWith(".toggle")) {
                String[] split = k.split("\\.");
                boolean bool = (boolean) v;
                switch (split[0]) {
                    //case "Inventory" -> this.enableInventory = bool;
                    case "Ender Chest" -> this.enableEnderChest = bool;
                    case "Chunk Load" -> this.enableChunkLoad = bool;
                    case "Invulnerable" -> craftPlayerDoll.setNoDamageTicks(bool? Integer.MAX_VALUE:0);
                    case "Hostility" -> this.enableHostility = bool;
                    case "Pushable" -> craftPlayerDoll.setCollidable(bool);
                    case "Gravity" -> this.setNoGravity(!bool);
                    case "Glow" -> this.setGlowingTag(bool);
                    case "Large Step Size" -> this.setMaxUpStep(bool? 1.0f:0.6f);
                }
            }
        });
    }
    private void saveFlags() {
        YamlConfiguration dollConfig = YAMLManager.getConfig(this.dollName);
        dollConfig.getConfigurationSection("setting").getValues(true).forEach((k,v) -> {
            if (k.endsWith(".toggle")) {
                String[] split = k.split("\\.");
                switch (split[0]) {
                    //case "Inventory" -> dollConfig.set("setting."+ split[0] +".toggle",this.enableInventory);
                    case "Ender Chest" -> dollConfig.set("setting."+ split[0] +".toggle",this.enableEnderChest);
                    case "Chunk Load" -> dollConfig.set("setting."+ split[0] +".toggle",this.enableChunkLoad);
                    case "Invulnerable" -> dollConfig.set("setting."+ split[0] +".toggle",craftPlayerDoll.getNoDamageTicks() > 20);
                    case "Hostility" -> dollConfig.set("setting."+ split[0] +".toggle",this.enableHostility);
                    case "Pushable" -> dollConfig.set("setting."+ split[0] +".toggle",craftPlayerDoll.isCollidable());
                    case "Gravity" -> dollConfig.set("setting."+ split[0] +".toggle",!this.isNoGravity());
                    case "Glow" -> dollConfig.set("setting."+ split[0] +".toggle",this.hasGlowingTag());
                    case "Large Step Size" -> dollConfig.set("setting."+ split[0] +".toggle",this.maxUpStep() == 1.0f);
                }
            }
        } );
    }
}
