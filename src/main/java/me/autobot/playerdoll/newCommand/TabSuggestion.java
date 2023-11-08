package me.autobot.playerdoll.newCommand;

import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TabSuggestion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        var argument = Arrays.stream(args).map(String::toLowerCase).toList();
        switch (argument.size()) {
            case 1 -> {
                if (argument.get(0).length() == 0) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(argument.get(0))).toList();
            }
            case 2 -> {
                ClassLoader classLoader = getClass().getClassLoader();
                try {
                    String path = this.getClass().getPackageName() + ".Execute";
                    Set<ClassPath.ClassInfo> classes = ClassPath.from(classLoader).getTopLevelClasses(path);
                    if (argument.get(1).length() == 0) return classes.stream().map(clazz -> clazz.getName().substring(path.length() + 1)).toList();
                    return classes.stream()
                            .map(clazz -> clazz.getName().substring(path.length()+1))
                            .filter(p -> p.startsWith(argument.get(1)))
                            .toList();
                } catch (IOException ignored) {
                    return null;
                }
            }
            default -> {
                try {
                    Class<?> clazz = Class.forName(this.getClass().getPackageName() + ".Execute." + argument.get(1));
                    List<?> suggestion = (List<?>) clazz.getMethod("tabSuggestion").invoke(null);
                    int index = argument.size() - 3;
                    if (suggestion == null || suggestion.size() <= index) return Collections.singletonList("?");
                    if (suggestion.get(index) instanceof String value) {
                        return List.of(value);
                    } else if (suggestion.get(index) instanceof List<?> value) {
                        return value.stream().map(Object::toString).toList();
                    } else {
                        return Collections.singletonList("?");
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |InvocationTargetException ignored) {
                    return null;
                }
            }
        }
    }
}
