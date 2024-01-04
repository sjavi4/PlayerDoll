package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Inventories.Dollinventory;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Inv extends SubCommand {


    public Inv(Player sender, String dollName) {
        super(sender, dollName);
        PlayerDoll.getInvManager().openInv(new Dollinventory(player, dollPlayer), player);
    }
}