package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.doll.DollStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        //Player player = event.getEntity();
        if (!DollStorage.ONLINE_DOLLS.containsKey(event.getEntity().getUniqueId())) {
            return;
        }
        if (!PlayerDollAPI.getConfigLoader().getBasicConfig().broadcastDollDeath.getValue()) {
            event.setDeathMessage(null);
        }
    }
}
