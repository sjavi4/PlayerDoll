package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.util.List;

public class unshare extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;
    public unshare(CommandSender sender, Object doll, Object args) {
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
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerNotExist",'&'));
            return;
        }
        List<String> shareList = (List<String>) doll.getConfigManager().getData().get("Share");
        if (!shareList.contains(target.getUniqueId().toString())) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerNotInShareList", '&', new Pair<>("%a%", target.getName()), new Pair<>("%b%", doll.getDollName())));
            return;
        }
        player.sendMessage(LangFormatter.YAMLReplace("DelShare", '&', new Pair<>("%a%", target.getName()), new Pair<>("%b%", doll.getDollName())));
        shareList.remove(target.getUniqueId().toString());
        doll.getConfigManager().getData().put("Share",shareList);
    }
    public static List<Object> tabSuggestion() {
        return List.of(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }
}