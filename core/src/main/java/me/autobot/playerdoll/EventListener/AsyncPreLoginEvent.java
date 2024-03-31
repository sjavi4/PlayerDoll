package me.autobot.playerdoll.EventListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class AsyncPreLoginEvent implements Listener {
    @EventHandler
    public void onDollPreLogin(AsyncPlayerPreLoginEvent event) {
        /*
        if (!Bukkit.hasWhitelist()) {
            return;
        }
        OfflinePlayer doll = Bukkit.getOfflinePlayer(event.getUniqueId());
        if (event.getKickMessage().equalsIgnoreCase("PlayerDoll") && event.getName().startsWith("-") && event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            Runnable task = () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"whitelist add " + doll.getName());
            if (PlayerDoll.isFolia) {
                PlayerDoll.getFoliaHelper().globalTask(task);
            } else {
                Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), task);
            }
        }

         */
    }
}
