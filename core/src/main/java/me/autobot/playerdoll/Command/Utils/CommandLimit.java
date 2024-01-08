package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermissionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.autobot.playerdoll.Util.Pair;

public class CommandLimit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player p)) return false;

        String playerPermission = PermissionManager.playerPermissionGroup.get(p);
        PermissionManager perm = PermissionManager.permissionGroupMap.get(playerPermission);

        String[] permissionInfo = new String[]{
                LangFormatter.YAMLReplace("permissionInfo.groupName",new Pair<>("%a%",perm.groupName)),
                LangFormatter.YAMLReplace("permissionInfo.nextGroup",new Pair<>("%a%",perm.nextGroup)),
                LangFormatter.YAMLReplace("permissionInfo.canCreateDoll",new Pair<>("%a%",String.valueOf(perm.canCreateDoll))),
                LangFormatter.YAMLReplace("permissionInfo.maxDollCreation",new Pair<>("%a%",String.valueOf(perm.maxDollCreation))),
                LangFormatter.YAMLReplace("permissionInfo.maxDollSpawn",new Pair<>("%a%",String.valueOf(perm.maxDollSpawn))),
                LangFormatter.YAMLReplace("permissionInfo.canJoinAtStart",new Pair<>("%a%",String.valueOf(perm.canJoinAtStart))),
                LangFormatter.YAMLReplace("permissionInfo.restrictSkin",new Pair<>("%a%",String.valueOf(perm.restrictSkin))),
                LangFormatter.YAMLReplace("permissionInfo.bypassMaxPlayer",new Pair<>("%a%",String.valueOf(perm.bypassMaxPlayer))),
                LangFormatter.YAMLReplace("permissionInfo.keepInventory",new Pair<>("%a%",String.valueOf(perm.keepInventory))),
                LangFormatter.YAMLReplace("permissionInfo.notCountSleeping",new Pair<>("%a%",String.valueOf(perm.notCountSleeping))),
                LangFormatter.YAMLReplace("permissionInfo.prefix",new Pair<>("%a%",String.valueOf(perm.prefix))),
                LangFormatter.YAMLReplace("permissionInfo.suffix",new Pair<>("%a%",String.valueOf(perm.suffix))),
                LangFormatter.YAMLReplace("permissionInfo.bypassResidence",new Pair<>("%a%",String.valueOf(perm.bypassResidence))),
                LangFormatter.YAMLReplace("permissionInfo.costPerCreation",new Pair<>("%a%",String.valueOf(perm.costPerCreation))),
                LangFormatter.YAMLReplace("permissionInfo.costForUpgrade",new Pair<>("%a%",String.valueOf(perm.costForUpgrade))),

                LangFormatter.YAMLReplace("permissionInfo.minUseInterval",new Pair<>("%a%",String.valueOf(perm.minUseInterval))),
                LangFormatter.YAMLReplace("permissionInfo.minAttackInterval",new Pair<>("%a%",String.valueOf(perm.minAttackInterval))),
                LangFormatter.YAMLReplace("permissionInfo.minSwapInterval",new Pair<>("%a%",String.valueOf(perm.minSwapInterval))),
                LangFormatter.YAMLReplace("permissionInfo.minDropInterval",new Pair<>("%a%",String.valueOf(perm.minDropInterval))),
                LangFormatter.YAMLReplace("permissionInfo.minJumpInterval",new Pair<>("%a%",String.valueOf(perm.minJumpInterval))),
                LangFormatter.YAMLReplace("permissionInfo.minLookatInterval",new Pair<>("%a%",String.valueOf(perm.minLookatInterval)))
        };

        p.sendMessage(permissionInfo);
        
        return true;
    }
}
