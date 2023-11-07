package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class tp extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;
    public tp(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        if (!isOnline(dollName)) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        if (PlayerDoll.isFolia) FoliaSupport.globalTask(() -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + doll.getGameProfile().getName() + " " + player.getName()));
        else Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "tp " + doll.getGameProfile().getName() + " " + player.getName());
        if (args[0].equalsIgnoreCase("inGrid")) doll.setPos(Math.round(doll.getX() * 2) / 2.0, doll.getBlockY(), Math.round(doll.getZ() * 2) / 2.0);
    }
    public static List<Object> tabSuggestion() {
        return List.of("inGrid");
    }
}