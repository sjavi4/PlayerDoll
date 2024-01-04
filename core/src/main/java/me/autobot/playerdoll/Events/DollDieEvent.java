package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.GameRule;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DollDieEvent implements Listener {
    @EventHandler
    public void onDollDie(PlayerDeathEvent event) {
        if (!PlayerDoll.dollManagerMap.containsKey(event.getEntity().getName())) {
            return;
        }
        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (!globalConfig.getBoolean("Global.DollDeathMessage")) {
            event.setDeathMessage(null);
        }
        if (PermissionManager.getInstance(DollConfigManager.getConfigManager(event.getEntity()).config.getString("Owner.Perm")).keepInventory) {
            event.setKeepLevel(true);
            event.setKeepInventory(true);
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
}
