package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Despawn extends SubCommand {
    public Despawn(Player sender, String dollName) {
        super(sender, dollName);
        Runnable task = () -> {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "kick " + dollPlayer.getName());
        };
        PlayerDoll.getScheduler().globalTask(task);
        /*
        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().globalTask(task);
        } else {
            task.run();
        }

         */
        //DollManager.getInstance().despawnDoll(doll);
    }
}
