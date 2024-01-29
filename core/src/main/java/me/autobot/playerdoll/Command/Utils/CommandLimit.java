package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermissionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandLimit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player p)) return false;

        PermissionManager perm = PermissionManager.getPlayerPermission(p);

        List<String> permissionInfo = new ArrayList<>() {{
            add(LangFormatter.YAMLReplace("permissionInfo.groupName",perm.groupName));
            add(LangFormatter.YAMLReplace("permissionInfo.nextGroup",perm.nextGroup));
        }};


                /*
                LangFormatter.YAMLReplace("permissionInfo.canCreateDoll",perm.canCreateDoll),
                LangFormatter.YAMLReplace("permissionInfo.maxDollCreation",perm.maxDollCreation),
                LangFormatter.YAMLReplace("permissionInfo.maxDollSpawn",perm.maxDollSpawn),
                LangFormatter.YAMLReplace("permissionInfo.canJoinAtStart",perm.canJoinAtStart),
                LangFormatter.YAMLReplace("permissionInfo.restrictSkin",perm.restrictSkin),
                LangFormatter.YAMLReplace("permissionInfo.bypassMaxPlayer",perm.bypassMaxPlayer),
                LangFormatter.YAMLReplace("permissionInfo.keepInventory",perm.keepInventory),
                LangFormatter.YAMLReplace("permissionInfo.notCountSleeping",perm.notCountSleeping),
                LangFormatter.YAMLReplace("permissionInfo.prefix",perm.prefix),
                LangFormatter.YAMLReplace("permissionInfo.suffix",perm.suffix),
                LangFormatter.YAMLReplace("permissionInfo.bypassResidence",perm.bypassResidence),
                LangFormatter.YAMLReplace("permissionInfo.costPerCreation",perm.costPerCreation),
                LangFormatter.YAMLReplace("permissionInfo.costForUpgrade",perm.costForUpgrade),

                 */
/*
                LangFormatter.YAMLReplace("permissionInfo.minUseInterval",perm.minUseInterval),
                LangFormatter.YAMLReplace("permissionInfo.minAttackInterval",perm.minAttackInterval),
                LangFormatter.YAMLReplace("permissionInfo.minSwapInterval",perm.minSwapInterval),
                LangFormatter.YAMLReplace("permissionInfo.minDropInterval",perm.minDropInterval),
                LangFormatter.YAMLReplace("permissionInfo.minJumpInterval",perm.minJumpInterval),
                LangFormatter.YAMLReplace("permissionInfo.minLookatInterval",perm.minLookatInterval)

 */
        perm.groupProperties.forEach((k,v) -> {
            permissionInfo.add(LangFormatter.YAMLReplace("permissionInfo."+k,v));
        });
        perm.dollProperties.forEach((k,v) -> {
            permissionInfo.add(LangFormatter.YAMLReplace("permissionInfo."+k,v));
        });

        p.sendMessage(permissionInfo.toArray(String[]::new));
        
        return true;
    }
}
