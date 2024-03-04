package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.GUIs.Doll.DollInvStorage;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

public class Gset extends SubCommand {
    public Gset(Player sender, String dollName) {
        super(sender, dollName);
        if (PlayerDoll.dollInvStorage.containsKey(dollName)) {
            sender.openInventory(PlayerDoll.dollInvStorage.get(dollName).getGSetPage());
        } else {
            PlayerDoll.dollInvStorage.put(dollName,DollInvStorage.offlineInstance(dollName));
            sender.openInventory(PlayerDoll.dollInvStorage.get(dollName).getGSetPage());
        }
    }
}