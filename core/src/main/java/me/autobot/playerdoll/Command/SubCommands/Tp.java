package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Tp extends SubCommand {

    public Tp(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (PlayerDoll.isFolia) {
            FoliaSupport.globalTask(() -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + dollPlayer.getName() + " " + sender.getName()));
        } else {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + dollPlayer.getName() + " " + sender.getName());
        }
        if (args != null && args.length > 0 && checkArgumentValid(ArgumentType.ALIGN_IN_GRID,args[0])) {
            doll._setPos((Math.round(sender.getLocation().getX() * 2) / 2.0), sender.getLocation().getBlockY(), (Math.round(sender.getLocation().getZ() * 2) / 2.0));
        }

    }
}