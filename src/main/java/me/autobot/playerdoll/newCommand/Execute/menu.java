package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.InvMenu.Menus.Mainmenu;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class menu extends SubCommand {
    Player player;
    Player doll;
    public menu(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(PlayerDoll.getDollPrefix() + dollName);
        if (this.doll == null) return;
        execute();
    }

    @Override
    public void execute() {
        PlayerDoll.getInvManager().openInv(new Mainmenu(player,doll),player);
    }
}
