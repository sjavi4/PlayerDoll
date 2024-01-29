package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Swap extends SubCommand {

    public Swap(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            actionPack.start(EntityPlayerActionPack.ActionType.SWAP_HANDS, EntityPlayerActionPack.Action.once());
            return;
        }
        EntityPlayerActionPack.ActionType actionType = EntityPlayerActionPack.ActionType.SWAP_HANDS;
        executeAction(args, 0, actionType);
    }
}