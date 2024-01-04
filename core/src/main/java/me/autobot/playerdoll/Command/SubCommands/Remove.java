package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

public class Remove extends SubCommand {

    public Remove(Player sender, String dollName) {
        super(sender, dollName);
        if (doll != null) {
            dollConfigManager.config.set("Remove", true);
            if (PlayerDoll.isFolia) {
                IDoll.foliaDisconnect(true, Bukkit.getPlayer(dollName), doll);
            } else {
                doll._kill();
            }
        } else {
            String uuid = sender.getUniqueId().toString();
            File config = new File(PlayerDoll.getDollDirectory(),player.getName()+".yml");
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            config.delete();
            dat.delete();
            dat_old.delete();
        }
    }
}