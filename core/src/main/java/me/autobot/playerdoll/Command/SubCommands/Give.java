package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.Pair;
import me.autobot.playerdoll.Util.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Give extends SubCommand {
    public Give(Player sender, String dollName, String[] args) {
        super(sender, dollName);

        if (args == null || args.length == 0) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMustSpecifyTargetPlayer"));
            return;
        }
        if (checkArgumentValid(ArgumentType.ALL_DOLL,args[0])) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandTargetCannotBeDoll"));
            return;
        }
        if (Bukkit.getPlayer(args[0]) == null || !Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() || args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandTargetPlayerNotExist"));
            return;
        }
        if (!dollConfig.getString("Owner.UUID").equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PermissionManager perm = PermissionManager.getOfflinePlayerPermission(target);
        int count = PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0);
        if (perm.maxDollCreation >= PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll",new Pair<>("%a%",Integer.toString(perm.maxDollCreation))));
            return;
        }
        PlayerDoll.playerDollCountMap.put(target.getUniqueId(),count+1);
        dollConfig.set("Owner.Name",target.getName());
        dollConfig.set("Owner.UUID",target.getUniqueId());
        dollConfig.set("Owner.Perm", perm.groupName);
        dollYAML.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver", new Pair<>("%a%", target.getName())));
        if (target.isOnline()) {
            ((Player)target).sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter", new Pair<>("%a%", player.getName())));
        }
    }
}
