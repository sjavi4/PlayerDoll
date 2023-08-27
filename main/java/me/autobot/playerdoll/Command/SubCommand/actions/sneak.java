package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class sneak implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}

        if (_args[1] == null) {
            doll.getActionPack().setSneaking(!doll.isCrouching());
            return;
        }
        doll.getActionPack().setSneaking(_args[1].equalsIgnoreCase("true"));
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"true", "false"}));
    }

}