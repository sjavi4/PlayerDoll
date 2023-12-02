package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class DollKickEvent implements Listener {
    @EventHandler
    public void onDollKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (PlayerDoll.dollManagerMap.containsKey(player.getName())) {
            DollConfigManager dollConfigManager = DollConfigManager.dollConfigManagerMap.get(player);
            dollConfigManager.save();
            if (PlayerDoll.isFolia) {
                event.setCancelled(true);
                return;
            }
            dollConfigManager.removeListener();

        }
    }
}
