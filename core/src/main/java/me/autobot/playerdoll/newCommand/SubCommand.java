package me.autobot.playerdoll.newCommand;

import me.autobot.playerdoll.CarpetMod.IEntityPlayerActionPack;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public abstract class SubCommand {

    public enum MinPermission {
        Player(0), Share(1), Owner(2), Admin(3);
        final int level;
        MinPermission(int level) {
            this.level = level;
        }
    }
    private final MinPermission permission;
    private final boolean allowConsole;

    public SubCommand(MinPermission permission , boolean allowConsole) {
        this.permission = permission;
        this.allowConsole = allowConsole;
    }
    public boolean checkPermission(CommandSender sender, String stripedDollName) {
        //get Doll config
        int level = -1;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            var doll = PlayerDoll.dollManagerMap.get(stripedDollName);
            var config = YAMLManager.loadConfig(stripedDollName,false).getConfig();
            if (player.isOp()) {
                level = 3;
            } else if (config.getString("Owner.UUID").equalsIgnoreCase(player.getUniqueId().toString())) {
                level = 2;
            } else if (config.getStringList("Share").contains(player.getUniqueId().toString())) {
                level = 1;
            } else {
                level = 0;
            }
        }
        boolean pass = level >= permission.level || allowConsole;
        if (!pass && player != null) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerNoPermission",'&'));
        }
        return pass;
    }

    public String checkDollName(Object dollName) {
        String name = (String) dollName;
        String identifier = PlayerDoll.dollIdentifier;
        if (!name.startsWith(identifier)) {
            return identifier + name;
        }
        return name;
    }

    public boolean isOnline(String dollName) {
        return PlayerDoll.dollManagerMap.containsKey(dollName);
    }

    public abstract void execute();

    public static class ActionHandler {
        public static Object action(IEntityPlayerActionPack actionPack, String actionType, int... args) {
            return switch (actionType) {
                case "continuous" -> actionPack.Action_continuous();
                case "interval" -> {
                    if (args.length >= 2) yield actionPack.Action_interval(args[0],args[1]);
                    yield actionPack.Action_interval(args[0]);
                }
                default -> actionPack.Action_once();
            };
        }

    }
}
