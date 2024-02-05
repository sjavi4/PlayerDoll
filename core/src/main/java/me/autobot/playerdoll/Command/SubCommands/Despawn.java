package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;

public class Despawn extends SubCommand {
    public Despawn(Player sender, String dollName) {
        super(sender, dollName);
        DollManager.getInstance().despawnDoll(doll);
    }
}
