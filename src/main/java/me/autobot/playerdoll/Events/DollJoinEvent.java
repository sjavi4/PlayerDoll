package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DollJoinEvent implements Listener {
    @EventHandler
    public void OnDollJoin(PlayerJoinEvent event) {
        if (!PlayerDoll.dollManagerMap.containsKey(event.getPlayer().getName())) {
            return;
        }
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }
    }
}
