package me.autobot.playerdoll.Command.SubCommand.utils;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import org.bukkit.entity.Player;

import java.util.List;

public class reload implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(TranslateFormatter.stringConvert("NoPermission",'&'));
            return;
        }
        YAMLManager.reloadAllConfig();
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }
}
