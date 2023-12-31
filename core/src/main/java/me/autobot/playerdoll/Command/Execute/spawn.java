package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollHelper;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class spawn extends SubCommand {
    Player player;
    String dollName;
    boolean inGrid;
    public spawn() {
    }
    public spawn(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share,false);
        this.dollName = checkDollName(doll);
        player = (Player) sender;
        if (!checkPermission(sender, dollName, "Spawn")) return;

        DollDataValidator validator = new DollDataValidator(player,dollName,dollName.substring(1));

        YamlConfiguration globalConfig = ConfigManager.getConfig();

        var dollManagerMap = PlayerDoll.dollManagerMap;

        if (globalConfig.getInt("Global.ServerMaxDoll") != -1) {
            if (!player.isOp() && dollManagerMap.size() >= globalConfig.getInt("Global.ServerMaxDoll")) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("DollCapacityIsFull",'&'));
                return;
            }
        }

        if (validator.isDollAlreadyOnline()) return;
        if (validator.isDollConfigNotExist()) return;

        var yaml = YAMLManager.loadConfig(dollName,false);
        if (yaml == null) return;
        var config = yaml.getConfig();

        String ownerUUID = config.getString("Owner.UUID");
        Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
        if (owner != null) {
            String permission = owner.getPersistentDataContainer().get(PlayerDoll.permissionKey, PersistentDataType.STRING);
            String configPermission = config.getString("Owner.Perm");
            if (configPermission == null || configPermission.isBlank()) {
                config.set("Owner.Perm",permission);
                yaml.saveConfig();
            } else if (!configPermission.equals(permission)) {
                config.set("Owner.Perm",permission);
                yaml.saveConfig();
            }
        }

        var map = config.getConfigurationSection("setting").getValues(false);
        PermissionManager perm = PermissionManager.permissionGroupMap.get(config.getString("Owner.Perm"));
        perm.flagGlobalToggles.forEach(map::putIfAbsent);

        if (!player.isOp()) {
            int[] count = {0};
            dollManagerMap.values().forEach(d -> {
                if (ownerUUID.equals(d.getOwner().getUniqueId().toString())) {
                    count[0]++;
                }
            });
            if (count[0] >= perm.maxDollSpawn) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll", '&', new Pair<>("%a%", Integer.toString(perm.maxDollSpawn))));
                return;
            }
        }

        /*
        var flag = ConfigManager.getFlag();
        if (flag != null) {
            Map<String,Object> configs = flag.getConfigurationSection("GlobalFlags").getValues(false);
            configs.keySet().forEach(k -> map.putIfAbsent(k, flag.getBoolean("GlobalFlags."+k+".Default")));
            config.set("setting",map);
            yaml.saveConfig();
        }

         */
        config = yaml.reloadConfig().getConfig();
        //profile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(name), name);
        inGrid = args != null && ((String[]) args)[0].equalsIgnoreCase("inGrid");
        if (config.getBoolean("Remove")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnRemovedDoll",'&'));
            return;
        }
        if (!player.isOp() && !perm.bypassMaxPlayer && Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("ServerReachMaxPlayer", '&'));
            return;
        }
        execute();
    }
    @Override
    public void execute() {
        try {
            PlayerDoll.dollManagerMap.put(dollName, null);
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".entity.CraftPlayer");
            ServerPlayer serverPlayer = (ServerPlayer) player.getClass().asSubclass(craftPlayerClass).getDeclaredMethod("getHandle").invoke(player);
            IDoll doll = (IDoll) DollHelper.callSpawn(serverPlayer,dollName, PlayerDoll.version);
            if (doll != null) {
                PlayerDoll.dollManagerMap.put(dollName, doll);
                if (inGrid) {
                    var pos = player.getLocation();
                    doll._setPos(Math.round(pos.getX() * 2) / 2.0, pos.getBlockY(), Math.round(pos.getZ() * 2) / 2.0);
                }
            } else PlayerDoll.dollManagerMap.remove(dollName);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
