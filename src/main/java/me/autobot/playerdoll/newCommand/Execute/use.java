package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class use extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;

    public use(CommandSender sender, Object doll, Object args) {
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
        var action = switch (args[0].toLowerCase()) {
            case "continuous" -> EntityPlayerActionPack.Action.continuous();
            case "interval" -> {
                if (args.length >= 3) {
                    try {
                        yield EntityPlayerActionPack.Action.interval(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
                    }
                } else if (args.length >= 2) {
                    try {
                        yield EntityPlayerActionPack.Action.interval(Integer.parseInt(args[1]));
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
                    }
                }
                yield EntityPlayerActionPack.Action.once();
            }
            default -> EntityPlayerActionPack.Action.once();
        };
        doll.getActionPack().start(EntityPlayerActionPack.ActionType.USE, action);
    }

    public static List<Object> tabSuggestion() {
        return List.of(
                List.of("once","interval","continuous"),
                List.of("<[tick:interval]>"),
                List.of("<[tick:offset]>")
        );
    }
}