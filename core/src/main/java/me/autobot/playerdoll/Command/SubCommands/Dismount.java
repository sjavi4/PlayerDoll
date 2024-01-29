package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Dismount extends SubCommand {
    public Dismount(Player sender, String dollName) {
        super(sender, dollName);
        actionPack.dismount();
    }
}
