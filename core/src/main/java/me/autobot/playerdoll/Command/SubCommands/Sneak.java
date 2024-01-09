package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Sneak extends SubCommand {

    public Sneak(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.setSneaking(!dollPlayer.isSneaking());
            return;
        }
        if (checkArgumentValid(ArgumentType.BOOLEAN,args[0])) {
            actionPack.setSneaking(castArgument(args[0], Boolean.class));
        }
    }
}