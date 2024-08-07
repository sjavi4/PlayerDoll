package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.doll.DollManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;

public class ServerListPing implements Listener {
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        BasicConfig basicConfig = BasicConfig.get();
        if (basicConfig.displayDollWhenPing.getValue()) {
            // true = allow display
            return;
        }

        // Remove all doll in ping list
        Iterator<Player> iterator = event.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (DollManager.ONLINE_DOLLS.containsKey(player.getUniqueId())) {
                iterator.remove();
            }
        }

        if (basicConfig.adjustableMaxPlayer.getValue()) {
            event.setMaxPlayers(PlayerDoll.PLUGIN.getMaxPlayer());
        }


        // Remove all dolls and adjust the player count

    }
}
