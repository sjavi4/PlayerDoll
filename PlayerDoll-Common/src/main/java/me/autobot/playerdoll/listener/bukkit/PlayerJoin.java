package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.doll.DollManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!DollManager.ONLINE_DOLLS.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        if (!BasicConfig.get().broadcastDollJoin.getValue()) {
            event.setJoinMessage(null);
        }
    }
}
