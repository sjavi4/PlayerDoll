package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.AbstractDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mount extends SubCommand {
    Player player;
    AbstractDoll doll;
    String[] args;
    public mount(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        if (!isOnline(dollName)) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        execute();
    }

    @Override
    public void execute() {
        doll.getActionPack().mount(true);
    }
}