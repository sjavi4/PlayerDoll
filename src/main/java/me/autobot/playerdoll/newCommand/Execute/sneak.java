package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class sneak extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;
    public sneak(CommandSender sender, Object doll, Object args) {
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
        doll.getActionPack().setSneaking(args[0] == null ? !doll.isCrouching() : Boolean.parseBoolean(args[0]));
    }
    public static List<Object> tabSuggestion() {
        return List.of(List.of("[true]","[false]"));
    }
}