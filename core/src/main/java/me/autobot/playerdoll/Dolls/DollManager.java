package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.Folia.FoliaHelper;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.YAMLManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class DollManager {
    public static final DollManager instance;
    public static final File dollDirectory = new File(PlayerDoll.getDollDirectory());
    public static final Map<UUID, IDoll> ONLINE_DOLL_MAP = new HashMap<>();
    public static final List<String> OFFLINE_DOLL_NAME = new ArrayList<>();
    public static final List<UUID> OFFLINE_DOLL_UUID = new ArrayList<>();
    public static final Map<String, UUID> OFFLINE_DOLL_MAP = new HashMap<>();
    public static final List<String> ALL_DOLL_NAME = new ArrayList<>();
    public static final List<UUID> ALL_DOLL_UUID = new ArrayList<>();

    static {
        File[] dollDirectories = dollDirectory.listFiles();
        if (dollDirectories == null || dollDirectories.length == 0) {
            instance = new DollManager(null);
        } else {
            instance = new DollManager(dollDirectories);
        }
    }

    private DollManager(File[] directory) {
        if (directory == null) {
            return;
        }
        for (File file : directory) {
            if (file.length() == 0) {
                file.delete();
            }
            String dollName = file.getName().substring(0,file.getName().length()-4);
            YamlConfiguration configuration = getDollConfigCopy(file);
            UUID dollUUID = UUID.fromString(configuration.getString("UUID"));
            ALL_DOLL_NAME.add(dollName);
            ALL_DOLL_UUID.add(dollUUID);
            OFFLINE_DOLL_NAME.add(dollName);
            OFFLINE_DOLL_UUID.add(dollUUID);
            OFFLINE_DOLL_MAP.put(dollName, dollUUID);
        }
    }

    public void createDoll() {

    }
    public void removeDoll(UUID dollUUID, String dollName) {
        File config = new File(PlayerDoll.getDollDirectory(), dollName + ".yml");
        File dat = new File(Bukkit.getServer().getWorldContainer() + File.separator + "world" + File.separator + "playerdata" + File.separator + dollUUID + ".dat");
        File dat_old = new File(Bukkit.getServer().getWorldContainer() + File.separator + "world" + File.separator + "playerdata" + File.separator + dollUUID + ".dat_old");
        Runnable task = () -> {
            config.delete(); dat.delete(); dat_old.delete();
        };
        if (!isDollOnline(dollUUID)) {
            task.run();
        } else  {
            IDoll doll = ONLINE_DOLL_MAP.get(dollUUID);

            // Delay and then delete
            final ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();
            delayedExecutor.schedule(task, 2, TimeUnit.SECONDS);
            delayedExecutor.shutdown();
        }
    }
    public boolean renameDoll(OfflinePlayer doll, String newName) {
        if (!doll.hasPlayedBefore()) {
            return false;
        }
        String name = dollFullName(newName);
        final boolean online = doll.isOnline();
        File oldConfig = new File(PlayerDoll.getDollDirectory(), doll.getName() +".yml");
        File newConfig = new File(PlayerDoll.getDollDirectory(), name+".yml");
        if (oldConfig.exists() && !newConfig.exists()) {
            if (online) {
                despawnDoll((Player) doll);
            }
            boolean flag = oldConfig.renameTo(new File(PlayerDoll.getDollDirectory(), name+".yml"));
            if (!flag) {
                new File(PlayerDoll.getDollDirectory(),name+".yml").renameTo(oldConfig);
            }
            if (online) {
                if (flag) spawnDoll(name, null);
                else spawnDoll(doll.getName(), null);
            }
            return flag;
        } else {
            return false;
        }

    }
    public void spawnDoll(String dollName, Player caller) {
        checkConfig();
        OFFLINE_DOLL_MAP.remove(dollName);

        // caller = null -> spawn in original pos
    }
    public void despawnDoll(Player dollPlayer) {
        IDoll doll = ONLINE_DOLL_MAP.get(dollPlayer.getUniqueId());
        if (PlayerDoll.isFolia) {
            DollManager.Folia_Disconnect(dollPlayer, doll);
        } else {
            doll._disconnect();
        }
    }
    public void killDoll(Player dollPlayer) {
        IDoll doll = ONLINE_DOLL_MAP.get(dollPlayer.getUniqueId());
        if (PlayerDoll.isFolia) {
            DollManager.Folia_Kill(dollPlayer, doll);
        } else {
            doll._kill();
        }
    }

    private void checkConfig() {

    }

    public static YamlConfiguration getDollConfigCopy(File dollFile) {
        return YamlConfiguration.loadConfiguration(dollFile);
    }
    public static YamlConfiguration getDollConfigCopy(String dollName) {
        if (isDollOnline(dollName)) {
            return DollConfigManager.getConfigManager(dollName).config;
        } else {
            if (!OFFLINE_DOLL_NAME.contains(dollName)) {
                return null;
            }
            YAMLManager config = YAMLManager.loadConfig(dollName, false, true);
            if (config == null) {
                return null;
            }
            return config.getConfig();
        }
    }
    public static YamlConfiguration getDollConfigCopy(UUID uuid) {
        if (isDollOnline(uuid)) {
            return DollConfigManager.getConfigManager(uuid).config;
        } else {
            if (!OFFLINE_DOLL_UUID.contains(uuid)) {
                return null;
            }
            String dollName = OFFLINE_DOLL_NAME.get(OFFLINE_DOLL_UUID.indexOf(uuid));
            YAMLManager config = YAMLManager.loadConfig(dollName, false, true);
            if (config == null) {
                return null;
            }
            return config.getConfig();
        }
    }
    public static boolean isDollOnline(UUID doll) {
        return Bukkit.getPlayer(doll) != null;
    }
    public static boolean isDollOnline(String doll) {
        return Bukkit.getPlayer(doll) != null;
    }
    public static boolean isDollJoinedBefore(UUID doll) {
        return Bukkit.getOfflinePlayer(doll).hasPlayedBefore();
    }
    public static boolean isDollConfigExist(String dollName) {
        return YAMLManager.loadConfig(dollName,false,true) != null;
    }
    public static boolean isDollExist(UUID doll) {
        if (isDollOnline(doll) || isDollJoinedBefore(doll)) {
            return true;
        } else {
            return OFFLINE_DOLL_MAP.containsValue(doll);
        }
    }
    public static String dollShortName(String name) {
        return name.startsWith("-") ? name.substring(1) : name;
    }
    public static String dollFullName(String name) {
        return name.startsWith("-") ? name : "-" + name;
    }
    public static UUID getDollUUID(String name) {return ALL_DOLL_UUID.get(ALL_DOLL_NAME.indexOf(name));}
    public static String getDollName(UUID uuid) {return ALL_DOLL_NAME.get(ALL_DOLL_UUID.indexOf(uuid));}
    public static boolean isPlayerDoll(Player player) {return ONLINE_DOLL_MAP.containsKey(player.getUniqueId());}
    public static boolean isOfflinePlayerDoll(OfflinePlayer player) {return OFFLINE_DOLL_NAME.contains(player.getName()) && OFFLINE_DOLL_UUID.contains(player.getUniqueId());}

    public static void Folia_HandleAcceptedLogin(Object connection, Object serverPlayer, Object playerList, Object chunkSource, Runnable packetTask) {
        try {
            Class<?> regionizedServerFolia = FoliaHelper.FOLIA_REGIONIZED_SERVER;
            Object regionizedServer = FoliaHelper.REGOINIZED_SERVER;
            MutableObject<CompoundTag> data = new MutableObject<>();
            MutableObject<String> lastKnownName = new MutableObject<>();
            Class<?> completable = Class.forName("ca.spottedleaf.concurrentutil.completable.Completable");
            Object toComplete = completable.getConstructor().newInstance();

            TicketType<?> loginType = (TicketType<?>) TicketType.class.getField("LOGIN").get(null);
            Object taskQueue = regionizedServerFolia.getField("taskQueue").get(regionizedServer);
            Class<?> priorityClass = Class.forName("ca.spottedleaf.concurrentutil.executor.standard.PrioritisedExecutor$Priority");
            Object[] priority = priorityClass.getEnumConstants();

            Method qQ = taskQueue.getClass().getMethod("queueTickTaskQueue", ServerLevel.class, int.class, int.class, Runnable.class, priorityClass);
            Method placeNewPlayerFolia = playerList.getClass().getMethod("placeNewPlayer", Connection.class, ServerPlayer.class, CompoundTag.class, String.class, Location.class);

            BiConsumer<Location, Throwable> waiter = (loc, t) -> {
                int chunkX = loc.getX() < (double)(int)loc.getX() ? (int)loc.getX() -1 >> 4 : (int)loc.getX() >> 4;
                int chunkZ = loc.getZ() < (double)(int)loc.getZ() ? (int)loc.getZ() -1 >> 4 : (int)loc.getZ() >> 4;
                try {
                    Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".CraftWorld");
                    ServerLevel serverLevel = (ServerLevel) loc.getWorld().getClass().asSubclass(craftWorldClass).getDeclaredMethod("getHandle").invoke(loc.getWorld());
                    Method addTicket = chunkSource.getClass().getMethod("addTicketAtLevel", TicketType.class, ChunkPos.class, int.class, Object.class);
                    addTicket.invoke(chunkSource,loginType, new ChunkPos(chunkX,chunkZ), 33, Unit.INSTANCE);

                    Runnable r = () -> {
                        try {
                            if (t == null) {
                                placeNewPlayerFolia.invoke(playerList, (Connection)connection, (ServerPlayer)serverPlayer, data.getValue(), lastKnownName.getValue(), loc);
                                packetTask.run();
                            }
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    };

                    qQ.invoke(taskQueue, serverLevel, chunkX, chunkZ, r, priority[2]);
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            };


            toComplete.getClass().getMethod("addWaiter", BiConsumer.class).invoke(toComplete,waiter);

            Method loadSpawnForNewPlayerFolia = playerList.getClass().getMethod("loadSpawnForNewPlayer", Connection.class, ServerPlayer.class, MutableObject.class, MutableObject.class, completable);
            loadSpawnForNewPlayerFolia.invoke(playerList, (Connection)connection, (ServerPlayer)serverPlayer, data, lastKnownName, toComplete);


        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            e.printStackTrace();
        }
    }

    public static void Paper_RemoveChunkLoader(Object serverLevel, Object serverPlayer) {
        try {
            Object playerChunkLoader = serverLevel.getClass().getField("playerChunkLoader").get(serverLevel);
            playerChunkLoader.getClass().getDeclaredMethod("removePlayer", ServerPlayer.class).invoke(playerChunkLoader, serverPlayer);
        } catch (NoSuchMethodException | NoSuchFieldException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception while invoking Paper's playerChunkLoader");
            throw new RuntimeException(e);
        }
    }

    public static void Folia_Disconnect(Player doll, IDoll iDoll) {
        PlayerDoll.getFoliaHelper().entityTask(doll, iDoll::_disconnect, 1);
    }
    public static void Folia_Kill(Player doll, IDoll iDoll) {
        PlayerDoll.getFoliaHelper().entityTask(doll, iDoll::_kill, 1);
    }
}
