package me.autobot.playerdoll.api.command.subcommand.builtin;

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

public class Despawn extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    public Despawn(Player target) {
        super(target);
    }

    @Override
    public void execute() {
        DollStorage.ONLINE_DOLLS.get(target.getUniqueId()).dollDisconnect();
        String disconnect = String.format("Doll %s Despawned by %s", target.getName(), sender.getName());
        PlayerDollAPI.getLogger().info(disconnect);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (target == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        this.sender = sender;
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (sender instanceof Player playerSender && !outputHasPerm(playerSender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.DESPAWN)) {
            return 0;
        }
        execute();
        return 1;
    }
}
