package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.Util.Configs.BasicConfig;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandHelper implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        CommandType commandType;
        if (args == null || args.length == 0) {
            return false;
        }
        try {
            commandType = CommandType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return true;
        }
        if (args.length > 1) {
            String dollName = args[1];
            String[] arguments = Arrays.copyOfRange(args,2,args.length);
            if (sender instanceof Player player) {
                if (!player.isOp()) {
                    if (commandType.checkPermission(player, dollName)) {
                        commandType.execute(player, dollName, arguments);
                    }
                } else {
                    if (dollName.equals("!") && BasicConfig.get().convertPlayer.getValue()) {
                        commandType.execute(player, dollName, arguments);
                    }else if (commandType.onlineStatus.valid(dollName)) {
                        commandType.execute(player, dollName, arguments);
                    } else {
                        player.sendMessage(LangFormatter.YAMLReplaceMessage("OnlineStatus.Invalid"));
                    }
                }
            } else if (commandType.allowConsole) {
                if (commandType.onlineStatus.valid(dollName)) {
                    commandType.execute(null, dollName, arguments);
                } else {
                    sender.sendMessage(LangFormatter.YAMLReplaceMessage("OnlineStatus.Invalid"));
                }
            } else {
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandRequirePlayer"));
            }
        }

        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command c, String s, String[] args) {
        var argument = Arrays.stream(args).map(String::toLowerCase).toList();
        if (sender instanceof Player player) {
            return trimSuggestion(SuggestionHelper.walkThrough(argument, player), argument.get(argument.size()-1));
        }
        return trimSuggestion(SuggestionHelper.walkThrough(argument, null), argument.get(argument.size()-1));
    }
    private List<String> trimSuggestion(List<String> list, String startString) {
        if (startString == null || startString.isBlank()) return list;
        return list.stream().filter(p -> p.startsWith(startString)).toList();
    }
}
