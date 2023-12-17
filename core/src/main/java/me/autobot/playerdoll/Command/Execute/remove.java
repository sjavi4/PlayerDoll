package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class remove extends SubCommand {
    Player player;
    IDoll doll;
    String dollName;
    public remove() {}
    public remove(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Owner, false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Remove")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        execute();
    }

    @Override
    public void execute() {
        doll.getConfigManager().config.set("Remove",true);
        if (PlayerDoll.isFolia) IDoll.foliaDisconnect(true, Bukkit.getPlayer(dollName),doll);
        else doll._kill();
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