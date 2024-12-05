package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class Create extends SubCommand implements DollCommandExecutor {
    private Player sender;
    private final GameProfile profile;
    private DollConfig dollConfig;
    public Create(String targetString) {
        super(targetString);
        profile = null;
    }
    public Create(String targetString, Collection<GameProfile> gameProfiles) {
        super(targetString);
        profile = gameProfiles.stream().findFirst().orElseThrow();
    }


    @Override
    public void execute() {
        dollConfig.dollUUID.setNewValue(UUID.randomUUID().toString());
        dollConfig.dollName.setNewValue(targetString);
        dollConfig.ownerName.setNewValue(sender.getName());
        dollConfig.ownerUUID.setNewValue(sender.getUniqueId().toString());
        if (profile != null) {
            if (!profile.getProperties().get("textures").isEmpty()) {
                Property property = profile.getProperties().get("textures").iterator().next();
                dollConfig.skinProperty.setNewValue(property.value());
                dollConfig.skinSignature.setNewValue(property.signature());
            }
        }
        dollConfig.saveConfig();
        sender.sendMessage(LangFormatter.YAMLReplaceMessage("success-create"));
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        if (targetString == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        if (targetString.length() > 15) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("long-name"));
            return 0;
        }
        if (DollNameUtil.validateDollName(targetString)) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("regex-fail"));
            return 0;
        }
        if (PlayerDollAPI.getConfigLoader().getBasicConfig().preservedDollName.getValue().contains(targetString.toLowerCase())) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("preserved-name"));
            return 0;
        }

        if (profile != null && !outputValidProfile(playerSender, profile)) {
            return 0;
        }
        this.sender = playerSender;

        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        File dollConfigFile = fileUtil.getFile(fileUtil.getDollDir(), DollNameUtil.dollShortName(targetString) + ".yml");
        if (dollConfigFile.exists()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("dupe-name"));
            return 0;
        }

        String[] splitInput = context.getInput().split(" ");

        if (fromManageCommand(context.getInput())) {
            dollConfig = DollConfig.getTemporaryConfig(fileUtil.getOrCreateFile(fileUtil.getDollDir(), dollConfigFile));
            execute();
            return 1;
        }

        if (!canPlayerCreateDoll(playerSender)) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("max-create"));
            return 0;
        }

        if (splitInput.length == 4) {
            // Player have skin permission & have input skin
            if (splitInput[3].startsWith("@")) { // doll create <name> [<skin>]
                playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("multi-select"));
                return 0;
            }
        }
        dollConfig = DollConfig.getTemporaryConfig(fileUtil.getOrCreateFile(fileUtil.getDollDir(), dollConfigFile));
        execute();
        return 1;
    }

    private boolean canPlayerCreateDoll(Player player) {
        PermConfig permConfig = PlayerDollAPI.getConfigLoader().getPermConfig();
        if (!permConfig.enable.getValue()) {
            return true;
        }

        Map<UUID, Integer> countMap = DollStorage.PLAYER_CREATION_COUNTS;
        Integer currentCount = countMap.get(player.getUniqueId());
        if (currentCount == null) {
            currentCount = 0;
        }

        int futureCount = currentCount + 1;

        boolean exceed = false;
        Map<String, Integer> maxCreationMap = permConfig.groupPerCreateLimits;
        for (String group : maxCreationMap.keySet()) {
            if (player.hasPermission(PermConfig.PERM_CREATE_STRING + group)) {
                exceed = futureCount > maxCreationMap.get(group);
                // iterate all
            }
        }
        if (exceed) {
            return false;
        }
        countMap.put(player.getUniqueId(), futureCount);
        return true;
    }
}
