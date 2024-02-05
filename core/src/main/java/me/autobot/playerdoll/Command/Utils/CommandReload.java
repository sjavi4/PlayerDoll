package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p && p.isOp()) {
            p.sendMessage(LangFormatter.YAMLReplaceMessage("ReloadPlugin"));
            DollManager.ONLINE_DOLL_MAP.values().forEach(i -> i.getConfigManager().save());
            PlayerDoll.configManager = new ConfigManager(PlayerDoll.getPlugin());
            //YAMLManager.reloadAllConfig();
        }
        return true;
    }
}
