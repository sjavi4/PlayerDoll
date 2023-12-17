package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollHelper;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (validator.isDollAlreadyOnline()) return;
        if (validator.isDollConfigNotExist()) return;

        var yaml = YAMLManager.loadConfig(dollName,false);
        if (yaml == null) return;
        var config = yaml.getConfig();


        var map = config.getConfigurationSection("setting").getValues(false);
        var flag = ConfigManager.getFlag();
        if (flag != null) {
            Map<String,Object> configs = flag.getConfigurationSection("GlobalFlags").getValues(false);
            configs.keySet().forEach(k -> map.putIfAbsent(k, flag.getBoolean("GlobalFlags."+k+".Default")));
            config.set("setting",map);
            yaml.saveConfig();
        }
        config = yaml.reloadConfig().getConfig();
        //profile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(name), name);
        inGrid = args != null && ((String[]) args)[0].equalsIgnoreCase("inGrid");
        if (config.getBoolean("Remove")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnRemovedDoll",'&'));
            return;
        }
        if (!player.isOp() && Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("ServerReachMaxPlayer",'&'));
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

    @Override
    public final ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>();
        set.addAll(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        getOnlineDoll().forEach(set::remove);
        return new ArrayList<>(){{addAll(set);}};
    }
    @Override
    public List<Object> tabSuggestion() {
        return List.of("inGrid");
    }
}
