package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Slot extends SubCommand {

    public Slot(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.setSlot(1);
            return;
        }
        if (checkArgumentValid(ArgumentType.HOTBAR_SLOT, args[0])) {
            actionPack.setSlot(castArgument(args[0], Integer.class));
        }
    }
}