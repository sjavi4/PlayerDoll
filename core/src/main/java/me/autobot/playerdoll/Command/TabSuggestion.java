package me.autobot.playerdoll.Command;

import com.google.common.reflect.ClassPath;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TabSuggestion <T extends SubCommand> implements TabCompleter {
    @Override
    @SuppressWarnings("unchecked")
    public final List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        var argument = Arrays.stream(args).map(String::toLowerCase).toList();
        T classInstance = null;
        if (argument.size() > 1) {
            try {
                Class<?> commandClass = Class.forName(this.getClass().getPackageName() + ".Execute." + argument.get(0));
                classInstance = (T) commandClass.getConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                return Collections.singletonList("?");
            }
        }
        switch (argument.size()) {
            case 1 -> {
                ClassLoader classLoader = getClass().getClassLoader();
                try {
                    String path = this.getClass().getPackageName() + ".Execute";
                    List<String> classes = ClassPath.from(classLoader).getTopLevelClasses(path).stream().map(ClassPath.ClassInfo::getSimpleName).toList();
                    return trimSuggestion(classes, argument.get(0));
                } catch (IOException ignored) {
                    return null;
                }
            }
            case 2 -> {
                if (commandSender instanceof Player player) {
                    ArrayList<String> targets = classInstance.targetSelection(player.getUniqueId());
                    return trimSuggestion(targets, argument.get(1));
                }
                return null;
            }
            default -> {
                List<Object> suggestion = classInstance.tabSuggestion();
                int index = argument.size() - 3;
                String currentTyping = argument.get(argument.size()-1);
                if (suggestion == null || suggestion.size() <= index) return Collections.singletonList("?");
                if (suggestion.get(index) instanceof String value) {
                    return trimSuggestion(List.of(value), currentTyping);
                } else if (suggestion.get(index) instanceof List<?> value) {
                    return trimSuggestion(value.stream().map(Object::toString).toList(), currentTyping);
                } else {
                    return Collections.singletonList("?");
                }
            }
        }
    }
    private List<String> trimSuggestion(List<String> list, String startString) {
        if (startString == null || startString.isBlank() || startString.isEmpty()) return list;
        return list.stream().filter(p -> p.startsWith(startString)).toList();
    }
}
