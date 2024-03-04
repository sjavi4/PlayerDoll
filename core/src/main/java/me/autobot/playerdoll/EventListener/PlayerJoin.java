package me.autobot.playerdoll.EventListener;

import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Real Player Joining
        Player player = event.getPlayer();
        if (!DollManager.ONLINE_DOLL_MAP.containsKey(player.getUniqueId())) {
            return;
        }
        //if (PlayerDoll.getluckPermsHelper() != null) {
            //PermissionManager.addPlayerExtern(p,PlayerDoll.getluckPermsHelper().getPlayerGroupName(p));
        //}
        //System.out.println("Trigger");
    }
}
