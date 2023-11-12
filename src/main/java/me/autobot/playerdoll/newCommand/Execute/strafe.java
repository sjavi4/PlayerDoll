package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.AbstractDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class strafe extends SubCommand {
    Player player;
    AbstractDoll doll;
    String[] args;
    public strafe(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        if (!isOnline(dollName)) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        float v = 0.0f;
        if (args[0].equalsIgnoreCase("left")) {
            v = 1.0f;
        } else if (args[0].equalsIgnoreCase("right")) {
            v = -1.0f;
        }
        doll.getActionPack().setStrafing(v);
    }
    public static List<Object> tabSuggestion() {
        return List.of(List.of("left","right"));
    }
}