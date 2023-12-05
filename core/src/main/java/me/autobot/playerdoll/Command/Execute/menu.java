package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.InvMenu.Menus.Mainmenu;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class menu extends SubCommand {
    Player player;
    Player doll;
    public menu() {}
    public menu(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName, "Menu")) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(dollName);
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        execute();
    }

    @Override
    public void execute() {
        PlayerDoll.getInvManager().openInv(new Mainmenu(player,doll),player);
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
        return null;
    }
}
