package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.persistence.PersistentDataType;

public class DollRecipeEvent implements Listener {
    @EventHandler
    public void onDollUnlockRecipe(PlayerRecipeDiscoverEvent event) {
        if (event.getPlayer().getPersistentDataContainer().has(new NamespacedKey(PlayerDoll.getPlugin(),"Doll"), PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
