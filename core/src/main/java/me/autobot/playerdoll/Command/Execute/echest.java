package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Inventories.Dollenderchest;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class echest extends SubCommand {
    Player player;
    Player doll;

    public echest() {
    }
    public echest(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName, "Ender Chest")) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(dollName);
        if (this.doll == null) return;
        execute();
    }

    @Override
    public void execute() {
        PlayerDoll.getInvManager().openInv(new Dollenderchest(player, doll), player);
    }

}
