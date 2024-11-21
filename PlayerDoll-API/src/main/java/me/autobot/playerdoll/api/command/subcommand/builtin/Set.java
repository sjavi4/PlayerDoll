package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.config.key.ConfigKey;
import me.autobot.playerdoll.api.doll.*;
import me.autobot.playerdoll.api.event.doll.DollSettingEvent;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import me.autobot.playerdoll.api.inv.gui.DollSetMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Set extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    private final GlobalFlagButton flagType;
    private final boolean toggle;
    public Set(Player target) {
        super(target);
        flagType = null;
        toggle = false;
    }

    public Set(Player target, GlobalFlagButton flagType, boolean toggle) {
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
        ConfigKey<DollConfig, Boolean> setting = config.dollSetting.get(flagType);
        setting.setNewValue(toggle);

        Doll doll = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        DollSetting settings = DollSetting.SETTINGS.stream().filter(s -> s.getType() == flagType).findAny().orElseThrow();

        Bukkit.getPluginManager().callEvent(new DollSettingEvent(sender, doll, settings, toggle));
    }
    private void openGUI() {
        if (sender instanceof Player player) {
            player.openInventory(DollMenuHolder.HOLDERS.get(target.getUniqueId()).inventoryStorage.get(DollSetMenu.class).get(0).getInventory());
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

        if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.SET)) {
            return 0;
        }

        execute();
        return 1;
    }
}
