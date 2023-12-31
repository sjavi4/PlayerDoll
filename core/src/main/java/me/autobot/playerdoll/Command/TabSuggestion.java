package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.TabSuggestion.TabSuggestionHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class TabSuggestion implements TabCompleter {
    @Override
    public final List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        var argument = Arrays.stream(args).map(String::toLowerCase).toList();

        return trimSuggestion(TabSuggestionHelper.walkThrough(argument), argument.get(argument.size()-1));
    }
    private List<String> trimSuggestion(List<String> list, String startString) {
        if (startString == null || startString.isBlank()) return list;
        return list.stream().filter(p -> p.startsWith(startString)).toList();
    }
}
