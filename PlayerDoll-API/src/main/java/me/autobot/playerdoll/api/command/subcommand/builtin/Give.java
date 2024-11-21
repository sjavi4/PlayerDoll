package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class Give extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    private final GameProfile profile;
    private DollConfig dollConfig;
    public Give(String target, Collection<GameProfile> profiles) {
        super(target);
        profile = profiles.stream().findFirst().orElseThrow();
    }

    @Override
    public void execute() {
        if (PlayerDollAPI.getConfigLoader().getPermConfig().enable.getValue()) {
            Map<UUID, Integer> creationCounts = DollStorage.PLAYER_CREATION_COUNTS;
            UUID oldOwnerUUID = UUID.fromString(dollConfig.ownerUUID.getValue());

            // Prevent OP / bypass permission give affect the count
            if (!oldOwnerUUID.equals(profile.getId())) {
                int oldOwnerCount = creationCounts.get(oldOwnerUUID);
                Integer newOwnerCount = creationCounts.get(profile.getId());
                if (newOwnerCount == null) {
                    newOwnerCount = 0;
                }

                creationCounts.put(oldOwnerUUID,oldOwnerCount-1);
                creationCounts.put(profile.getId(),newOwnerCount+1);
            }
        }

        dollConfig.ownerName.setNewValue(profile.getName());
        dollConfig.ownerUUID.setNewValue(profile.getId().toString());
        dollConfig.saveConfig();
        sender.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver",targetString));
        //target.sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter",player.getName()));
    }
    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
//        if (!(sender instanceof Player playerSender)) {
//            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
//            return 0;
//        }
        this.sender = sender;
        if (targetString == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        String[] splitInput = context.getInput().split(" ");
        if (splitInput.length == 4) {
            // Player have skin permission & inputted skin
            if (splitInput[3].startsWith("@")) { // doll pset <name> <player (profile)>
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("multi-select"));
                return 0;
            }
        }
        if (!outputValidProfile(sender, profile)) {
            return 0;
        }

        Player newOwner = Bukkit.getPlayer(profile.getId());
        if (newOwner == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("player-offline"));
            return 0;
        }

        if (targetString.equalsIgnoreCase(profile.getName())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("self-give"));
            return 0;
        }

        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        if (fileUtil.getFile(fileUtil.getDollDir(), DollNameUtil.dollShortName(profile.getName()) + ".yml").exists()) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("doll-give"));
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

        if (!newOwner.hasPermission("playerdoll.command.create")) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("cannot-create"));
            return 0;
        }

        execute();
        return 1;
    }
}
