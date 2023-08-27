package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.List;

public class mount implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}

        doll.getActionPack().mount(true);
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }

}