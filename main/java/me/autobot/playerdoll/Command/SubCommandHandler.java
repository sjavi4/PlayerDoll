package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public interface SubCommandHandler {
    void perform(Player player, String dollName, String args[]);

    static DollManager checkDoll(Player player, String dollName) {

        DollManager doll = PlayerDoll.dollManagerMap.get(PlayerDoll.getDollPrefix() + dollName);
        if (doll == null) {
            player.sendMessage(TranslateFormatter.stringConvert("DollNotExist",'&'));
            return null;
        }
        YamlConfiguration dollData = YAMLManager.getConfig(dollName);
        if (dollData == null) {
            player.sendMessage(TranslateFormatter.stringConvert("DollNotExist",'&'));
            return null;
        }
        boolean flag1 = dollData.getStringList("Share").contains(player.getUniqueId().toString());
        boolean flag2 = dollData.getString("Owner").equals(player.getUniqueId().toString());

        if (!flag1 && !flag2 && !player.isOp()) {
            player.sendMessage(TranslateFormatter.stringConvert("NoPermission",'&'));
            return null;
        }
        return doll;
    }
    List<List<String>> commandList();
}
