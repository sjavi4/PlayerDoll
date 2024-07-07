package me.autobot.playerdoll.command.subcommand;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
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
        dollConfig.dollName.setNewValue(DollManager.dollFullName(targetString));
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
        if (DollManager.validateDollName(targetString)) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("regex-fail"));
            return 0;
        }
        if (BasicConfig.get().preservedDollName.getValue().contains(targetString.toLowerCase())) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("preserved-name"));
            return 0;
        }

        if (profile != null && !outputValidProfile(playerSender, profile)) {
            return 0;
        }
        this.sender = playerSender;

        FileUtil fileUtil = FileUtil.INSTANCE;
        File dollConfigFile = fileUtil.getFile(fileUtil.getDollDir(), DollManager.dollFullName(targetString) + ".yml");
        if (dollConfigFile.exists()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("dupe-name"));
            return 0;
        }

        String[] splitInput = context.getInput().split(" ");

        if (fromManageCommand(context.getInput())) {
            dollConfig = DollConfig.createNewConfig(fileUtil.getOrCreateFile(fileUtil.getDollDir(), dollConfigFile));
            execute();
            return 1;
        }

        if (!DollManager.canPlayerCreateDoll(playerSender)) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("max-create"));
            return 0;
        }

        if (splitInput.length == 4) {
            // Player have skin permission & inputted skin
            if (splitInput[3].startsWith("@")) { // doll create <name> [<skin>]
                playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("multi-select"));
                return 0;
            }
        }
        dollConfig = DollConfig.createNewConfig(fileUtil.getOrCreateFile(fileUtil.getDollDir(), dollConfigFile));
        execute();
        return 1;
    }
}
