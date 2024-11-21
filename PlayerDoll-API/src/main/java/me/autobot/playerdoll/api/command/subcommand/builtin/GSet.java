package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import me.autobot.playerdoll.api.inv.gui.DollGSetMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GSet extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    private final PersonalFlagButton flagType;
    private final boolean toggle;
    public GSet(Player target) {
        super(target);
        flagType = null;
        toggle = false;
    }

    public GSet(Player target, PersonalFlagButton flagType, boolean toggle) {
        super(target);
        this.flagType = flagType;
        this.toggle = toggle;
    }

    @Override
    public void execute() {
        if (flagType == null) {
            openGUI();
            return;
        }
        DollConfig config = DollConfig.getOnlineConfig(target.getUniqueId());
        config.generalSetting.put(flagType, toggle);
        if (flagType == PersonalFlagButton.HIDDEN) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (DollStorage.ONLINE_DOLLS.containsKey(player.getUniqueId())) {
                    return;
                }
                if (!player.isOp() || (player.isOp() && !PlayerDollAPI.getConfigLoader().getBasicConfig().opCanSeeHiddenDoll.getValue())) {
                    if (toggle) {
                        player.hidePlayer(PlayerDollAPI.getInstance(), target);
                    } else {
                        player.showPlayer(PlayerDollAPI.getInstance(), target);
                    }
                }
            });
        }
    }
    private void openGUI() {
        if (sender instanceof Player player) {
            player.openInventory(DollMenuHolder.HOLDERS.get(target.getUniqueId()).inventoryStorage.get(DollGSetMenu.class).get(0).getInventory());
        }
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
//        if (!(sender instanceof Player playerSender)) {
//            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
//            return 0;
//        }
        this.sender = sender;
        if (target == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.GSET)) {
            return 0;
        }

        execute();
        return 1;
    }
}
