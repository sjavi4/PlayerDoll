package me.autobot.playerdoll.doll;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.PermConfig;
import me.autobot.playerdoll.util.FileUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DollManager {

    private static final FileUtil FILE_UTIL = FileUtil.INSTANCE;
    public static final Map<UUID, Doll> ONLINE_DOLLS = new HashMap<>();
    public static final Map<UUID, ExtendPlayer> ONLINE_PLAYERS = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> PLAYER_CREATION_COUNTS = new Object2IntOpenHashMap<>();
    public static final Map<UUID, PermissionAttachment> DOLL_PERMISSIONS = new HashMap<>();

    public static final Map<UUID, String> DOLL_BUNGEE_SERVERS = new HashMap<>();
    //private static final String NAME_PATTERN = "^[a-zA-Z0-9_]*$";
    // Word argument filters most characters
    private static final String DOLL_IDENTIFIER = BasicConfig.get().dollIdentifier.getValue();
    private static final String NAME_PATTERN_COMMAND = ".*[.+\\-].*";

//    public boolean renameDoll(String dollName, String newName) {
//        String name = dollFullName(newName);
//
//        File oldConfig = FILE_UTIL.getFile(FILE_UTIL.getDollDir(), dollName + ".yml");;
//        File newConfig = FILE_UTIL.getFile(FILE_UTIL.getDollDir(), name + ".yml");;
//        if (oldConfig.exists() && !newConfig.exists()) {
//            File file = FILE_UTIL.getFile(FILE_UTIL.getDollDir(), name + ".yml");
//            boolean flag = oldConfig.renameTo(file);
//            //boolean flag = oldConfig.renameTo(new File(PlayerDoll.getDollDirectory(), name+".yml"));
//            if (!flag) {
//                file.renameTo(oldConfig);
//                //new File(PlayerDoll.getDollDirectory(),name+".yml").renameTo(oldConfig);
//            }
//            return flag;
//        } else {
//            return false;
//        }
//    }

    public static boolean validateDollName(String dollName) {
        return dollShortName(dollName).matches(NAME_PATTERN_COMMAND);
        //return dollShortName(dollName).matches(NAME_PATTERN);
    }
    public static String dollShortName(String name) {
        if (DOLL_IDENTIFIER.isEmpty()) {
            return name;
        } else {
            return name.startsWith(DOLL_IDENTIFIER) ? name.substring(1) : name;
        }
        //return name.startsWith("-") ? name.substring(1) : name;
    }
    public static String dollFullName(String name) {
        return name.startsWith(DOLL_IDENTIFIER) ? name : DOLL_IDENTIFIER + name;
        //return name.startsWith("-") ? name : "-" + name;
    }

    public static boolean canPlayerCreateDoll(Player player) {
        PermConfig permConfig = PermConfig.get();
        if (!permConfig.enable.getValue()) {
            return true;
        }

        Map<UUID, Integer> countMap = DollManager.PLAYER_CREATION_COUNTS;
        Integer currentCount = countMap.get(player.getUniqueId());
        if (currentCount == null) {
            currentCount = 0;
        }

        int futureCount = currentCount + 1;

        boolean exceed = false;
        Map<String, Integer> maxCreationMap = permConfig.groupPerCreateLimits;
        for (String group : maxCreationMap.keySet()) {
            if (player.hasPermission(PermConfig.PERM_CREATE_STRING + group)) {
                exceed = futureCount > maxCreationMap.get(group);
                // iterate all
            }
        }
        if (exceed) {
            return false;
        }
        countMap.put(player.getUniqueId(), futureCount);
        return true;
    }
}
