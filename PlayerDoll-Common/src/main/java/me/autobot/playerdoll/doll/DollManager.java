package me.autobot.playerdoll.doll;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.autobot.playerdoll.config.PermConfig;
import me.autobot.playerdoll.doll.config.DollConfigHelper;
import me.autobot.playerdoll.util.FileUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DollManager {
    public static final DollManager INSTANCE = new DollManager();
    private static final FileUtil FILE_UTIL = FileUtil.INSTANCE;
    public static final Map<UUID, Doll> ONLINE_DOLLS = new HashMap<>();
    public static final Map<UUID, ExtendPlayer> ONLINE_PLAYERS = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> PLAYER_CREATION_COUNTS = new Object2IntOpenHashMap<>();
    public static final Map<UUID, PermissionAttachment> DOLL_PERMISSIONS = new HashMap<>();

    public static final Map<UUID, String> DOLL_BUNGEE_SERVERS = new HashMap<>();
    //private static final String NAME_PATTERN = "^[a-zA-Z0-9_]*$";
    // Word argument filters most characters
    private static final String NAME_PATTERN_COMMAND = ".*[.+\\-].*";

//    public void removeDoll(Player sender, String dollName) {
//        //File config = DollConfigHelper.getFile(dollName);
//        File dollFile = FILE_UTIL.getOrCreateFile(FILE_UTIL.getDollDir(), dollName + ".yml");
//        //File dollFile = new File(PlayerDoll.getDollDirectory(),dollName+".yml");
//        DollConfig dollConfig = DollConfig.getOfflineDollConfig(dollName);
//        if (!dollFile.exists()) {
//            return;
//        }
//        //YamlConfiguration dollConfig = DollConfigHelper.getConfig(config);
//        //String dollUUID = dollConfig.getString("UUID");
//        String dollUUID = dollConfig.dollUUID.getValue();
//        if (dollUUID.equals(DollConfig.NULL_UUID)) {
//            return;
//        }
//        UUID uuid = UUID.fromString(dollUUID);
//        File dat = DollConfigHelper.getPlayerDataFile(dollUUID);
//        File dat_old = DollConfigHelper.getPlayerData_OldFile(dollUUID);
//
//        Runnable task = () -> {
//            dollFile.delete(); dat.delete(); dat_old.delete();
//        };
//        if (!isDollOnline(uuid)) {
//            task.run();
//        } else  {
//            Doll doll = ONLINE_DOLLS.get(uuid);
//            killDoll(doll);
//            // Delay and then delete
//            final ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();
//            delayedExecutor.schedule(task, 2, TimeUnit.SECONDS);
//            delayedExecutor.shutdown();
//        }
//        dollConfig = null;
//        int count = PLAYER_DOLL_COUNT_MAP.get(sender.getUniqueId());
//        PLAYER_DOLL_COUNT_MAP.put(sender.getUniqueId(), count-1);
//    }
    public boolean renameDoll(String dollName, String newName) {
        String name = dollFullName(newName);
        //UUID dollUUID = UUID.fromString(DollConfigHelper.getConfig(dollName).getString("UUID"));

        File oldConfig = DollConfigHelper.getFile(dollName);
        File newConfig = DollConfigHelper.getFile(name);
        if (oldConfig.exists() && !newConfig.exists()) {
            File file = FILE_UTIL.getFile(FILE_UTIL.getDollDir(), name + ".yml");
            boolean flag = oldConfig.renameTo(file);
            //boolean flag = oldConfig.renameTo(new File(PlayerDoll.getDollDirectory(), name+".yml"));
            if (!flag) {
                file.renameTo(oldConfig);
                //new File(PlayerDoll.getDollDirectory(),name+".yml").renameTo(oldConfig);
            }
            return flag;
        } else {
            return false;
        }
    }
//    public void spawnDoll(String dollName, UUID dollUUID, Player caller) {
//        //OFFLINE_DOLL_MAP.remove(dollName);
//        Doll doll;
//        //ONLINE_DOLL_MAP.put(dollUUID,null);
//        /*
//        ServerPlayer serverPlayer = null;
//        Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".entity.CraftPlayer");
//        if (caller != null) {
//            serverPlayer = (ServerPlayer) caller.getClass().asSubclass(craftPlayerClass).getDeclaredMethod("getHandle").invoke(caller);
//        }
//
//         */
//
//        //DollHelper.callSpawn(caller,dollName, dollUUID , PlayerDoll.version);
//        //ONLINE_DOLL_MAP.put(dollUUID,doll);
//        // caller = null -> spawn in original pos
//    }

//    public void despawnDoll(Player dollPlayer) {
//        despawnDoll(ONLINE_DOLLS.get(dollPlayer.getUniqueId()));
//    }
//    public void killDoll(Player dollPlayer) {
//        killDoll(ONLINE_DOLLS.get(dollPlayer.getUniqueId()));
//    }
//    public void despawnDoll(Doll doll) {
//        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
//            DollManager.Folia_Disconnect(doll);
//        } else {
//            doll.dollDisconnect();
//        }
//        //ONLINE_DOLL_MAP.remove(doll.getBukkitPlayer().getUniqueId());
//    }
//    public void killDoll(Doll doll) {
//        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
//            DollManager.Folia_Kill(doll);
//        } else {
//            doll.dollKill();
//        }
//        //ONLINE_DOLL_MAP.remove(doll.getBukkitPlayer().getUniqueId());
//    }


    public static boolean validateDollName(String dollName) {
        return dollShortName(dollName).matches(NAME_PATTERN_COMMAND);
        //return dollShortName(dollName).matches(NAME_PATTERN);
    }
//    public static boolean isDollOnline(UUID doll) {
//        return Bukkit.getPlayer(doll) != null;
//    }
//    public static boolean isDollOnline(String doll) {
//        return Bukkit.getPlayer(doll) != null;
//    }
//    public static boolean isDollJoinedBefore(UUID doll) {
//        return Bukkit.getOfflinePlayer(doll).hasPlayedBefore();
//    }
//    public static boolean isDollConfigExist(String dollName) {
//        return FILE_UTIL.getFile(FILE_UTIL.getDollDir(),  dollName+ ".yml") != null;
//        //return YAMLManager.loadConfig(dollName,false,true) != null;
//    }
//    public static boolean isDollExist(UUID doll) {
//        return isDollOnline(doll) || isDollJoinedBefore(doll);
//    }
    public static String dollShortName(String name) {
        return name.startsWith("-") ? name.substring(1) : name;
    }
    public static String dollFullName(String name) {
        return name.startsWith("-") ? name : "-" + name;
    }

    public static boolean canPlayerCreateDoll(Player player) {
        PermConfig permConfig = PermConfig.get();
        if (!permConfig.enable.getValue()) {
            return true;
        }

        Map<UUID, Integer> countMap = DollManager.PLAYER_CREATION_COUNTS;
        Integer currentCount = countMap.get(player.getUniqueId());
        currentCount = currentCount == null ? 1 : currentCount + 1;

        boolean exceed = false;
        Map<String, Integer> maxCreationMap = permConfig.groupPerCreateLimits;
        for (String group : maxCreationMap.keySet()) {
            if (player.hasPermission(PermConfig.PERM_CREATE_STRING + group)) {
                exceed = currentCount >= maxCreationMap.get(group);
                // iterate all
            }
        }
        if (exceed) {
            return false;
        }


        if (countMap.containsKey(player.getUniqueId())) {
            int c = countMap.get(player.getUniqueId());
            countMap.put(player.getUniqueId(), c+1);
        } else {
            countMap.put(player.getUniqueId(), 1);
        }
        return true;
    }
//    public static boolean isPlayerDoll(Player player) {
//        return ONLINE_DOLLS.containsKey(player.getUniqueId());
//    }

//    public static void Folia_Disconnect(Doll doll) {
//        PlayerDoll.scheduler.entityTask(doll::dollDisconnect, doll.getBukkitPlayer());
//        //PlayerDoll.getFoliaHelper().entityTask(iDoll.getBukkitPlayer(), iDoll::_disconnect, 1);
//    }
//    public static void Folia_Kill(Doll doll) {
//        PlayerDoll.scheduler.entityTask(doll::dollKill, doll.getBukkitPlayer());
//        //PlayerDoll.getFoliaHelper().entityTask(iDoll.getBukkitPlayer(), iDoll::_kill, 1);
//    }
}
