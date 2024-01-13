package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Echest extends SubCommand {
    public Echest(Player sender, String dollName) {
        super(sender, dollName);
        sender.openInventory(PlayerDoll.dollInvStorage.get(dollName).getEnderchestInventory());
    }
}
