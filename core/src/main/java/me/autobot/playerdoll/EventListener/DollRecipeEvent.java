package me.autobot.playerdoll.EventListener;

import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class DollRecipeEvent implements Listener {
    @EventHandler
    public void onDollUnlockRecipe(PlayerRecipeDiscoverEvent event) {
        if (DollManager.ONLINE_DOLL_MAP.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
