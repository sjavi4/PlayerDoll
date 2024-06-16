package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Info extends SubCommand implements DollCommandExecutor {
    public Info(Player target) {
        super(target);
    }

    @Override
    public void execute() {

    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        return 0;
    }
}
