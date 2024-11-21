package me.autobot.playerdoll.api.command.subcommand.builtin.misc;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Version extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    public Version() {
        super((Player) null);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        this.sender = sender;
        execute();
        return 1;
    }

    @Override
    public void execute() {
        sender.sendMessage(LangFormatter.YAMLReplaceMessage("ver-query", PlayerDollAPI.getInstance().getDescription().getVersion()));
    }
}
