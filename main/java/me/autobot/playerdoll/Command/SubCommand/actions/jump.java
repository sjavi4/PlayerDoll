package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class jump implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,4);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission
        //use [once/interval/periodic/hold] [null/tick/tick/tick]
        int arg1 = _args[2] == null ? 20 : Integer.parseInt(_args[2]);
        int arg2 = _args[3] == null ? 0 : Integer.parseInt(_args[3]);

        String pattern = _args[1] == null ? "once":_args[1];
        EntityPlayerActionPack.Action action = EntityPlayerActionPack.Action.once();
        switch (pattern.toLowerCase()) {
            case "interval" -> action = EntityPlayerActionPack.Action.interval(arg1,arg2);
            case "continuous" -> action = EntityPlayerActionPack.Action.continuous();
        }
        doll.getActionPack().start(EntityPlayerActionPack.ActionType.JUMP,action);
    }
    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"once","interval","continuous"}));
    }
}