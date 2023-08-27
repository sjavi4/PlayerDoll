package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class drop implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,4);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission

        int arg1 = _args[2] == null ? 1 : Integer.parseInt(_args[2]);
        boolean arg2 = _args[3] != null && _args[3].equalsIgnoreCase("all");
        doll.getActionPack().drop(arg1,arg2);
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"1","2","3","4","5","6","7","8","9"}), Collections.singletonList("all"));
    }
}
