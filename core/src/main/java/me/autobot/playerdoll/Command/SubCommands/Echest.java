package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Inventories.Dollenderchest;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Echest extends SubCommand {
    public Echest(Player sender, String dollName) {
        super(sender, dollName);
        PlayerDoll.getInvManager().openInv(new Dollenderchest(sender, dollPlayer),sender);
    }
}
