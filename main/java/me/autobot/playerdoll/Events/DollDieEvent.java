package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DollDieEvent implements Listener {
    @EventHandler
    public void onDollDie(PlayerDeathEvent event) {
        if (!PlayerDoll.dollManagerMap.containsKey(event.getEntity().getName())) {
            return;
        }
        if (YAMLManager.getConfig("config").getBoolean("Global.FollowKeepInventory")) {
            //if (Boolean.TRUE.equals(event.getEntity().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) {
            return;
            //}
        }
        if (YAMLManager.getConfig("config").getBoolean("Global.DollKeepInventory")) {
            event.setKeepLevel(true);
            event.setKeepInventory(true);
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
}
