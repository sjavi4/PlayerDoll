package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class DollKickEvent implements Listener {
    @EventHandler
    public void onDollKick(PlayerKickEvent event) {
        if (!PlayerDoll.isFolia) return;
        if (PlayerDoll.dollManagerMap.containsKey(event.getPlayer().getDisplayName().substring(PlayerDoll.getDollPrefix().length()))) {
            event.setCancelled(true);
        }
    }
}
