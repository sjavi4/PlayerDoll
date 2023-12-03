package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class DollJoinEvent implements Listener {
    @EventHandler
    public void OnDollJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PlayerDoll.dollManagerMap.containsKey(player.getName())) {
            return;
        }
        YamlConfiguration globalConfig = ConfigManager.configs.get("config");
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }
        player.setSleepingIgnored(globalConfig.getBoolean("Global.DollNotCountSleeping"));
        if (!PlayerDoll.isFolia) PlayerDoll.getScoreboard().addMember(player);

        if (!globalConfig.getBoolean("Global.DollJoinMessage")) {
            event.setJoinMessage(null);
        }

        player.setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setSaturation(0.0f);
        player.setCollidable(false);
        player.setHealth(20);
    }
}
