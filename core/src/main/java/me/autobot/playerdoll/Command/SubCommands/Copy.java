package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.entity.Player;

public class Copy extends SubCommand {
    public Copy(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMustSpecifyTargetDoll"));
            return;
        }
        if (checkArgumentValid(ArgumentType.ONLINE_DOLL,args[0])) {
            actionPack.copyFrom(PlayerDoll.dollManagerMap.get(args[0]));
        }
    }
}
