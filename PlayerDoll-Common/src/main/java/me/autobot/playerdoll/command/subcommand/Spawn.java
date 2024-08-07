package me.autobot.playerdoll.command.subcommand;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class Spawn extends SubCommand implements DollCommandExecutor {
    private Player caller;
    private UUID targetUUID;
    private GameProfile profile;
    public Spawn(String target) {
        super(target);
    }

    @Override
    public void execute() {
        if (targetUUID.toString().equals(DollConfig.NULL_UUID)) {
            PlayerDoll.LOGGER.warning("Doll[" + targetString + "] Got Null UUID! Please check the config");
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("null-uuid"));
            return;
        }
        if (targetString.length() >= 15) {
            PlayerDoll.LOGGER.warning("Doll[" + targetString + "] Got Legacy Name Format! Please check the config");
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("spawn-error", targetString));
            return;
        }
        // Spawn with Prefixed value (can be flexibly changed)
        DollConnection.connect(profile, caller);
        //SocketHelper.createConnection(BasicConfig.get().dollIdentifier.getValue() + targetString, targetUUID, caller);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        caller = playerSender;
        if (targetString == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        Player onlinePlayer = Bukkit.getPlayerExact(targetString);
        if (onlinePlayer != null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("in-world"));
            return 0;
        }
        FileUtil fileUtil = FileUtil.INSTANCE;
        File file = fileUtil.getFile(fileUtil.getDollDir(), targetString + ".yml");
        if (!file.exists()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute

        DollConfig offlineConfig = DollConfig.getTemporaryConfig(targetString);
        targetUUID = UUID.fromString(offlineConfig.dollUUID.getValue());

        profile = new GameProfile(targetUUID, BasicConfig.get().dollIdentifier.getValue() + targetString);
        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", offlineConfig.skinProperty.getValue(), offlineConfig.skinSignature.getValue()));

        if (executeIfManage(context.getInput())) {
            return 1;
        }

        int maxSpawn = BasicConfig.get().serverMaxDollSpawn.getValue();
        if (maxSpawn > 0 && maxSpawn >= DollManager.ONLINE_DOLLS.size()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("max-capacity"));
            return 0;
        }

        if (!outputHasPerm(playerSender, offlineConfig, FlagConfig.PersonalFlagType.SPAWN)) {
            return 0;
        }

        execute();
        return 1;
    }
}
