package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.BasicConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    public static boolean firstJoin = false;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!firstJoin && PlayerDoll.BUNGEECORD) {
            firstJoin = true;
            // wait for 5s
            PlayerDoll.scheduler.globalTaskDelayed(() -> PlayerDoll.PLUGIN.prepareDollSpawn(BasicConfig.get().proxyAutoJoinDelay.getValue()), 100);
        }
        if (event.getPlayer().getName().startsWith("-") && !BasicConfig.get().broadcastDollJoin.getValue()) {
            event.setJoinMessage(null);
        }
//        if (!DollManager.ONLINE_DOLLS.containsKey(event.getPlayer().getUniqueId())) {
//            return;
//        }


    }
}
