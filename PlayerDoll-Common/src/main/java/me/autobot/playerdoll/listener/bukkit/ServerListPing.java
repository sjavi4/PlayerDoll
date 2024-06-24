package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPing implements Listener {
    @EventHandler
    public void onListPing(ServerListPingEvent event) {
        PlayerDoll.LOGGER.info("Call Ping");
    }
}
