package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.util.Map;

public class DollDataValidator {
    private final Player player;
    private final String fullName;
    private final String shortName;
    private final Map<String, IDoll> dollMap = PlayerDoll.dollManagerMap;
    private final YamlConfiguration globalConfig = ConfigManager.getConfig();
    public DollDataValidator(Player sender, String dollName) {
        player = sender;
        fullName = dollName;
        shortName = CommandType.getDollName(dollName,false);
    }
    public boolean illegalName() {
        if (!shortName.matches("^[a-zA-Z0-9_]*$")) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("IllegalDollName",'&'));
            return true;
        }
        return false;
    }
    public boolean preservedName() {
        if (globalConfig.getStringList("Global.PreservedName").stream().anyMatch(s -> s.equalsIgnoreCase(shortName))) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PreservedDollName",'&', new Pair<>("%a%", shortName)));
            return true;
        }
        return false;
    }
    public boolean longName() {
        if (fullName.length() > 16) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("LongDollName",'&', new Pair<>("%a%" , Integer.toString(16 - PlayerDoll.dollIdentifier.length()))));
            return true;
        }
        return false;
    }
    public boolean repeatName() {
        if (YAMLManager.loadConfig(fullName, false) != null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("RepeatDollName", '&', new Pair<>("%a%", shortName)));
            return true;
        }
        return false;
    }
}
