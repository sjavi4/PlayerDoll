package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class strafe implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args, 2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {
            return;
        }

        doll.getActionPack().setStrafing(_args[1] == null? 0.0F: Float.parseFloat(_args[1]));
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"0", "0.0"}));
    }


}