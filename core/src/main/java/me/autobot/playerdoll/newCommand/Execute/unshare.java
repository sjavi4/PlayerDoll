package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.Helper.DollDataValidator;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.util.*;

public class unshare extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    String dollName;
    public unshare() {}
    public unshare(CommandSender sender, Object doll, Object args) {
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
        Player target = Bukkit.getPlayer(args[0]);
        Player bDoll = Bukkit.getPlayer(dollName);
        if (target == null) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerNotExist",'&'));
            return;
        }
        List<String> shareList = (List<String>) doll.getConfigManager().getData().get("Share");
        if (!shareList.contains(target.getUniqueId().toString())) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerNotInShareList", '&', new Pair<>("%a%", target.getName()), new Pair<>("%b%", bDoll.getName())));
            return;
        }
        player.sendMessage(LangFormatter.YAMLReplace("DelShare", '&', new Pair<>("%a%", target.getName()), new Pair<>("%b%", bDoll.getName())));
        shareList.remove(target.getUniqueId().toString());
        doll.getConfigManager().getData().put("Share",shareList);
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
        return List.of(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }
}