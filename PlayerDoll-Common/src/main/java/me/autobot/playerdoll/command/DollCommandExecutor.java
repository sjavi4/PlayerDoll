package me.autobot.playerdoll.command;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.command.CommandSender;

public interface DollCommandExecutor {
    int onCommand(CommandSender sender, CommandContext<Object> context);
}
