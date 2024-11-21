package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import me.autobot.playerdoll.api.inv.gui.DollBackpackMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Inv extends SubCommand implements DollCommandExecutor {
    private Player sender;
    public Inv(Player target) {
        super(target);
    }

    @Override
    public void execute() {
        sender.openInventory(DollMenuHolder.HOLDERS.get(target.getUniqueId()).inventoryStorage.get(DollBackpackMenu.class).get(0).getInventory());
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        this.sender = playerSender;
        if (target == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.INV)) {
            return 0;
        }

        execute();
        return 1;
    }
}
