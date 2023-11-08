package me.autobot.playerdoll.newCommand.Others;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class CommandHelp implements CommandExecutor, TabCompleter {
    final ArrayList<String> index = new ArrayList<>();
    final ArrayList<String> desc = new ArrayList<>();
    final ArrayList<String> usage = new ArrayList<>();

    public CommandHelp() {
        var command = YAMLManager.getConfig("lang").getConfigurationSection("helpCommand").getValues(true).keySet();
        for (int i = 0; i < command.size(); i = i + 3) {
            index.add((String) command.toArray()[i]);
            desc.add((String) command.toArray()[i + 1]);
            usage.add((String) command.toArray()[i + 2]);
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            int page = 0;
            args = args == null || args.length == 0 ? new String[]{"1"} : args;
            int max = (int) Math.ceil(index.size()/10.0);
            try {
                page = Integer.parseInt(args[0]) - 1;
                if (page < 0 || page >= max) page = 0;
            } catch (NumberFormatException ignored) {
            }
            //p.sendMessage(LangFormatter.YAMLReplace("commandPage.header",'&'));
            for (int i = 10 * page; i < Math.min(10+10*page ,index.size()); i++) {
                TextComponent hoverText = new TextComponent(ChatColor.BOLD +""+ ChatColor.GOLD + index.get(i) + ChatColor.DARK_GREEN + " : " + LangFormatter.YAMLReplace("helpCommand." + desc.get(i),'&'));
                ComponentBuilder componentBuilder = new ComponentBuilder();
                String string = LangFormatter.YAMLReplace( "helpCommand." + usage.get(i),'&');
                if (string.contains("$n")) {
                    String[] splited = string.split("\\$n");
                    for (String sp : splited) componentBuilder = componentBuilder.append(sp + "\n");
                } else componentBuilder = componentBuilder.append(LangFormatter.YAMLReplace( "helpCommand." + usage.get(i),'&'));

                hoverText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
                hoverText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/doll ? "+ index.get(i)+ " "));
                p.spigot().sendMessage(hoverText);
            }
            p.sendMessage(LangFormatter.YAMLReplace("commandPage.footer",'&',new Pair<>("%a%",String.valueOf(page+1)),new Pair<>("%b%", String.valueOf(max))));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return List.of("<page>");
    }
}
