package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class DollKickEvent implements Listener {
    @EventHandler
    public void onDollKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (DollManager.ONLINE_DOLL_MAP.containsKey(player.getUniqueId())) {
            if (PlayerDoll.isFolia) {
                //PlayerDoll.getFoliaHelper().kickPlayer(player);
                event.setCancelled(true);
            }
        }
    }
}
