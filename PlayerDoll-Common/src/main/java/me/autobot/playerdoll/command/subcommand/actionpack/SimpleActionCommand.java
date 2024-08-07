package me.autobot.playerdoll.command.subcommand.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.carpetmod.EntityPlayerActionPack;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SimpleActionCommand extends SubCommand implements DollCommandExecutor {

    private final Consumer<EntityPlayerActionPack> consumer;
    private final FlagConfig.PersonalFlagType type;
    private BaseEntity targetEntity;
    public SimpleActionCommand(Player target, Consumer<EntityPlayerActionPack> consumer, FlagConfig.PersonalFlagType type) {
        super(target);
        this.consumer = consumer;
        this.type = type;
    }

    @Override
    public void execute() {
        consumer.accept(targetEntity.getActionPack());
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        if (target == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), type)) {
            return 0;
        }

        execute();
        return 1;
    }
}
