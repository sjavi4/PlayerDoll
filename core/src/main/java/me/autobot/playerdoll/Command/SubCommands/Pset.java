package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.GUIs.Doll.DollInvStorage;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Pset extends SubCommand {

    public Pset(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMustSpecifyTargetPlayer"));
            return;
        }
        if (checkArgumentValid(ArgumentType.ONLINE_DOLL,args[0])) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandTargetCannotBeDoll"));
            return;
        }
        if (checkArgumentValid(ArgumentType.ALL_PLAYER,args[0])) {
            if (PlayerDoll.dollInvStorage.containsKey(dollName)) {
                sender.openInventory(PlayerDoll.dollInvStorage.get(dollName).getPSetPage(Bukkit.getOfflinePlayer(args[0])));
            } else {
                PlayerDoll.dollInvStorage.put(dollName,DollInvStorage.offlineInstance(dollName));
                sender.openInventory(PlayerDoll.dollInvStorage.get(dollName).getPSetPage(Bukkit.getOfflinePlayer(args[0])));
            }
        }
    }
}
