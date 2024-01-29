package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Drop extends SubCommand {
    public Drop(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.drop(-1,false);
            return;
        }
        EntityPlayerActionPack.ActionType actionType = EntityPlayerActionPack.ActionType.DROP_ITEM;
        switch (args.length) {
            case 1 -> {
                actionPack.drop(-1, args[0].equalsIgnoreCase("stack"));
            }
            case 2 -> {
                if (checkArgumentValid(ArgumentType.INVENTORY_SLOT,args[1])) {
                    if (checkArgumentValid(ArgumentType.POSITIVE_INTEGER, args[1])) {
                        actionPack.drop(castArgument(args[1], Integer.class)-1, args[0].equalsIgnoreCase("stack"));
                    } else {
                        int slot = switch (args[1].toLowerCase()) {
                            case "helmet" -> 39;
                            case "chestplate" -> 38;
                            case "leggings" -> 37;
                            case "boots" -> 36;
                            case "offhand" -> 40;
                            case "everything" -> -2;
                            default -> -1;
                        };
                        actionPack.drop(slot, args[0].equalsIgnoreCase("stack"));
                    }
                } else {
                    if (args[0].equalsIgnoreCase("stack")) {
                        actionType = EntityPlayerActionPack.ActionType.DROP_STACK;
                    }
                    executeAction(args,1, actionType);
                }
            }
            case 3, 4, 5 -> {
                if (args[0].equalsIgnoreCase("stack")) {
                    actionType = EntityPlayerActionPack.ActionType.DROP_STACK;
                }
                executeAction(args,1, actionType);
            }
        }
    }
}
