package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Turn extends SubCommand {

    public Turn(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length < 2) {
            return;
        }
        ArgumentType argumentType = ArgumentType.SIGNED_FLOAT;
        String sYaw = args[0];
        String sPitch = args[1];
        float yaw = 0;
        float pitch = 0;
        if (checkArgumentValid(argumentType, sYaw)) yaw = castArgument(sYaw, Float.class);
        if (checkArgumentValid(argumentType, sPitch)) pitch = castArgument(sPitch, Float.class);
        actionPack.turn(yaw, pitch);
    }
}