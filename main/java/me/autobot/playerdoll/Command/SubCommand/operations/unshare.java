package me.autobot.playerdoll.Command.SubCommand.operations;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class unshare implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        if ((!doll.getOwner().getName().equals(player.getName()) && !player.isOp())) {
            player.sendMessage(TranslateFormatter.stringConvert("NoPermission",'&'));
            return;
        }
        Player arg1 = _args[1] == null ? null : Bukkit.getPlayer(_args[1]);
        if (arg1 == null) {
            player.sendMessage(TranslateFormatter.stringConvert("PlayerNotExist",'&'));
            return;
        }
        YamlConfiguration dollConfig = YAMLManager.getConfig(dollName);
        List<String> shareList = dollConfig.getStringList("Share");
        if (!shareList.contains(arg1.getUniqueId().toString())) {
            player.sendMessage(TranslateFormatter.stringConvert("PlayerNotInShare", '&', "%player%", arg1.getName(), "%doll%", PlayerDoll.getDollPrefix() + doll.getDollName()));
            return;
        }
        player.sendMessage(TranslateFormatter.stringConvert("DelShare", '&', "%player%", arg1.getName(), "%doll%", PlayerDoll.getDollPrefix() + doll.getDollName()));
        shareList.remove(arg1.getUniqueId().toString());
        dollConfig.set("Share", shareList);
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()));
    }
}