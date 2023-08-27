package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class move implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission
        String arg1 = _args[1] == null ? "1.0" : _args[1];
        if (arg1.matches("[-+]?[0-9]*\\.?[0-9]+")) {
            doll.getActionPack().setForward(Float.parseFloat(arg1));
        }
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"1.0"}));
    }

}