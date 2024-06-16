package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.socket.SocketHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class AsyncPlayerPreLogin implements Listener {
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (SocketHelper.DOLL_CLIENTS.containsKey(event.getUniqueId())) {
            PlayerDoll.scheduler.globalTask(() -> Bukkit.getOfflinePlayer(event.getUniqueId()).setOp(true));
        }
    }
}
