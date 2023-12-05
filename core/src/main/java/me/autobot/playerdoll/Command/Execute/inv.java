package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Inventories.Dollinventory;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class inv extends SubCommand {
    Player player;
    Player doll;

    public inv() {
    }
    public inv(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName, "Inventory")) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(dollName);
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        execute();
    }

    @Override
    public void execute() {
        PlayerDoll.getInvManager().openInv(new Dollinventory(player, doll), player);
    }

    @Override
    public ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        return new ArrayList<>() {{
            addAll(set);
        }};
    }

    @Override
    public List<Object> tabSuggestion() {
        return new ArrayList<>();
    }
}