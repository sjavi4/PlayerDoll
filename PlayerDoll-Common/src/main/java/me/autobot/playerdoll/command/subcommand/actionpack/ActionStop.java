package me.autobot.playerdoll.command.subcommand.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
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
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }

        if (target == null) {
            if (self) {
                targetEntity = DollManager.ONLINE_PLAYERS.get(playerSender.getUniqueId());
            } else {
                playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
                return 0;
            }
        } else {
            targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        }
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        // Converted Player does not have config
        if (targetEntity.isDoll()) {
            if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.STOP)) {
                return 0;
            }
        }
        execute();
        return 1;
    }
}
