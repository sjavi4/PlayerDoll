package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Look extends SubCommand {

    public Look(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMustContainArgument"));
            return;
        }
        switch (args.length) {
            case 1 -> {
                String value = args[0];
                if (checkArgumentValid(ArgumentType.DIRECTION,value)) {
                    actionPack.look(value);
                } else if (checkArgumentValid(ArgumentType.ONLINE_PLAYER,value)) {
                    actionPack.look(Bukkit.getPlayer(value));
                }
            }
            case 2 -> {
                ArgumentType argumentType = ArgumentType.SIGNED_FLOAT;
                String sYaw = args[0];
                String sPitch = args[1];
                float yaw = 0;
                float pitch = 0;
                if (checkArgumentValid(argumentType, sYaw)) yaw = castArgument(sYaw, Float.class);
                if (checkArgumentValid(argumentType, sPitch)) pitch = castArgument(sPitch, Float.class);
                actionPack.look(yaw, pitch);
            }
        }
    }
}