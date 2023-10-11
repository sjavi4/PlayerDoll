package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;


public class DollDisconnectEvent implements Listener {
    @EventHandler
    public void onDollDisconnect(PlayerQuitEvent event) {
        if (!PlayerDoll.dollManagerMap.containsKey(event.getPlayer().getName())) {
            return;
        }
        event.getPlayer().setFallDistance(0.0f);
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers() - 1);
        }
        event.setQuitMessage(null);
        PlayerDoll.dollManagerMap.remove(event.getPlayer().getName());

        String stripedName = event.getPlayer().getName().substring(PlayerDoll.getDollPrefix().length());
        boolean success = YAMLManager.saveConfig(stripedName,true);
        if (success) {
            System.out.println("Successfully Save Config for Doll " + event.getPlayer().getName());
        } else {
            System.out.println("Could Not Save Config for Doll " + event.getPlayer().getName());
        }
    }
}
