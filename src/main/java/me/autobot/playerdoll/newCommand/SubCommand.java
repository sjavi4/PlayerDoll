package me.autobot.playerdoll.newCommand;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
            if (player.isOp()) {
                level = 3;
            } else if (((ArrayList<String>)doll.getConfigManager().getData().get("Owner.UUID")).contains(player.getUniqueId().toString())) {
                level = 2;
            } else if (((ArrayList<String>)doll.getConfigManager().getData().get("Share")).contains(player.getUniqueId().toString())) {
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
        String prefix = PlayerDoll.getDollPrefix();
        if (name.startsWith(prefix) && !name.equalsIgnoreCase(prefix) && name.length() > prefix.length()) {
            return name.substring(prefix.length());
        }
        return name;
    }

    public boolean isOnline(String dollName) {
        return PlayerDoll.dollManagerMap.containsKey(dollName);
    }

    public abstract void execute();

}
