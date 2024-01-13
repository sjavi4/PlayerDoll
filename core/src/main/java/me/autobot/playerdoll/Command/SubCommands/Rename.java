package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.Pair;
import me.autobot.playerdoll.YAMLManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Rename extends SubCommand {

    public Rename(Player sender, String dollName, String[] args) {
        super(sender, dollName);

        if (args == null || args.length == 0) {
            return;
        }
        String name = "-" + args[0];
        DollDataValidator newNameValidator = new DollDataValidator(sender,name);
        if (newNameValidator.longName()) return;
        if (newNameValidator.illegalName()) return;
        if (newNameValidator.preservedName()) return;
        if (newNameValidator.repeatName()) return;
        if (!dollConfig.getString("Owner.UUID").equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        var oldConfig = new File(PlayerDoll.getDollDirectory(), dollName+".yml");
        var newConfig = new File(PlayerDoll.getDollDirectory(), name+".yml");
        if (oldConfig.exists() && !newConfig.exists()) {
            String oldUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + dollName).getBytes(StandardCharsets.UTF_8)).toString();
            String newUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString();
            File dollFile = new File(PlayerDoll.getDollDirectory(),dollName+".yml");
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+oldUUID+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+oldUUID+".dat_old");

            boolean flag1 = dat.renameTo(new File(dat.getParentFile(),newUUID+".dat"));
            boolean flag2 = dat_old.renameTo(new File(dat.getParentFile(),newUUID+".dat"));
            boolean flag3 = dollFile.renameTo(new File(PlayerDoll.getDollDirectory(), name+".yml"));
            if (flag1 && flag2 && flag3) {
                var config = YAMLManager.loadConfig(newConfig.getName().substring(0, newConfig.getName().lastIndexOf(".")),false, false);
                config.getConfig().set("UUID", newUUID);
                config.saveConfig();
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameSucceed", new Pair<>("%a%",dollName.substring(1)), new Pair<>("%b%",name.substring(1))));
            } else {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameFailed"));
                if (!flag3) new File(dat.getParentFile(),newUUID+".dat").renameTo(dat);
                if (!flag2) new File(dat.getParentFile(),newUUID+".dat_old").renameTo(dat_old);
                if (!flag1) new File(PlayerDoll.getDollDirectory(),name+".yml").renameTo(dollFile);
            }
        } else {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameFailed"));
        }
    }
}
