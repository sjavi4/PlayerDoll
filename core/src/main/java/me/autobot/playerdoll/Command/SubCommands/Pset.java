package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.PlayerSettingmenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Pset extends SubCommand {

    public Pset(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            return;
        }
        if (checkArgumentValid(ArgumentType.ONLINE_DOLL,args[0])) return;
        if (checkArgumentValid(ArgumentType.ONLINE_PLAYER,args[0])) {
            PlayerDoll.getInvManager().openInv(new PlayerSettingmenu(player, dollPlayer, Bukkit.getPlayer(args[0])), player);
        }
    }
}
