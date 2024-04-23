package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Tp extends SubCommand {

    public Tp(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        Runnable task = () -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + dollPlayer.getName() + " " + sender.getName());
        PlayerDoll.getScheduler().globalTask(task);
        /*
        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().globalTask(task);
            //FoliaSupport.globalTask(() -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + dollPlayer.getName() + " " + sender.getName()));
        } else {
            task.run();
        }

         */
        if (args != null && args.length > 0 && checkArgumentValid(ArgumentType.ALIGN_IN_GRID,args[0])) {
            Location pos = sender.getLocation();
            Runnable align = () -> {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + dollPlayer.getName() + " " + Math.round(pos.getX() * 2) / 2.0 + " " + pos.getBlockY() + " " + Math.round(pos.getZ() * 2) / 2.0);
            };
            PlayerDoll.getScheduler().globalTask(align);
            //doll._setPos((Math.round(sender.getLocation().getX() * 2) / 2.0), sender.getLocation().getBlockY(), (Math.round(sender.getLocation().getZ() * 2) / 2.0));
        }

    }
}