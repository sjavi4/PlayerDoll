package me.autobot.playerdoll.listener;

import me.autobot.playerdoll.doll.DollData;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisconnectListener implements Listener {
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        DollData.DOLL_DATA_LIST.removeIf(dollData -> event.getPlayer().getPendingConnection().getSocketAddress().toString().equals(dollData.getAddress()));
    }
}
