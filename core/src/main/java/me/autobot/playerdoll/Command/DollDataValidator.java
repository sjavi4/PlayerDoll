package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.YAMLManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DollDataValidator {
    private final Player player;
    private final String fullName;
    private final String shortName;
    private final YamlConfiguration globalConfig = ConfigManager.getConfig();
    public DollDataValidator(Player sender, String dollName) {
        player = sender;
        fullName = dollName;
        shortName = CommandType.getDollName(dollName,false);
    }
    public boolean illegalName() {
        if (!shortName.matches("^[a-zA-Z0-9_]*$")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("IllegalDollName"));
            return true;
        }
        return false;
    }
    public boolean preservedName() {
        if (globalConfig.getStringList("Global.PreservedName").stream().anyMatch(s -> s.equalsIgnoreCase(shortName))) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PreservedDollName", shortName));
            return true;
        }
        return false;
    }
    public boolean longName() {
        if (fullName.length() > 16) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("LongDollName", 16 - PlayerDoll.dollIdentifier.length()));
            return true;
        }
        return false;
    }
    public boolean repeatName() {
        if (YAMLManager.loadConfig(fullName, false, true) != null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("RepeatDollName", shortName));
            return true;
        }
        return false;
    }
}
