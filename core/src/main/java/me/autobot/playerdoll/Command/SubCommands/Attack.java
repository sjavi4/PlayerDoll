package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;


public class Attack extends SubCommand {
    public Attack(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.start(EntityPlayerActionPack.ActionType.ATTACK, EntityPlayerActionPack.Action.once());
            return;
        }
        EntityPlayerActionPack.ActionType actionType = EntityPlayerActionPack.ActionType.ATTACK;
        executeAction(args, 0, actionType, permissionManager.minAttackInterval);
    }
}
