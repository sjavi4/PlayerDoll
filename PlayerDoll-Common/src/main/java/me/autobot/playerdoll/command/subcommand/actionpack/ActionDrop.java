package me.autobot.playerdoll.command.subcommand.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionDrop extends SubCommand implements DollCommandExecutor {

    private final int slotId;
    private final boolean self;
    private BaseEntity targetEntity;
    private boolean dropStack;
    public ActionDrop(Player target, int slotId, boolean selfIndicate) {
        super(target);
        this.slotId = slotId;
        self = selfIndicate;
    }

    @Override
    public void execute() {
        PlayerDoll.scheduler.entityTask(() -> targetEntity.getActionPack().drop(slotId, dropStack), targetEntity.getBukkitPlayer());
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
        dropStack = context.getInput().split(" ")[1].equals("dropStack"); // doll <action> <target> ...

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        // Converted Player does not have config
        if (targetEntity.isDoll()) {
            if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.DROP)) {
                return 0;
            }
        }
        execute();
        return 1;
    }
}
