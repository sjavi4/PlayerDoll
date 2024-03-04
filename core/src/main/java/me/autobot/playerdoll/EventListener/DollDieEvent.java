package me.autobot.playerdoll.EventListener;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DollDieEvent implements Listener {
    @EventHandler
    public void onDollDie(PlayerDeathEvent event) {
        if (!DollManager.ONLINE_DOLL_MAP.containsKey(event.getEntity().getUniqueId())) {
            return;
        }
        BasicConfig basicConfig = BasicConfig.get();
        /*
        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (!globalConfig.getBoolean("Global.DollDeathMessage")) {
            event.setDeathMessage(null);
        }
         */
        if (!basicConfig.broadcastDollDeath.getValue()) {
            event.setDeathMessage(null);
        }

        //if (!DollConfigManager.getConfigManager(event.getEntity()).config.getBoolean("Remove")) {
            /*
            if ((boolean) PermissionManager.getPermissionGroup(DollConfigManager.getConfigManager(event.getEntity()).config.getString("Owner.Perm")).dollProperties.get("keepInventory")) {
                event.setKeepLevel(true);
                event.setKeepInventory(true);
                event.setDroppedExp(0);
                event.getDrops().clear();
            }

             */
        //} else {
            event.setKeepLevel(false);
            event.setKeepInventory(false);
            event.setDroppedExp(event.getEntity().getTotalExperience());
        //}
    }
}
