package me.autobot.playerdoll.Command.SubCommand.operations;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.List;

public class despawn implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        doll.disconnect();
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }

}
