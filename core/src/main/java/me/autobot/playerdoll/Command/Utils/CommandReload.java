package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p && p.isOp()) {
            p.sendMessage(LangFormatter.YAMLReplaceMessage("ReloadPlugin",'&'));
            PlayerDoll.dollManagerMap.values().forEach(i -> i.getConfigManager().save());
            PlayerDoll.configManager = new ConfigManager(PlayerDoll.getPlugin());
            //YAMLManager.reloadAllConfig();
        }
        return true;
    }
}
