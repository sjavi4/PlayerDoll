package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.configkey.ConfigKey;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.event.DollSettingEvent;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Set extends SubCommand implements DollCommandExecutor {
    private Player sender;
    private final FlagConfig.GlobalFlagType flagType;
    private final boolean toggle;
    public Set(Player target) {
        super(target);
        flagType = null;
        toggle = false;
    }

    public Set(Player target, FlagConfig.GlobalFlagType flagType, boolean toggle) {
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
        DollConfig config = DollConfig.getOnlineDollConfig(target.getUniqueId());
        ConfigKey<DollConfig, Boolean> setting = config.dollSetting.get(flagType);
        setting.setNewValue(toggle);

        Doll doll = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        DollConfig.DollSettings settings = DollConfig.DollSettings.valueOf(flagType.getCommand().toUpperCase());

        PlayerDoll.callSyncEvent(new DollSettingEvent(sender, doll, settings, toggle));
    }
    private void openGUI() {
        sender.openInventory(DollGUIHolder.DOLL_GUI_HOLDERS.get(target.getUniqueId()).menus.get(DollGUIHolder.MenuType.SETTING).getInventory());
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

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.SET)) {
            return 0;
        }

        execute();
        return 1;
    }
}
