package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.socket.SocketHelper;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Despawn extends SubCommand implements DollCommandExecutor {
    private Player sender;
    public Despawn(Player target) {
        super(target);
    }

    @Override
    public void execute() {
        DollManager.ONLINE_DOLLS.get(target.getUniqueId()).dollDisconnect();
        String disconnect = String.format("Doll %s Despawned by %s", target.getName(), sender.getName());
        PlayerDoll.LOGGER.info(disconnect);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        if (target == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        this.sender = playerSender;
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.DESPAWN)) {
            return 0;
        }
        execute();
        return 1;
    }
}
