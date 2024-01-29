package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Menu extends SubCommand {

    public Menu(Player sender, String dollName) {
        super(sender, dollName);
        sender.openInventory(PlayerDoll.dollInvStorage.get(dollName).getInfoPage());
    }
}
