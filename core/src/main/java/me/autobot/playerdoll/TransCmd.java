package me.autobot.playerdoll;

import me.autobot.playerdoll.CustomEvent.PlayerTransformEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        Bukkit.getPluginManager().callEvent(new PlayerTransformEvent(player));

        return true;
    }
}
