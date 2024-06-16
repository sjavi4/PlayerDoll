package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tp extends SubCommand implements DollCommandExecutor {
    private final boolean center;
    private Player sender;
    public Tp(Player target, boolean center) {
        super(target);
        this.center = center;
    }

    @Override
    public void execute() {
        String tpToPlayer = String.format("tp %s %s", target.getName(), sender.getName());
        Runnable toPlayerTask = () -> PlayerDoll.sendServerCommand(tpToPlayer);

        Location loc = sender.getLocation();
        String tpToBlock = String.format("tp %d %d %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        Runnable toBlockTask = () ->PlayerDoll.sendServerCommand(tpToBlock);


        if (sender.getWorld() == target.getWorld()) {
            if (center) {
                PlayerDoll.scheduler.globalTask(toBlockTask);
            } else {
                PlayerDoll.scheduler.globalTask(toPlayerTask);
            }
            return;
        }
        PlayerDoll.scheduler.globalTask(toPlayerTask);
        if (center) {
            PlayerDoll.scheduler.globalTask(toBlockTask);
        }
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

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.TP)) {
            return 0;
        }

        execute();
        return 1;
    }
}
