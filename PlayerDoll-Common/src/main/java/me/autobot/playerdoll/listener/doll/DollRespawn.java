package me.autobot.playerdoll.listener.doll;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.event.DollRespawnEvent;
import me.autobot.playerdoll.socket.ClientSocket;
import me.autobot.playerdoll.socket.SocketHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DollRespawn implements Listener {

    @EventHandler
    public void onDollRespawn(DollRespawnEvent event) {
        // Folia won't trigger Bukkit PlayerRespawnEvent
        UUID playerUUID = event.getPlayer().getUniqueId();
        ClientSocket socket = SocketHelper.DOLL_CLIENTS.get(playerUUID);
        if (socket == null) {
            return;
        }
        PlayerDoll.LOGGER.info("Doll Respawn");
        PlayerDoll.scheduler.entityTaskDelayed(socket.getSocketReader()::close, event.getPlayer(), 1);
    }
}
