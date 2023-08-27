package me.autobot.playerdoll.Command.SubCommand.operations;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class remove implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}

        if ((!doll.getOwner().getName().equals(player.getName()) && !player.isOp())) {
            player.sendMessage(TranslateFormatter.stringConvert("NoPermission",'&'));
            return;
        }

        YAMLManager.getConfig(dollName).set("Remove",true);
        doll.kill();
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }


}
