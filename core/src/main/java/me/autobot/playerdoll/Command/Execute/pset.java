package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.InvMenu.Menus.PlayerSettingmenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class pset extends SubCommand {
    Player player;
    Player doll;
    OfflinePlayer target;
    public pset() {}
    public pset(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName, "Pset")) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(dollName);
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        if (args == null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMissingTarget",'&'));
            return;
        }
        String[] strArgs = (String[]) args;
        target = Bukkit.getOfflinePlayer(strArgs[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerNotExist",'&'));
            return;
        }
        Player target = Bukkit.getPlayer(strArgs[0]);
        if (target == null || validator.isDollBeingTarget(target)) return;

        execute();
    }
    @Override
    public void execute() {
        PlayerDoll.getInvManager().openInv(new PlayerSettingmenu(player,doll,target),player);
    }

    @Override
    public ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        return new ArrayList<>(){{addAll(set);}};
    }

    @Override
    public List<Object> tabSuggestion() {
        return List.of(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }
}
