package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class Spawn extends SubCommand {

    public Spawn(Player sender, String dollName, String[] args) {
        super(sender, dollName);

        if (PlayerDoll.dollInvStorage.containsKey(dollName)) {
            PlayerDoll.dollInvStorage.get(dollName).closeOfflineInv();
        }
        dollYAML.reloadConfig();

        int serverMaxDoll = ConfigManager.getConfig().getInt("Global.ServerMaxDoll");
        if (serverMaxDoll > -1 && !sender.isOp() && DollManager.ONLINE_DOLL_MAP.size() >= serverMaxDoll) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("DollCapacityIsFull"));
            return;
        }
        String ownerUUID = dollConfig.getString("Owner.UUID");
        Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
        if (owner != null) {
            PermissionManager perm = PermissionManager.getPlayerPermission(owner);
            if (!perm.groupName.equals(permissionManager.groupName)) {
                permissionManager = perm;
                dollConfig.set("Owner.Perm",perm.groupName);
                DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                dollConfig.set("LastSpawn",date.format(new Date(System.currentTimeMillis())));
                dollYAML.saveConfig();
            }
        }

        Map<String, Object> map = dollConfig.getConfigurationSection("setting").getValues(false);
        permissionManager.dollDefaultSettings.forEach(map::putIfAbsent);

        if (!player.isOp()) {
            int count = 0;
            for (IDoll d : DollManager.ONLINE_DOLL_MAP.values()) {
                if (d.getConfigManager().config.getString("Owner.UUID").equals(ownerUUID)) {
                    count++;
                }
            }
            int maxDollSpawn = (int) permissionManager.groupProperties.get("maxDollSpawn");
            if (count >= maxDollSpawn) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll",maxDollSpawn));
                return;
            }
        }
        if (!player.isOp() && !(boolean)permissionManager.groupProperties.get("bypassMaxPlayer") && Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("ServerReachMaxPlayer"));
            return;
        }
        dollConfig = dollYAML.reloadConfig().getConfig();
        UUID configUUID = UUID.fromString(dollConfig.getString("UUID"));
        DollManager.getInstance().spawnDoll(dollName, configUUID,sender,(args != null && args.length > 0 && checkArgumentValid(ArgumentType.ALIGN_IN_GRID,args[0])));
    }
}
