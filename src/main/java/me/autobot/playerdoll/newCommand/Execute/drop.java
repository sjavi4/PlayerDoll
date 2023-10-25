package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class drop extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;
    public drop(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        if (!isOnline(dollName)) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        EntityPlayerActionPack.Action action = EntityPlayerActionPack.Action.once();
        int slot = -1;
        boolean start = false;
        boolean stack = args.length >= 1 && args[1].equalsIgnoreCase("stack");

        switch (args[0].toLowerCase()) {
            case "once" -> start = true;
            case "continuous" -> {
                start = true;
                action = EntityPlayerActionPack.Action.continuous();
            }
            case "interval" -> {
                start = true;
                if (args.length >= 4) {
                    try {
                        action = EntityPlayerActionPack.Action.interval(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
                    }
                } else if (args.length >= 3) {
                    try {
                        action = EntityPlayerActionPack.Action.interval(Integer.parseInt(args[2]));
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
                    }
                }
            }
            case "helmet" -> slot = 39;
            case "chestplate" -> slot = 38;
            case "leggings" -> slot = 37;
            case "boots" -> slot = 36;
            case "offhand" -> slot = 40;
            case "everything" -> slot = -2;
            default -> {
                try {
                    int num = Integer.parseInt(args[0]);
                    if (num < 1 || num > 36) return;
                    slot = num - 1;
                } catch (NumberFormatException ignored) {
                    player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
                }
            }
        };
        if (start) {
            doll.getActionPack().start(stack ? EntityPlayerActionPack.ActionType.DROP_STACK : EntityPlayerActionPack.ActionType.DROP_ITEM, action);
        } else {
            doll.getActionPack().drop(slot, stack);
        }
    }

    public static List<Object> tabSuggestion() {
        return List.of(
                List.of("{1..36}","helmet","chestplate","leggings","boots","offhand","everything","once","interval","continuous"),
                List.of("stack"),
                List.of("<[tick:interval]>"),
                List.of("<[tick:offset]>")
        );
    }
}