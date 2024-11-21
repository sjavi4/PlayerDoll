package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

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
            PlayerDollAPI.getLogger().log(Level.WARNING, "Doll[{0}] Got Null UUID! Please check the config", targetString);
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("null-uuid"));
            return;
        }
        if (targetString.length() >= 15) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Doll[{0}] Got Legacy Name Format! Please check the config", targetString);
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("spawn-error", targetString));
            return;
        }
        // Spawn with Prefixed value (can be flexibly changed)
        PlayerDollAPI.getConnection().connect(profile, caller);
        //DollConnection.connect(profile, caller);
        //SocketHelper.createConnection(PlayerDollAPI.getConfigLoader().getBasicConfig().dollIdentifier.getValue() + targetString, targetUUID, caller);
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
        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        File file = fileUtil.getFile(fileUtil.getDollDir(), targetString + ".yml");
        if (!file.exists()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        DollConfig offlineConfig = DollConfig.getTemporaryConfig(targetString);
        targetUUID = UUID.fromString(offlineConfig.dollUUID.getValue());

        Player onlinePlayer;
        //String dollUUID = dollConfig.dollUUID.getValue();
        if (PlayerDollAPI.getCommandBuilder().getDollIndicator().isBlank()) {
            onlinePlayer = Bukkit.getPlayer(targetUUID);
        } else {
            onlinePlayer = Bukkit.getPlayerExact(DollNameUtil.dollFullName(targetString));
        }
        //Player onlinePlayer = Bukkit.getPlayerExact(PlayerDollAPI.getCommandBuilder().getDollIndicator() + targetString);
        if (onlinePlayer != null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("in-world"));
            return 0;
        }

        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();

        profile = new GameProfile(targetUUID, basicConfig.dollIdentifier.getValue() + targetString);
        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", offlineConfig.skinProperty.getValue(), offlineConfig.skinSignature.getValue()));

        if (executeIfManage(context.getInput())) {
            return 1;
        }

        int maxSpawn = basicConfig.serverMaxDollSpawn.getValue();
        if (maxSpawn > 0 && maxSpawn >= DollStorage.ONLINE_DOLLS.size()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("max-capacity"));
            return 0;
        }

        if (!outputHasPerm(playerSender, offlineConfig, PersonalFlagButton.SPAWN)) {
            return 0;
        }

        execute();
        return 1;
    }
}
