package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Sprint extends SubCommand {

    public Sprint(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.setSprinting(!dollPlayer.isSprinting());
            return;
        }
        if (checkArgumentValid(ArgumentType.BOOLEAN,args[0])) {
            actionPack.setSprinting(castArgument(args[0], Boolean.class));
        }
    }
}