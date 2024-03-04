package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.entity.Player;

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
        if (!dollConfig.ownerUUID.getValue().equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        final boolean flag = DollManager.getInstance().renameDoll(dollName,name);
        if (flag) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameSucceed",dollName.substring(1),name.substring(1)));
        } else {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerRenameFailed"));
        }
        /*
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

         */
    }
}
