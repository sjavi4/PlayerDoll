package me.autobot.playerdoll.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PermissionManager {
    public static final Set<String> permissionGroups = new HashSet<>();
    public static final Map<String, String> attachments = new HashMap<>(); //ExternName, PermName
    public static YamlConfiguration permissionYAML;
    private static File offlinePlayerPermFile;
    private static YamlConfiguration offlinePlayerPerm;
    public static final Map<UUID, String> playerPermissions = new HashMap<>();
    public final Map<String, Object> groupProperties = new LinkedHashMap<>();
    public final Map<String, Object> dollProperties = new LinkedHashMap<>();
    public final Map<String, Boolean> dollDefaultSettings = new LinkedHashMap<>();
    public final Map<String, Boolean> dollAvailableFlags = new LinkedHashMap<>();
    public final Map<String, Boolean> playerDefaultSettings = new LinkedHashMap<>();
    public final Map<String, Boolean> playerAvailableFlags = new LinkedHashMap<>();
    public final String mirror;
    public final String groupName;
    public final String nextGroup;
    public final List<String> attachedGroup;
    public static boolean externalPerm;

    public static void initialize(Plugin plugin, boolean useExtern) {
        externalPerm = useExtern;
        if (!externalPerm) {
            offlinePlayerPermFile = new File(plugin.getDataFolder(), "playerPerm.yml");
            offlinePlayerPerm = YamlConfiguration.loadConfiguration(offlinePlayerPermFile);
            offlinePlayerPerm.getConfigurationSection("").getKeys(false).forEach(k -> {
                if (k.contains(".")) {
                    String[] split = k.split("\\.");
                    String name = split[0];
                    UUID uuid = UUID.fromString(split[1]);
                    playerPermissions.put(uuid, name);
                } else {
                    permissionGroups.add(k);
                }
            });
        }
        permissionGroups.forEach(g -> {
            permissionYAML.getStringList("group."+g+".Attach").forEach(r -> {
                if (!r.isBlank()) attachments.put(r,g);
            });
        });
    }
    public static void save() {
        if (!externalPerm) {
            // Not save default group
            offlinePlayerPerm.set("default", null);
            try {
                offlinePlayerPerm.save(offlinePlayerPermFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static PermissionManager getPermByExternalGroup(String groupName) {
        if (!externalPerm) return null;
        return getPermissionGroup(attachments.get(groupName));
    }
    public static PermissionManager getPermissionGroup(String groupName) {
        if (permissionGroups.contains(groupName)) {
            return new PermissionManager(groupName);
        } else {
            permissionYAML = ConfigManager.getPermission();
            if (permissionYAML.contains("group."+groupName)) {
                permissionGroups.add(groupName);
                permissionYAML.getStringList("group."+groupName+".Attach").forEach(r -> {
                    if (!r.isBlank()) attachments.put(r,groupName);
                });
                return new PermissionManager(groupName);
            } else {
                return null;
            }
        }
    }
    private PermissionManager(String name) {
        this.groupName = name;
        this.attachedGroup = permissionYAML.getStringList( "group." + groupName + ".Attach");
        this.mirror = permissionYAML.getString( "group." + groupName + ".Mirror");
        this.nextGroup = permissionYAML.getString( "group." + groupName + ".NextGroup");

        mirrorPermission();
        loadPermission();
    }

    private void mirrorPermission() {
        if (mirror.isBlank()) return;
        PermissionManager mirrorGroup = getPermissionGroup(mirror);
        if (mirrorGroup == null) {
            return;
        }
        groupProperties.putAll(mirrorGroup.groupProperties);
        dollProperties.putAll(mirrorGroup.dollProperties);
        dollDefaultSettings.putAll(mirrorGroup.dollDefaultSettings);
        dollAvailableFlags.putAll(mirrorGroup.dollAvailableFlags);
        playerDefaultSettings.putAll(mirrorGroup.playerDefaultSettings);
        playerAvailableFlags.putAll(mirrorGroup.playerAvailableFlags);
    }

    private void loadPermission() {
        groupProperties.putAll(permissionYAML.getConfigurationSection("group." + groupName + ".groupProperty").getValues(false));
        dollProperties.putAll(permissionYAML.getConfigurationSection("group." + groupName + ".dollProperty").getValues(false));
        addToMap(dollDefaultSettings,"group." + groupName + ".dollDefaultSetting");
        addToMap(dollAvailableFlags,"group." + groupName + ".dollAvailableFlag");
        addToMap(playerDefaultSettings,"group." + groupName + ".playerDefaultSetting");
        addToMap(playerAvailableFlags,"group." + groupName + ".playerAvailableFlag");
    }

    private void addToMap(Map<String,Boolean> map, String path) {
        if (!permissionYAML.contains(path)) {
            return;
        }
        permissionYAML.getConfigurationSection(path).getValues(false).forEach((k,o)->{
            map.put(k,(boolean)o);
        });
    }
    public static void addPlayerExtern(Player player, String groupName) {
        if (!externalPerm) return;
        if (groupName.isBlank()) return;
        PermissionManager perm = getPermByExternalGroup(groupName);
        if (perm == null) {
            return;
        }
        if (perm.groupName.equalsIgnoreCase("default")) {
            return;
        }
        playerPermissions.put(player.getUniqueId(),perm.groupName);
    }
    public static void removePlayer(Player player) {
        playerPermissions.remove(player.getUniqueId());
    }
    public static PermissionManager getPlayerPermission(UUID uuid) {
        return getPermissionGroup(playerPermissions.getOrDefault(uuid, "default"));
    }
    public static PermissionManager getPlayerPermission(Player player) {
        return getPlayerPermission(player.getUniqueId());
    }
    public static String[] getAttached(String groupName) {
        PermissionManager group = getPermissionGroup(groupName);
        return group == null ? null : group.attachedGroup.toArray(String[]::new);
    }
    public String[] getAttached(PermissionManager group) {
        return group.attachedGroup.toArray(String[]::new);
    }
    public static boolean upgradePerm(Player player) {
        if (externalPerm) return false;
        PermissionManager perm = getPlayerPermission(player);
        if (perm == null || perm.nextGroup == null || perm.nextGroup.isBlank()) {
            return false;
        }
        playerPermissions.put(player.getUniqueId(), perm.nextGroup);
        List<String> oldList = offlinePlayerPerm.getStringList(perm.groupName);
        oldList.remove(player.getUniqueId().toString());
        offlinePlayerPerm.set(perm.groupName,oldList);
        List<String> nextList = offlinePlayerPerm.getStringList(perm.nextGroup);
        nextList.add(player.getUniqueId().toString());
        offlinePlayerPerm.set(perm.nextGroup,nextList);
        return true;
    }
}
