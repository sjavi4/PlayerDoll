package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.entity.Player;

public class Remove extends SubCommand {

    public Remove(Player sender, String dollName) {
        super(sender, dollName);
        if (!dollConfig.ownerUUID.getValue().equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        /*
        if (doll != null) {
            dollConfigManager.config.set("Remove", true);
            DollManager.getInstance().removeDoll(dollName);
        } else {
            String uuid = dollConfig.getString("UUID");
            File config = new File(PlayerDoll.getDollDirectory(),dollName+".yml");
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            config.delete();
            dat.delete();
            dat_old.delete();
        }

         */
        DollManager.getInstance().removeDoll(sender, dollName);
    }
}