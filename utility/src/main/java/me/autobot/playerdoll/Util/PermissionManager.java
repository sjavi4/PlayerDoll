package me.autobot.playerdoll.Util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class PermissionManager {
    public static final HashMap<String,PermissionManager> permissionGroupMap = new HashMap<>();
    public static final HashMap<Player, String> playerPermissionGroup = new HashMap<>();
    public static final YamlConfiguration permissionFile = ConfigManager.getPermission();
    private static File uuidDirectory;
    public static YamlConfiguration playerUUIDs;
    public Map<String, Boolean> flagGlobalDisplays = new HashMap<>();
    public Map<String, Boolean> flagGlobalToggles = new HashMap<>();
    public Map<String, Boolean> flagPersonalDisplays = new HashMap<>();
    public Map<String, Boolean> flagPersonalToggles = new HashMap<>();
    public String groupName;
    public String mirror;
    public String nextGroup;
    public boolean canCreateDoll;
    public int maxDollCreation;
    public int maxDollSpawn;
    public boolean canJoinAtStart;
    public int minUseInterval;
    public int minAttackInterval;
    public int minSwapInterval;
    public int minDropInterval;
    public int minJumpInterval;
    public int minLookatInterval;
    public boolean restrictSkin;
    public boolean bypassMaxPlayer;
    public boolean keepInventory;
    public boolean notCountSleeping;
    public String prefix;
    public String suffix;
    public boolean bypassResidence;

    public double costPerCreation;
    public double costForUpgrade;
    public static void newInstance(Plugin plugin) {
        uuidDirectory = new File(plugin.getDataFolder() + File.separator + "player","uuids.yml");
        playerUUIDs = YamlConfiguration.loadConfiguration(uuidDirectory);
        List<String> groups = permissionFile.getStringList("permissionGroup");
        groups.forEach(s -> {
            permissionGroupMap.put(s, new PermissionManager(s));
            playerUUIDs.addDefault(s,new ArrayList<>());
            //playerPermissionGroup.put(s,null);
        });
        Bukkit.getOnlinePlayers().forEach(p -> {
            checkPlayerPermission(p);
        });
    }
    public static PermissionManager getInstance(Player player) {
        return permissionGroupMap.get(playerPermissionGroup.get(player));
    }
    public static PermissionManager getInstance(String group) {
        return permissionGroupMap.get(group);
    }
    private PermissionManager(String permissionGroup) {
        if (!permissionFile.contains(permissionGroup)) return;
        this.groupName = permissionGroup;
        this.mirror = permissionFile.getString(this.groupName + ".Mirror");
        this.nextGroup = permissionFile.getString(this.groupName + ".NextGroup");
        
        if (hasMirror() && hasPermissionGroup()) {
            mirrorPermission();
        }
        loadPermission();
    }

    private void loadPermission() {
        getPermission("canCreateDoll");
        getPermission("maxDollCreation");
        getPermission("maxDollSpawn");
        getPermission("canJoinAtStart");
        getPermission("minUseInterval");
        getPermission("minAttackInterval");
        getPermission("minSwapInterval");
        getPermission("minDropInterval");
        getPermission("minJumpInterval");
        getPermission("minLookatInterval");
        getPermission("restrictSkin");
        getPermission("bypassMaxPlayer");
        getPermission("keepInventory");
        getPermission("notCountSleeping");
        getPermission("prefix");
        getPermission("suffix");

        getPermission("bypassResidence");
        getPermission("costPerCreation");
        getPermission("costForUpgrade");

        getPermissionMap("GlobalFlag.Display");
        getPermissionMap("PersonalFlag.Display");
        getPermissionMap("GlobalFlag.Toggle");
        getPermissionMap("PersonalFlag.Toggle");
    }

    private void getPermission(String key) {
        String path = groupName + "." + key;
        if (permissionFile.contains(path)) {
            try {
                Field f = this.getClass().getField(key);
                f.set(this, permissionFile.get(path));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            //value = permissionFile.get(path);
        }
    }
    private void getPermissionMap(String key) {
        String path = groupName + "." + key;
        if (!permissionFile.contains(path)) return;
        String[] splitText = key.split("\\.");
        Map<String, Object> map = permissionFile.getConfigurationSection(path).getValues(true);
        if (splitText[0].equals("GlobalFlag")) {
            if (splitText[1].equals("Display")) {
                map.forEach( (s,o) -> this.flagGlobalDisplays.put(s,(boolean) o));
            } else if (splitText[1].equals("Toggle")) {
                map.forEach( (s,o) -> this.flagGlobalToggles.put(s,(boolean) o));
            }
        } else if (splitText[0].equals("PersonalFlag")) {
            if (splitText[1].equals("Display")) {
                map.forEach( (s,o) -> this.flagPersonalDisplays.put(s,(boolean) o));
            } else if (splitText[1].equals("Toggle")) {
                map.forEach( (s,o) -> this.flagPersonalToggles.put(s,(boolean) o));
            }
        }
    }
    private void mirrorPermission() {
        PermissionManager mirror = permissionGroupMap.get(this.mirror);
        this.canCreateDoll = mirror.canCreateDoll;
        this.maxDollCreation = mirror.maxDollCreation;
        this.maxDollSpawn = mirror.maxDollSpawn;
        this.canJoinAtStart = mirror.canJoinAtStart;
        this.minUseInterval = mirror.minUseInterval;
        this.minAttackInterval = mirror.minAttackInterval;
        this.minSwapInterval = mirror.minSwapInterval;
        this.minDropInterval = mirror.minDropInterval;
        this.minJumpInterval = mirror.minJumpInterval;
        this.minLookatInterval = mirror.minLookatInterval;
        this.restrictSkin = mirror.restrictSkin;
        this.bypassMaxPlayer = mirror.bypassMaxPlayer;
        this.keepInventory = mirror.keepInventory;
        this.notCountSleeping = mirror.notCountSleeping;
        this.prefix = mirror.prefix;
        this.suffix = mirror.suffix;

        this.bypassResidence = mirror.bypassResidence;
        this.costPerCreation = mirror.costPerCreation;
        this.costForUpgrade = mirror.costForUpgrade;

        this.flagGlobalDisplays.putAll(mirror.flagGlobalDisplays);
        this.flagGlobalToggles.putAll(mirror.flagGlobalToggles);
        this.flagPersonalDisplays.putAll(mirror.flagPersonalDisplays);
        this.flagPersonalToggles.putAll(mirror.flagPersonalToggles);
    }

    private boolean hasMirror() {
        return !(mirror == null || mirror.isBlank()) && permissionFile.contains(mirror);
    }
    private boolean hasPermissionGroup() {
        return permissionFile.contains(mirror);
    }

    public static void savePlayerUUIDs() {
        try {
            playerUUIDs.save(uuidDirectory);
        } catch (IOException ignored) {
        }

    }
    public static void checkPlayerPermission(Player player) {
        if (player.getName().startsWith("-")) {
            return;
        }
        permissionGroupMap.keySet().forEach(s -> {
            if (playerUUIDs.contains(s+"."+player.getUniqueId())) {
                playerPermissionGroup.put(player,s);
                return;
            }
            playerPermissionGroup.put(player,"default");
            List<String> list = playerUUIDs.getStringList("default");
            if (list.contains(player.getUniqueId().toString())) {
                return;
            }
            list.add(player.getUniqueId().toString());
            playerUUIDs.set("default", list);
        });
    }
    private static PermissionManager getPlayerPermission(String uuid) {
        for (String s : permissionGroupMap.keySet()) {
            if (playerUUIDs.contains(s+"."+uuid)) {
                return permissionGroupMap.get(s);
            }
        }
        return null;
    }
    public static PermissionManager getOfflinePlayerPermission(OfflinePlayer player) {
        return getOfflinePlayerPermission(player.getUniqueId());
    }
    public static PermissionManager getOfflinePlayerPermission(UUID uuid) {
        return getPlayerPermission(uuid.toString());
    }
    public static PermissionManager getOfflinePlayerPermission(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (!player.hasPlayedBefore()) {
            return getOfflinePlayerPermission(player.getUniqueId());
        }
        return null;
    }
}
