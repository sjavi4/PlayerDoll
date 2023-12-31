package me.autobot.playerdoll.Command.Helper;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.io.File;
import java.util.Map;

public class DollDataValidator {
    private final Player player;
    private final String fullName;
    private final String shortName;
    private final Map<String, IDoll> dollMap = PlayerDoll.dollManagerMap;
    private final YamlConfiguration globalConfig = ConfigManager.getConfig();
    public DollDataValidator(Player sender, String dollName, String stripedDollName) {
        player = sender;
        fullName = dollName;
        shortName = stripedDollName;
    }
    public boolean isDollNameIllegal() {
        if (!shortName.matches("^[a-zA-Z0-9_]*$")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("IllegalDollName",'&'));
            return true;
        }
        return false;
    }
    public boolean isDollNamePreserved() {
        if (globalConfig.getStringList("Global.PreservedName").stream().anyMatch(s -> s.equalsIgnoreCase(shortName))) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PreservedDollName",'&', new Pair<>("%a%", shortName)));
            return true;
        }
        return false;
    }
    public boolean isDollNameTooLong() {
        if (fullName.length() > 16) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("LongDollName",'&', new Pair<>("%a%" , Integer.toString(16 - PlayerDoll.dollIdentifier.length()))));
            return true;
        }
        return false;
    }
    public boolean isDollConfigNotExist() {
        if (!new File(PlayerDoll.getDollDirectory(),fullName+".yml").exists()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("DollNotExist",'&'));
            return true;
        }
        return false;
    }
    public boolean isDollConfigNotExist(File dollFile) {
        if (!dollFile.exists()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("DollNotExist",'&'));
            return true;
        }
        return false;
    }
    public boolean isDollNameRepeat(YamlConfiguration dollConfig) {
        if (!dollConfig.getBoolean("Remove")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("RepeatDollName", '&', new Pair<>("%a%", shortName)));
            return true;
        }
        return false;
    }
    public boolean isExecutionWhenDollRemoved(YamlConfiguration dollConfig) {
        if (dollConfig.getBoolean("Remove")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerExecuteRemovedDoll", '&', new Pair<>("%a%", shortName)));
            return true;
        }
        return false;
    }
    public boolean isDollAlreadyOnline() {
        if (dollMap.containsKey(fullName)) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("InUseDollName",'&', new Pair<>("%a%",shortName)));
            return true;
        }
        return false;
    }
    public boolean isDollOffline() {
        if (!dollMap.containsKey(fullName)) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("DollNotExist",'&'));
            return true;
        }
        return false;
    }
    public boolean isDollBeingTarget(Player target) {
        if (target.hasMetadata("NPC")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("TargetPlayerInvalid",'&'));
            return true;
        }
        return false;
    }
    public boolean isOfflineOperationWhenDollOnline() {
        if (dollMap.containsKey(fullName)) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerExecuteOnlineDoll",'&'));
            return true;
        }
        return false;
    }
}
