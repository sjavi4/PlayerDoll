package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandTabManager implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<List<String>> list = null;
        if (args.length >= 3) {
            list = CommandManager.subCommandMap.get(args[1]).commandList();
        }
        switch (args.length) {
            case 1 -> {
                List<String> list1 = new ArrayList<>();
                list1.addAll(CommandManager.directCommandMap.keySet());
                list1.addAll(PlayerDoll.dollManagerMap.keySet().stream().map(s1 -> s1.substring(PlayerDoll.getDollPrefix().length())).toList());
                //if ((boolean) PlayerDoll.getGlobalConfig().get("AllowActionToPlayer")) {
                //    list1.add(commandSender.getName());
                //}
                return list1;
            }
            case 2 -> {
                return CommandManager.subCommandMap.keySet().stream().sorted().toList();
                //return Stream.of(EntityPlayerActionPack.ActionType.values()).map(EntityPlayerActionPack.ActionType::name).toList();
            }
        }
        if (args.length > 2) {
            return list != null && list.size() > args.length-3 ? list.get(args.length-3) : Collections.emptyList();
        }
        return null;
    }
}
