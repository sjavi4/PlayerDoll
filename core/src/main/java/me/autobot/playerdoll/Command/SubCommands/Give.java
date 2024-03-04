package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Util.LangFormatter;
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
        if (!dollConfig.ownerUUID.getValue().equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        /*
        PermissionManager perm = PermissionManager.getPlayerPermission(target.getUniqueId());
        int count = PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0);
        int max = (int) perm.groupProperties.get("maxDollCreation");
        if (max >= PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll",max));
            return;
        }


        PlayerDoll.playerDollCountMap.put(target.getUniqueId(),count+1);
        */
        dollConfig.ownerName.setNewValue(target.getName());
        dollConfig.ownerUUID.setNewValue(target.getUniqueId().toString());
        //dollConfig.set("Owner.Perm", perm.groupName);
        dollConfig.saveConfig();
        //dollYAML.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver",target.getName()));
        if (target.isOnline()) {
            ((Player)target).sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter",player.getName()));
        }
    }
}
