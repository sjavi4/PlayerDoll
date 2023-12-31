package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class info extends SubCommand {
    Player player;
    String dollName;
    public info() {
    }

    public info(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        this.dollName = checkDollName(doll);
        player = (Player) sender;
        if (!checkPermission(sender, dollName, "Info")) return;

        execute();
    }

    @Override
    public void execute() {

    }

}