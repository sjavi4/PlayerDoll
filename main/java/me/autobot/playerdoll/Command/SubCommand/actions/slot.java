package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class slot implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission

        //slot [1-9]
        doll.getActionPack().setSlot(_args[1] == null ? 1 : Integer.parseInt(_args[1]));
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"1","2","3","4","5","6","7","8","9"}));
    }


}
