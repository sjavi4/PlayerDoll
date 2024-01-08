package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.PlayerSettingmenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Gset extends SubCommand {
    public Gset(Player sender, String dollName) {
        super(sender, dollName);
        PlayerDoll.getInvManager().openInv(new PlayerSettingmenu(player,dollPlayer,null),player);
    }
}
