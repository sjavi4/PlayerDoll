package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Menu extends SubCommand implements DollCommandExecutor {
    private Player sender;
    public Menu(Player target) {
        super(target);
    }

    @Override
    public void execute() {
        sender.openInventory(DollGUIHolder.DOLL_GUI_HOLDERS.get(target.getUniqueId()).menus.get(DollGUIHolder.MenuType.INFO).getInventory());
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
        BaseEntity targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.MENU)) {
            return 0;
        }

        execute();
        return 1;
    }
}
