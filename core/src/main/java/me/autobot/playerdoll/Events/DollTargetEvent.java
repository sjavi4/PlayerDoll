package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class DollTargetEvent implements Listener {
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            String name = player.getName();
            if (PlayerDoll.dollManagerMap.containsKey(name)) {
                var config = DollConfigManager.dollConfigManagerMap.get(player);
                if (!(boolean)config.getDollSetting().get("Hostility")) {
                    event.setTarget(null);
                    event.setCancelled(true);
                }
            }
        }
    }
}
