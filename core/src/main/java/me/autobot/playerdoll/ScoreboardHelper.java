package me.autobot.playerdoll;

import me.autobot.playerdoll.Configs.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHelper {
    final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    final Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
    Team team;
    public ScoreboardHelper() {
        team = scoreboard.getTeam("Doll");
        if (team == null) team = scoreboard.registerNewTeam("Doll");
        //team.setDisplayName("Doll");
        YamlConfiguration config = ConfigManager.configs.get("config");
        String configPrefix = config.getString("Global.DollScoreboardPrefix");
        String prefix = configPrefix == null? "" : configPrefix;
        String configSuffix = config.getString("Global.DollScoreboardSuffix");
        String suffix = configSuffix == null? "" : configSuffix;
        team.setPrefix(ChatColor.translateAlternateColorCodes('&',prefix));
        team.setSuffix(ChatColor.translateAlternateColorCodes('&',suffix));
    }
    public void addMember(Player player) {
        team.addEntry(player.getName());
    }
    public void removeMember(Player player) {
        team.removeEntry(player.getName());
    }
}
