package me.autobot.playerdoll.api.command.subcommand.builtin.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SimpleActionCommand extends SubCommand implements DollCommandExecutor {

    private final Consumer<ActionPack> consumer;
    private final PersonalFlagButton type;
    private BaseEntity targetEntity;
    public SimpleActionCommand(Player target, Consumer<ActionPack> consumer, PersonalFlagButton type) {
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
//        if (!(sender instanceof Player playerSender)) {
//            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
//            return 0;
//        }
        if (target == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), type)) {
            return 0;
        }

        execute();
        return 1;
    }
}
