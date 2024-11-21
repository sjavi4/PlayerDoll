package me.autobot.playerdoll.api.command.subcommand.builtin.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
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
        PlayerDollAPI.getScheduler().entityTask(() -> targetEntity.getActionPack().drop(slotId, dropStack), targetEntity.getBukkitPlayer());
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
//        if (!(sender instanceof Player playerSender)) {
//            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
//            return 0;
//        }

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
        dropStack = context.getInput().split(" ")[1].equals("dropStack"); // doll <action> <target> ...

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        // Converted Player does not have config
        if (targetEntity.isDoll()) {
            if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.DROP)) {
                return 0;
            }
        }
        execute();
        return 1;
    }
}
