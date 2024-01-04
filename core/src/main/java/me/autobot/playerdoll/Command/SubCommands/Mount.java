package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Mount extends SubCommand {

    public Mount(Player sender, String dollName) {
        super(sender, dollName);
        actionPack.mount(true);
    }
}