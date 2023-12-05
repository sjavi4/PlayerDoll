package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class info extends SubCommand {
    Player player;
    String dollName;
    public info() {
    }

    public info(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        this.dollName = checkDollName(doll);
        player = (Player) sender;
        if (!checkPermission(sender, dollName, "Info")) return;

        execute();
    }

    @Override
    public void execute() {

    }

    @Override
    public final ArrayList<String> targetSelection(UUID uuid) {
        return getAllDoll();
    }
    @Override
    public List<Object> tabSuggestion() {
        return new ArrayList<>();
    }
}