package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Settingmenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Set extends SubCommand {


    public Set(Player sender, String dollName) {
        super(sender, dollName);
        PlayerDoll.getInvManager().openInv(new Settingmenu(player, dollPlayer), player);
    }
}