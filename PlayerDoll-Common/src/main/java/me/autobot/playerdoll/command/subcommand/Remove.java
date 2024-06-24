package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.PermConfig;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Remove extends SubCommand implements DollCommandExecutor {
    private Player sender;
    private DollConfig dollConfig;
    public Remove(String target) {
        super(target);
    }

    @Override
    public void execute() {
        Player onlinePlayer = Bukkit.getPlayerExact(targetString);
        if (onlinePlayer != null) {
            // kill when online
            onlinePlayer.setHealth(0);
        }
        // remove config
        UUID ownerUUID = UUID.fromString(dollConfig.ownerUUID.getValue());
        String dollUUID = dollConfig.dollUUID.getValue();

        FileUtil fileUtil = FileUtil.INSTANCE;
        File configFile = fileUtil.getFile(fileUtil.getDollDir(), dollConfig.dollName.getValue() + ".yml");
        File dataFile = fileUtil.getFile(fileUtil.getPlayerDataDir(), dollUUID + ".dat");
        File dataOldFile = fileUtil.getFile(fileUtil.getPlayerDataDir(), dollUUID + ".dat_old");

        Runnable task = () -> {
            configFile.delete(); dataFile.delete(); dataOldFile.delete();
        };
        ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();
        delayedExecutor.schedule(task, 2, TimeUnit.SECONDS);
        delayedExecutor.shutdown();

        PermConfig permConfig = PermConfig.get();
        if (permConfig.enable.getValue()) {
            Integer count = DollManager.PLAYER_CREATION_COUNTS.get(ownerUUID);
            if (count == null || count == 0) {
                return;
            }
            DollManager.PLAYER_CREATION_COUNTS.put(ownerUUID, count-1);
        }
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        this.sender = playerSender;
        if (targetString == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        dollConfig = DollConfig.getTemporaryConfig(targetString);
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!isOwnerOrOp(playerSender, dollConfig)) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("not-owner"));
            return 0;
        }

        execute();
        return 1;
    }
}
