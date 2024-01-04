package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

public class Give extends SubCommand {
    public Give(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            return;
        }
        if (!permissionManager.canGiveDoll) {
            return;
        }
        if (checkArgumentValid(ArgumentType.ALL_DOLL,args[0])) return;
        if (Bukkit.getPlayer(args[0]) == null || !Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() || args[0].equalsIgnoreCase(sender.getName())) {
            return;
        }
        if (!dollConfig.getString("Owner.UUID").equalsIgnoreCase(sender.getUniqueId().toString())) {
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PermissionManager perm = PermissionManager.getOfflinePlayerPermission(target);
        int count = PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0);
        if (perm.maxDollCreation >= PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0)) {
            return;
        }
        PlayerDoll.playerDollCountMap.put(target.getUniqueId(),count+1);
        dollConfig.set("Owner.Name",target.getName());
        dollConfig.set("Owner.UUID",target.getUniqueId());
        dollConfig.set("Owner.Perm", perm.groupName);
        dollYAML.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver",'&', new Pair<>("%a%", target.getName())));
        if (target.isOnline()) {
            ((Player)target).sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter", '&', new Pair<>("%a%", player.getName())));
        }
    }
}
