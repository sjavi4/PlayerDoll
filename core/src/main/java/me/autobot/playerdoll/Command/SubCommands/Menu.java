package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Mainmenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Menu extends SubCommand {

    public Menu(Player sender, String dollName) {
        super(sender, dollName);
        PlayerDoll.getInvManager().openInv(new Mainmenu(player,dollPlayer),player);
    }
}
