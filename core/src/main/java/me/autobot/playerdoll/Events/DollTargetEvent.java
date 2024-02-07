package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.UUID;

public class DollTargetEvent implements Listener {
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (DollManager.ONLINE_DOLL_MAP.containsKey(uuid)) {
                var config = DollConfigManager.dollConfigManagerMap.get(uuid);
                if (!(boolean)config.getDollSetting().get("hostility")) {
                    event.setTarget(null);
                    event.setCancelled(true);
                }
            }
        }
    }
}
