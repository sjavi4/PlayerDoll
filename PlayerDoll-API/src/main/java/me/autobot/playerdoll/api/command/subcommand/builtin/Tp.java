package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
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
//        String tpToPlayer = String.format("tp %s %s", target.getName(), sender.getName());
//        Runnable toPlayerTask = () -> PlayerDoll.sendServerCommand(tpToPlayer);
//
//        Location loc = sender.getLocation();
//        String tpToBlock = String.format("tp %d %d %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//        Runnable toBlockTask = () ->PlayerDoll.sendServerCommand(tpToBlock);

        Location o = sender.getLocation();
        if (center) {
            o.setX(o.getBlockX() + 0.5);
            o.setZ(o.getBlockZ() + 0.5);
        }
        if (PlayerDollAPI.getServerBranch() == AbsServerBranch.FOLIA) {
            PlayerDollAPI.getScheduler().foliaTeleportAync(target, o);
        } else {
            target.teleport(o);
        }


//        if (sender.getWorld() == target.getWorld()) {
//            if (center) {
//                PlayerDollAPI.getScheduler().globalTask(toBlockTask);
//            } else {
//                PlayerDollAPI.getScheduler().globalTask(toPlayerTask);
//            }
//            return;
//        }
//        PlayerDollAPI.getScheduler().globalTask(toPlayerTask);
//        if (center) {
//            PlayerDollAPI.getScheduler().globalTask(toBlockTask);
//        }
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

        if (!outputHasPerm(playerSender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.TP)) {
            return 0;
        }

        execute();
        return 1;
    }
}
