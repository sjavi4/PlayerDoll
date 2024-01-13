package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

public class Remove extends SubCommand {

    public Remove(Player sender, String dollName) {
        super(sender, dollName);
        if (!dollConfig.getString("Owner.UUID").equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        if (doll != null) {
            dollConfigManager.config.set("Remove", true);
            if (PlayerDoll.isFolia) {
                IDoll.foliaDisconnect(true, Bukkit.getPlayer(dollName), doll);
            } else {
                doll._kill();
            }
        } else {
            String uuid = dollConfig.getString("UUID");
            File config = new File(PlayerDoll.getDollDirectory(),dollName+".yml");
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            config.delete();
            dat.delete();
            dat_old.delete();
        }
    }
}