package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tp extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    String dollName;
    public tp() {}
    public tp(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Tp")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        Player bDoll = Bukkit.getPlayer(dollName);
        if (PlayerDoll.isFolia) FoliaSupport.globalTask(() -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + bDoll.getName() + " " + player.getName()));
        else Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + bDoll.getName() + " " + player.getName());
        if (args[0].equalsIgnoreCase("inGrid")) doll._setPos((Math.round(player.getLocation().getX() * 2) / 2.0), player.getLocation().getBlockY(), (Math.round(player.getLocation().getZ() * 2) / 2.0));
    }
}