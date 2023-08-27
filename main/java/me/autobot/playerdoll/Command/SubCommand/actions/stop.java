package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class stop implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission

        //slot [1-9]
        String pattern = _args[1] == null ? "all":_args[1];
        if (pattern.equalsIgnoreCase("movement")) {
            doll.getActionPack().stopMovement();
        } else {
            doll.getActionPack().stopAll();
        }
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"all", "movement"}));
    }


}
