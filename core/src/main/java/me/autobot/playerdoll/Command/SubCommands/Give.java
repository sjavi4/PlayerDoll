package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Util.Configs.PermConfig;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermChecker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Give extends SubCommand {
    public Give(Player sender, String dollName, String[] args) {
        super(sender, dollName);

        if (args == null || args.length == 0) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMustSpecifyTargetPlayer"));
            return;
        }
        if (checkArgumentValid(ArgumentType.ALL_DOLL,args[0])) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandTargetCannotBeDoll"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandTargetPlayerNotExist"));
            return;
        }
        if (!dollConfig.ownerUUID.getValue().equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandExecutorMustBeOwner"));
            return;
        }
        /*
        PermissionManager perm = PermissionManager.getPlayerPermission(target.getUniqueId());
        int count = PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0);
        int max = (int) perm.groupProperties.get("maxDollCreation");
        if (max >= PlayerDoll.playerDollCountMap.getOrDefault(target.getUniqueId(),0)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll",max));
            return;
        }


        PlayerDoll.playerDollCountMap.put(target.getUniqueId(),count+1);
        */

        int oldOwnerCount = DollManager.PLAYER_DOLL_COUNT_MAP.get(sender.getUniqueId());
        int newOwnerCount = DollManager.PLAYER_DOLL_COUNT_MAP.get(target.getUniqueId());

        PermChecker permChecker = (perm) ->{
            boolean pass = true;
            if (perm.enable.getValue()) {
                if (sender.isOp() && perm.opBypass.getValue()) {
                    return true;
                }
                var maxCreate = perm.maxDollCreate.getValue();
                Optional<String> match = maxCreate.keySet().stream().filter(sender::hasPermission).findFirst();
                if (match.isPresent()) {
                    int max = maxCreate.get(match.get());
                    if (newOwnerCount >= max) {
                        sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll", max));
                        pass = false;
                    }
                }
            }
            return pass;
        };

        if (!permChecker.check(PermConfig.get())) {
            return;
        }

        DollManager.PLAYER_DOLL_COUNT_MAP.put(sender.getUniqueId(),oldOwnerCount-1);
        DollManager.PLAYER_DOLL_COUNT_MAP.put(target.getUniqueId(),newOwnerCount+1);


        dollConfig.ownerName.setNewValue(target.getName());
        dollConfig.ownerUUID.setNewValue(target.getUniqueId().toString());
        dollConfig.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver",target.getName()));
        target.sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter",player.getName()));
    }
}
