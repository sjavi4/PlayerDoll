package me.autobot.playerdoll.api.command.subcommand.builtin.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionStop extends SubCommand implements DollCommandExecutor {
    private final boolean self;
    private BaseEntity targetEntity;
    public ActionStop(Player target, boolean selfIndicate) {
        super(target);
        self = selfIndicate;
    }

    @Override
    public void execute() {
        targetEntity.getActionPack().stopAll();
    }
    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (target == null) {
            if (self && sender instanceof Player playerSender) {
                targetEntity = DollStorage.ONLINE_TRANSFORMS.get(playerSender.getUniqueId());
            } else {
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
                return 0;
            }
        } else {
            targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        }
        if (targetEntity == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        // Converted Player does not have config
        if (targetEntity.isDoll()) {
            if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.STOP)) {
                return 0;
            }
        }
        execute();
        return 1;
    }
}
