package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;

public class ServerListPing implements Listener {
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();
        if (basicConfig.displayDollWhenPing.getValue()) {
            // true = allow display
            return;
        }

        // Remove all doll in ping list
        Iterator<Player> iterator = event.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (DollStorage.ONLINE_DOLLS.containsKey(player.getUniqueId())) {
                iterator.remove();
            }
        }

        if (basicConfig.adjustableMaxPlayer.getValue()) {
            event.setMaxPlayers(PlayerDollAPI.getOriginalMaxPlayer());
        }


        // Remove all dolls and adjust the player count

    }
}
