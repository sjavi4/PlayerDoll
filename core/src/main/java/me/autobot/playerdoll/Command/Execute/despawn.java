package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class despawn extends SubCommand {
    Player player;
    IDoll doll;
    String dollName;
    public despawn() {}
    public despawn(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Despawn")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        execute();
    }

    @Override
    public void execute() {
        if (PlayerDoll.isFolia) IDoll.foliaDisconnect(false, Bukkit.getPlayer(dollName),doll);
        else doll._disconnect();
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
