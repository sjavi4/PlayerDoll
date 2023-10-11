package me.autobot.playerdoll.Command.SubCommand.utils;

import me.autobot.playerdoll.Command.SubCommandHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class flags implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        if (args.length > 1) {return;}
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }
}
