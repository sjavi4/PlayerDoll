package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.YAMLManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DollManager {
    private static final DollManager instance = new DollManager();
    public static final File dollDirectory = new File(PlayerDoll.getDollDirectory());
    public static final Map<UUID, IDoll> ONLINE_DOLL_MAP = new HashMap<>();
    private static final String NAME_PATTERN = "^[a-zA-Z0-9_]*$";

    public static DollManager getInstance() {
        return instance;
    }

    public void removeDoll(String dollName) {
        File config = DollConfigHelper.getFile(dollName);
        if (!config.exists()) {
            return;
        }
        YamlConfiguration dollConfig = DollConfigHelper.getConfig(config);
        if (!dollConfig.contains("UUID")) {
            return;
        }
        String dollUUID = dollConfig.getString("UUID");
        if (dollUUID == null || dollUUID.isBlank()) {
            return;
        }
        UUID uuid = UUID.fromString(dollUUID);
        File dat = DollConfigHelper.getPlayerDataFile(dollUUID);
        File dat_old = DollConfigHelper.getPlayerData_OldFile(dollUUID);

        Runnable task = () -> {
            config.delete(); dat.delete(); dat_old.delete();
        };
        if (!isDollOnline(uuid)) {
            task.run();
        } else  {
            IDoll doll = ONLINE_DOLL_MAP.get(uuid);
            killDoll(doll);
            // Delay and then delete
            final ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();
            delayedExecutor.schedule(task, 2, TimeUnit.SECONDS);
            delayedExecutor.shutdown();
        }

    }
    public boolean renameDoll(String dollName, String newName) {
        String name = dollFullName(newName);
        UUID dollUUID = UUID.fromString(DollConfigHelper.getConfig(dollName).getString("UUID"));

        File oldConfig = DollConfigHelper.getFile(dollName);
        File newConfig = DollConfigHelper.getFile(name);
        if (oldConfig.exists() && !newConfig.exists()) {
            boolean flag = oldConfig.renameTo(new File(PlayerDoll.getDollDirectory(), name+".yml"));
            if (!flag) {
                new File(PlayerDoll.getDollDirectory(),name+".yml").renameTo(oldConfig);
            }
            return flag;
        } else {
            return false;
        }
    }
    public void spawnDoll(String dollName, UUID dollUUID, Player caller, boolean align) {
        //OFFLINE_DOLL_MAP.remove(dollName);
        IDoll doll;
        ONLINE_DOLL_MAP.put(dollUUID,null);
        try {
            ServerPlayer serverPlayer = null;
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".entity.CraftPlayer");
            if (caller != null) {
                serverPlayer = (ServerPlayer) caller.getClass().asSubclass(craftPlayerClass).getDeclaredMethod("getHandle").invoke(caller);
            }
            doll = (IDoll) DollHelper.callSpawn(serverPlayer,dollName, dollUUID , PlayerDoll.version);
            ONLINE_DOLL_MAP.put(dollUUID,doll);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        if (doll == null) {
            ONLINE_DOLL_MAP.remove(dollUUID);
            return;
        }
        if (align && caller != null) {
            var pos = caller.getLocation();
            doll._setPos(Math.round(pos.getX() * 2) / 2.0, pos.getBlockY(), Math.round(pos.getZ() * 2) / 2.0);
        }
        // caller = null -> spawn in original pos
    }

    public void despawnDoll(Player dollPlayer) {
        despawnDoll(ONLINE_DOLL_MAP.get(dollPlayer.getUniqueId()));
    }
    public void killDoll(Player dollPlayer) {
        killDoll(ONLINE_DOLL_MAP.get(dollPlayer.getUniqueId()));
    }
    public void despawnDoll(IDoll doll) {
        if (PlayerDoll.isFolia) {
            DollManager.Folia_Disconnect(doll);
        } else {
            doll._disconnect();
        }
        //ONLINE_DOLL_MAP.remove(doll.getBukkitPlayer().getUniqueId());
    }
    public void killDoll(IDoll doll) {
        if (PlayerDoll.isFolia) {
            DollManager.Folia_Kill(doll);
        } else {
            doll._kill();
        }
        //ONLINE_DOLL_MAP.remove(doll.getBukkitPlayer().getUniqueId());
    }


    public static boolean validateDollName(String dollName) {
        return dollShortName(dollName).matches(NAME_PATTERN);
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
        return isDollOnline(doll) || isDollJoinedBefore(doll);
    }
    public static String dollShortName(String name) {
        return name.startsWith("-") ? name.substring(1) : name;
    }
    public static String dollFullName(String name) {
        return name.startsWith("-") ? name : "-" + name;
    }
    public static boolean isPlayerDoll(Player player) {
        return ONLINE_DOLL_MAP.containsKey(player.getUniqueId());
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

    public static void Folia_Disconnect(IDoll iDoll) {
        PlayerDoll.getFoliaHelper().entityTask(iDoll.getBukkitPlayer(), iDoll::_disconnect, 1);
    }
    public static void Folia_Kill(IDoll iDoll) {
        PlayerDoll.getFoliaHelper().entityTask(iDoll.getBukkitPlayer(), iDoll::_kill, 1);
    }
}
