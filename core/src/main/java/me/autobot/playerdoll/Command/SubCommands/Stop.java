package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Stop extends SubCommand {

    public Stop(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.stopAll();
            return;
        }
        if (args[0].equalsIgnoreCase("movement")) {
            actionPack.stopMovement();
        } else if (args[0].equalsIgnoreCase("all")) {
            actionPack.stopAll();
        }
    }
}
