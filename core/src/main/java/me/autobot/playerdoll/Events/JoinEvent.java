package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.PermissionManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class JoinEvent implements Listener {
    @EventHandler
    public void OnDollJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PlayerDoll.dollManagerMap.containsKey(player.getName())) {
            playerJoin(event);
            return;
        }

        String permission = DollConfigManager.getConfigManager(player).config.getString("Owner.Perm");
        if (permission == null || permission.isBlank()) {
            permission = "default";
        }
        PermissionManager permissionManager = PermissionManager.permissionGroupMap.get(permission);

        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }
        //player.setSleepingIgnored(globalConfig.getBoolean("Global.DollNotCountSleeping"));
        player.setSleepingIgnored(permissionManager.notCountSleeping);

        if (!globalConfig.getBoolean("Global.DollJoinMessage")) {
            event.setJoinMessage(null);
        }

        if (permissionManager.bypassResidence) {
            player.setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier
        }
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setSaturation(0.0f);
        DollConfigManager.getConfigManager(player).setDollSetting("Pushable",false);
        //player.setCollidable(false);
        player.setHealth(20);
/*
        String configPrefix = globalConfig.getString("Global.DollPrefix");
        String prefix = ChatColor.translateAlternateColorCodes('&',configPrefix == null? "" : configPrefix);
        String configSuffix = globalConfig.getString("Global.DollSuffix");
        String suffix = ChatColor.translateAlternateColorCodes('&',configSuffix == null? "" : configSuffix);
        player.setPlayerListName(prefix + player.getName() + suffix);
        player.setDisplayName(prefix + player.getName() + suffix);


 */
        String prefix = ChatColor.translateAlternateColorCodes('&', permissionManager.prefix);
        String suffix = ChatColor.translateAlternateColorCodes('&', permissionManager.suffix);
        player.setDisplayName(prefix + player.getName() + suffix);
        player.setPlayerListName(prefix + player.getName() + suffix);

    }
    private void playerJoin(PlayerJoinEvent event) {
        PermissionManager.checkPlayerPermission(event.getPlayer());
    }
}
