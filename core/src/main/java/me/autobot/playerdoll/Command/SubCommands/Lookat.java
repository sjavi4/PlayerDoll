package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class Lookat extends SubCommand {

    public Lookat(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMustContainArgument"));
            return;
        }
        EntityPlayerActionPack.ActionType actionType = EntityPlayerActionPack.ActionType.LOOK_AT;
        switch (args.length) {
            case 1 -> {
                String value = args[0];
                if (value.equals("target")) {
                    Location loc = player.getEyeLocation();
                    RayTraceResult result = player.getWorld().rayTraceEntities(loc,loc.getDirection(), 3.5, entity -> !(entity instanceof Player));
                    if (result != null && result.getHitEntity() != null) {
                        actionPack.lookAt(result.getHitEntity());
                    }
                } else if (checkArgumentValid(ArgumentType.ONLINE_PLAYER,value)) {
                    actionPack.look(Bukkit.getPlayer(value));
                }
            }
            case 2 -> {
                String value = args[0];
                if (value.equals("target")) {
                    executeAction(args,1,actionType);
                }
            }
            case 3 -> {
                if (args[0].equals("target")) {
                    executeAction(args,1,actionType);
                } else {
                    String sX = args[0];
                    String sY = args[1];
                    String sZ = args[2];
                    float x = 0;
                    float y = 0;
                    float z = 0;
                    ArgumentType argumentType = ArgumentType.COORDINATE;
                    if (checkArgumentValid(argumentType, sX)) x = castArgument(sX, Float.class);
                    if (checkArgumentValid(argumentType, sY)) y = castArgument(sY, Float.class);
                    if (checkArgumentValid(argumentType, sZ)) z = castArgument(sZ, Float.class);
                    actionPack.lookAt(x, y, z);
                }
            }
            case 4 -> {
                if (args[0].equals("target")) {
                    executeAction(args,1,actionType);
                }
            }
        }
    }
}