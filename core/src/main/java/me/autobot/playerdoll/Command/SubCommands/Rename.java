package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
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
        File oldConfig = new File(PlayerDoll.getDollDirectory(), dollName+".yml");
        File newConfig = new File(PlayerDoll.getDollDirectory(), name+".yml");
        if (oldConfig.exists() && !newConfig.exists()) {
            boolean flag = oldConfig.renameTo(new File(PlayerDoll.getDollDirectory(), name+".yml"));
            if (flag) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameSucceed",dollName.substring(1),name.substring(1)));
            } else {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameFailed"));
                new File(PlayerDoll.getDollDirectory(),name+".yml").renameTo(oldConfig);
            }
        } else {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameFailed"));
        }
    }
}
