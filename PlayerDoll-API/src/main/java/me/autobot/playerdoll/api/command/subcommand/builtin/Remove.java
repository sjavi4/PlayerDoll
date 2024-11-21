package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.config.impl.PermConfig;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Remove extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    private DollConfig dollConfig;
    public Remove(String target) {
        super(target);
    }

    @Override
    public void execute() {
        Player onlinePlayer;
        String dollUUID = dollConfig.dollUUID.getValue();
        if (PlayerDollAPI.getCommandBuilder().getDollIndicator().isBlank()) {
            onlinePlayer = Bukkit.getPlayer(UUID.fromString(dollUUID));
        } else {
            onlinePlayer = Bukkit.getPlayerExact(DollNameUtil.dollFullName(targetString));
        }
        if (onlinePlayer != null) {
            // kill when online
            onlinePlayer.setHealth(0);
        }
        // remove config
        UUID ownerUUID = UUID.fromString(dollConfig.ownerUUID.getValue());

        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        File configFile = fileUtil.getFile(fileUtil.getDollDir(), dollConfig.dollName.getValue() + ".yml");
        File dataFile = fileUtil.getFile(fileUtil.getPlayerDataDir(), dollUUID + ".dat");
        File dataOldFile = fileUtil.getFile(fileUtil.getPlayerDataDir(), dollUUID + ".dat_old");

        Runnable task = () -> {
            configFile.delete(); dataFile.delete(); dataOldFile.delete();
        };
        ScheduledExecutorService delayedExecutor = Executors.newSingleThreadScheduledExecutor();
        delayedExecutor.schedule(task, 2, TimeUnit.SECONDS);
        delayedExecutor.shutdown();

        PermConfig permConfig = PlayerDollAPI.getConfigLoader().getPermConfig();
        if (permConfig.enable.getValue()) {
            Integer count = DollStorage.PLAYER_CREATION_COUNTS.get(ownerUUID);
            if (count == null || count == 0) {
                return;
            }
            DollStorage.PLAYER_CREATION_COUNTS.put(ownerUUID, count-1);
        }
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        this.sender = sender;
        if (targetString == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        File file = fileUtil.getFile(fileUtil.getDollDir(), targetString + ".yml");
        if (!file.exists()) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        dollConfig = DollConfig.getTemporaryConfig(targetString);
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!isOwnerOrOp(sender, dollConfig)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("not-owner"));
            return 0;
        }

        execute();
        return 1;
    }
}
