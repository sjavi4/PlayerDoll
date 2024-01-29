package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Jump extends SubCommand {

    public Jump(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.start(EntityPlayerActionPack.ActionType.JUMP, EntityPlayerActionPack.Action.once());
            return;
        }
        EntityPlayerActionPack.ActionType actionType = EntityPlayerActionPack.ActionType.JUMP;
        executeAction(args, 0, actionType);
    }
}