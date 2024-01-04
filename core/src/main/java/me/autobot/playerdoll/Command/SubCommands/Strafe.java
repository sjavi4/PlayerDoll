package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Strafe extends SubCommand {

    public Strafe(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.setStrafing(0);
            return;
        }
        float v = 0.0f;
        if (args[0].equalsIgnoreCase("left")) {
            v = 1.0f;
        } else if (args[0].equalsIgnoreCase("right")) {
            v = -1.0f;
        }
        actionPack.setStrafing(v);
    }
}