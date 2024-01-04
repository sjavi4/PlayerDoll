package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Move extends SubCommand {

    public Move(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.setForward(0);
            return;
        }
        float v = 0.0f;
        if (args[0].equalsIgnoreCase("forward")) {
            v = 1.0f;
        } else if (args[0].equalsIgnoreCase("backward")) {
            v = -1.0f;
        }
        actionPack.setForward(v);
    }
}