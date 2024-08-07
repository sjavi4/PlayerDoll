package me.autobot.playerdoll.listener.doll;

import io.netty.channel.Channel;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.event.DollRespawnEvent;
import me.autobot.playerdoll.netty.DollConnection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DollRespawn implements Listener {

    @EventHandler
    public void onDollRespawn(DollRespawnEvent event) {
        // Folia won't trigger Bukkit PlayerRespawnEvent
        UUID playerUUID = event.getPlayer().getUniqueId();
        Channel channel = DollConnection.DOLL_CONNECTIONS.get(playerUUID);
        if (channel == null) {
            return;
        }
        PlayerDoll.LOGGER.info("Doll Respawn");
        PlayerDoll.scheduler.entityTaskDelayed(channel::close, event.getPlayer(), 1);
    }
}
