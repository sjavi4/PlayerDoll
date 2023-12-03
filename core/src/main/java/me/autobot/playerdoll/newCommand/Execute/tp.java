package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.Helper.DollDataValidator;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class tp extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    String dollName;
    public tp() {}
    public tp(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
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
    @Override
    public final ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>();
        set.addAll(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        set.retainAll(getOnlineDoll());
        return new ArrayList<>(){{addAll(set);}};
    }
    @Override
    public List<Object> tabSuggestion() {
        return List.of("inGrid");
    }
}