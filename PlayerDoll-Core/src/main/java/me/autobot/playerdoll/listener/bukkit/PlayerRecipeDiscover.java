package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.api.doll.DollStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class PlayerRecipeDiscover implements Listener {
    @EventHandler
    public void onDiscoverRecipe(PlayerRecipeDiscoverEvent event) {
        if (DollStorage.ONLINE_DOLLS.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
