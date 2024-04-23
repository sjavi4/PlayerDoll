package me.autobot.playerdoll.v1_20_R4.Event;

import me.autobot.playerdoll.CustomEvent.PlayerTransformEvent;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.player.TransformPlayer;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class TransformEvent implements Listener {

    static {
        Bukkit.getPluginManager().registerEvents(new TransformEvent(), PlayerDoll.getPlugin());
    }

    @EventHandler
    public void onPlayerTransform(PlayerTransformEvent event) {
        Player player = event.getPlayer();

        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
        //UniversalDollImpl doll = new UniversalDollImpl(serverPlayer);
        //Player dollPlayer = doll.getBukkitPlayer();

        //ServerLevel currentLevel = serverPlayer.serverLevel();
        //ResourceKey<LevelStem> resourcekey = currentLevel.getTypeKey();
        Location currentLocation = player.getLocation();
        int id = player.getEntityId();

        PlayerList playerList = serverPlayer.server.getPlayerList();

        //ServerGamePacketListenerImpl connection = serverPlayer.connection;
        //connection.send(new ClientboundRespawnPacket(serverPlayer.createCommonSpawnInfo(currentLevel), (byte)3));
        //connection.send(new ClientboundChangeDifficultyPacket(serverPlayer.level().getDifficulty(), serverPlayer.level().getLevelData().isDifficultyLocked()));
        //playerList.sendPlayerPermissionLevel(serverPlayer);
        //doll.connection = connection;
        //connection.player = doll;



        //serverPlayer.stopRiding();
        playerList.saveAll();
        playerList.players.remove(serverPlayer);
        Map<String,ServerPlayer> playersByName = getPlayersByName(playerList);
        if (playersByName != null) {
            playersByName.remove(serverPlayer.getScoreboardName().toLowerCase(Locale.ROOT));
        }
        Map<UUID, ServerPlayer> playersByUUID = getPlayersByUUID(playerList);
        if (playersByUUID != null) {
            playersByUUID.remove(serverPlayer.getUUID(), serverPlayer);
        }

        ServerLevel currentLevel = serverPlayer.serverLevel();
        currentLevel.removePlayerImmediately(serverPlayer, Entity.RemovalReason.CHANGED_DIMENSION);

        //ServerPlayer doll = new ServerPlayer(serverPlayer.server, serverPlayer.serverLevel(), serverPlayer.getGameProfile(), serverPlayer.clientInformation());

        TransformPlayer doll = new TransformPlayer(player);
        //doll.setId(id);
        final ServerGamePacketListenerImpl connection = serverPlayer.connection;

        Connection networkManager = getConnection(connection);
        if (networkManager != null) {
            serverPlayer.connection = null;
            serverPlayer = null;
            networkManager.suspendInboundAfterProtocolChange();
            playerList.placeNewPlayer(networkManager, doll, CommonListenerCookie.createInitial(doll.getGameProfile()));
            doll.connection = connection;
            connection.player = doll;
            setCommonPacketListenerPlayer(doll);
            playerList.respawn(doll, true, PlayerRespawnEvent.RespawnReason.PLUGIN);
            networkManager.resumeInboundAfterProtocolChange();
            doll.teleportTo(currentLocation.getX(),currentLocation.getY(),currentLocation.getZ());
            //doll.forceSetPositionRotation(currentLocation.getX(),currentLocation.getY(),currentLocation.getZ(),currentLocation.getYaw(),currentLocation.getPitch());
        }
        /*
        CompoundTag tag = playerList.load(doll);

        serverPlayer.wonGame = false;
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        doll.connection = connection;
        connection.player = doll;

        doll.restoreFrom(serverPlayer, true);
        doll.setId(serverPlayer.getId());
        doll.setMainArm(serverPlayer.getMainArm());

        for (String s : serverPlayer.getTags()) {
            doll.addTag(s);
        }
        //serverPlayer.remove(Entity.RemovalReason.DISCARDED);

        doll.spawnIn(currentLevel);
        doll.setServerLevel(currentLevel);

        doll.loadGameTypes(tag);
        doll.getBukkitEntity().sendSupportedChannels();
        doll.getStats().markAllDirty();
        doll.getRecipeBook().sendInitialRecipeBook(doll);
        playerList.updateEntireScoreboard(currentLevel.getScoreboard(), doll);
        doll.server.invalidateStatus();
        doll.containerMenu.transferTo(doll.containerMenu, doll.getBukkitEntity());

        doll.unsetRemoved();
        doll.setShiftKeyDown(false);
        doll.forceSetPositionRotation(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), currentLocation.getYaw(), currentLocation.getPitch());

        int i = 1;
        LevelData worlddata = currentLevel.getLevelData();
        doll.connection.send(new ClientboundRespawnPacket(doll.createCommonSpawnInfo(currentLevel), (byte)i));
        doll.connection.send(new ClientboundSetChunkCacheRadiusPacket(currentLevel.spigotConfig.viewDistance));
        doll.connection.send(new ClientboundSetSimulationDistancePacket(currentLevel.spigotConfig.simulationDistance));
        doll.connection.teleport(currentLocation);
        doll.connection.send(new ClientboundSetDefaultSpawnPositionPacket(currentLevel.getSharedSpawnPos(), currentLevel.getSharedSpawnAngle()));
        doll.connection.send(new ClientboundChangeDifficultyPacket(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        doll.connection.send(new ClientboundSetExperiencePacket(doll.experienceProgress, doll.totalExperience, doll.experienceLevel));
        playerList.sendLevelInfo(doll, currentLevel);
        playerList.sendPlayerPermissionLevel(doll);
        if (!doll.connection.isDisconnected()) {
            currentLevel.addRespawnedPlayer(doll);
            playerList.players.add(doll);
            if (playersByName != null) {
                playersByName.put(doll.getScoreboardName().toLowerCase(Locale.ROOT), doll);
            }
            if (playersByUUID != null) {
                playersByUUID.put(doll.getUUID(), doll);
            }
        }

        doll.setHealth(doll.getHealth());

        playerList.sendAllPlayerInfo(doll);
        doll.onUpdateAbilities();

        for (MobEffectInstance mobEffect : doll.getActiveEffects()) {
            doll.connection.send(new ClientboundUpdateMobEffectPacket(doll.getId(), mobEffect));
        }
        doll.initInventoryMenu();

         */

    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ServerPlayer> getPlayersByUUID(PlayerList list) {
        return (Map<UUID, ServerPlayer>) getMap(list, true);
    }
    @SuppressWarnings("unchecked")
    private Map<String, ServerPlayer> getPlayersByName(PlayerList list) {
        return (Map<String, ServerPlayer>) getMap(list, false);
    }

    @SuppressWarnings("unchecked")
    private Map<?, ServerPlayer> getMap(PlayerList list, boolean uuid) {
        for (Field field : list.getClass().getSuperclass().getDeclaredFields()) {
            if (field.getType() == Map.class) {
                // PlayersByUUID and PlayersByName
                if (field.getGenericType() instanceof ParameterizedType type) {
                    Type[] types = type.getActualTypeArguments();
                    if (types.length == 2 && types[1] == ServerPlayer.class) {
                        if (types[0] == UUID.class) {
                            if (uuid) {
                                field.setAccessible(true);
                                try {
                                    return (Map<?, ServerPlayer>) field.get(list);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if (types[0] == String.class) {
                            if (!uuid) {
                                field.setAccessible(true);
                                try {
                                    return (Map<?, ServerPlayer>) field.get(list);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Connection getConnection(ServerGamePacketListenerImpl connection) {
        for (Field field : connection.getClass().getSuperclass().getDeclaredFields()) {
            if (field.getType() == Connection.class) {
                field.setAccessible(true);
                try {
                    return (Connection) field.get(connection);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private void setCommonPacketListenerPlayer(ServerPlayer doll) {
        for (Field field : doll.connection.getClass().getSuperclass().getDeclaredFields()) {
            if (field.getType() == ServerPlayer.class) {
                field.setAccessible(true);
                try {
                    field.set(doll.connection, doll);
                    break;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
