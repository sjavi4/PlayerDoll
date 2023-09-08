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
        String[] _args = Arrays.copyOf(args,3);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission
        int arg1 = -1;
        if (_args[1] != null) {
            if (_args[1].matches("\\d+")) {
                int num = Integer.parseInt(_args[2]);
                if (num < 1 && num > 36) {
                    arg1 = -1;
                } else {
                    arg1 = num - 1;
                }
            } else {
                switch (_args[1].toLowerCase()) {
                    case "helmet" -> arg1 = 39;
                    case "chestplate" -> arg1 = 38;
                    case "leggings" -> arg1 = 37;
                    case "boots" -> arg1 = 36;
                    case "offhand" -> arg1 = 40;
                    case "everything" -> arg1 = -2;
                }
            }
        }
        boolean arg2 = _args[2] != null && _args[2].equalsIgnoreCase("stack");
        doll.getActionPack().drop(arg1,arg2);
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(List.of(new String[]{"1-36","helmet","chestplate","leggings","boots","offhand","everything"}), Collections.singletonList("stack"));
    }
}
