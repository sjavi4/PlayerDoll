package me.autobot.playerdoll.newCommand.Others;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
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

public class CommandReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p && p.isOp()) {
            p.sendMessage(LangFormatter.YAMLReplace("ReloadPlugin",'&'));
            PlayerDoll.dollManagerMap.values().forEach(i -> i.getConfigManager().save());
            PlayerDoll.configManager = new ConfigManager(PlayerDoll.getPlugin());
            //YAMLManager.reloadAllConfig();
        }
        return true;
    }
}
