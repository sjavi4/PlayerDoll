package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.Helper.ArgumentHelper;
import me.autobot.playerdoll.newCommand.Helper.DollDataValidator;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class lookat extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    public lookat() {}
    public lookat(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
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
        if (Bukkit.getPlayer(args[0]) != null) {
            doll.getActionPack().lookAt(args[0]);
            //doll.getActionPack().lookAt(doll.server.getPlayerList().getPlayerByName(args[0]).getEyePosition());
        } else if (args.length >= 3) {
            ArgumentHelper<Float> argumentHelper = new ArgumentHelper<>();
            ArrayList<Float> floatArgs = argumentHelper.castTo(args);
            doll.getActionPack().lookAt(floatArgs.get(0), floatArgs.get(1), floatArgs.get(2));
        }
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
        return List.of(new ArrayList<>(){{
            add("<coord:X> <coord:Y> <coord:Z>");
            addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }});
    }
}