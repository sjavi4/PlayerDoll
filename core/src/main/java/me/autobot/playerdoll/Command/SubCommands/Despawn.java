package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Despawn extends SubCommand {
    public Despawn(Player sender, String dollName) {
        super(sender, dollName);
        if (PlayerDoll.isFolia) {
            IDoll.foliaDisconnect(false, Bukkit.getPlayer(dollName), doll);
        } else {
            doll._disconnect();
        }
    }
}
