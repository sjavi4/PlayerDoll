package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class CommandList implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            File dollDirectory = new File(PlayerDoll.getDollDirectory());
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".yml");
                }
            };
            int page = 0;
            args = args == null || args.length == 0 ? new String[]{"1"} : args;
            var filterlist = dollDirectory.list(filter);
            if (filterlist == null || filterlist.length == 0) return true;
            int max = (int) Math.ceil(filterlist.length/10.0);
            try {
                page = Integer.parseInt(args[0]) - 1;
                if (page < 0 || page >= max) page = 0;
            } catch (NumberFormatException ignored) {
            }
            for (int i = 10 * page; i < Math.min(10+10*page , filterlist.length); i++) {
                var color = ChatColor.GREEN;
                String name = filterlist[i].substring(0,filterlist[i].length()-4);
                if (!PlayerDoll.dollManagerMap.containsKey(name)) color = ChatColor.GRAY;
                //var yaml = ConfigManager.configs.get(ConfigType.CONFIG);
                var config = YAMLManager.loadConfig(name,false);
                var yaml = config.getConfig();
                var removed = yaml.getBoolean("Remove")? ChatColor.STRIKETHROUGH : "";
                TextComponent hoverText = new TextComponent( i+1 + ". " + color + removed + name.substring(1));
                hoverText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(LangFormatter.YAMLReplace("commandList.creationDate",'&',new Pair<>("%a%",yaml.getString("Timestamp")))+"\n")
                                .append(LangFormatter.YAMLReplace("commandList.owner",'&',new Pair<>("%a%",yaml.getString("Owner.Name")))+"\n")
                                .append(LangFormatter.YAMLReplace("commandList.skin",'&',new Pair<>("%a%",yaml.getString("SkinData.Name")))+"\n")
                                .create()));
                config.unloadConfig();
                //hoverText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/doll ? "+ index.get(i)));
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
