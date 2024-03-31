package me.autobot.playerdoll.EventListener;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Real Player Joining
        Player player = event.getPlayer();
        if (!DollManager.ONLINE_DOLL_MAP.containsKey(player.getUniqueId())) {
            return;
        }
        if (player.getName().length() == 1) {
            // Wrong Joining
            PlayerDoll.getPluginLogger().log(Level.INFO, "Did not capture this Doll, please Respawn");
            Runnable task = () -> player.kickPlayer("Wrong Joining");
            if (PlayerDoll.isFolia) {
                PlayerDoll.getFoliaHelper().entityTask(player,task,1);
            } else {
                task.run();
            }
        }

        if (!BasicConfig.get().broadcastDollJoin.getValue()) {
            event.setJoinMessage(null);
        }
    }
}
