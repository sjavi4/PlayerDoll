package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class JoinEvent implements Listener {
    @EventHandler
    public void OnDollJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PlayerDoll.dollManagerMap.containsKey(player.getName())) {
            playerJoin(event);
            return;
        }
        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }
        player.setSleepingIgnored(globalConfig.getBoolean("Global.DollNotCountSleeping"));

        if (!globalConfig.getBoolean("Global.DollJoinMessage")) {
            event.setJoinMessage(null);
        }

        player.setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setSaturation(0.0f);
        DollConfigManager.getConfigManager(player).setDollSetting("Pushable",false);
        //player.setCollidable(false);
        player.setHealth(20);

        String configPrefix = globalConfig.getString("Global.DollPrefix");
        String prefix = ChatColor.translateAlternateColorCodes('&',configPrefix == null? "" : configPrefix);
        String configSuffix = globalConfig.getString("Global.DollSuffix");
        String suffix = ChatColor.translateAlternateColorCodes('&',configSuffix == null? "" : configSuffix);
        player.setPlayerListName(prefix + player.getName() + suffix);
        player.setDisplayName(prefix + player.getName() + suffix);
    }
    private void playerJoin(PlayerJoinEvent event) {
        //Player player = event.getPlayer();
        //PermissionAttachment attachment = player.addAttachment(PlayerDoll.getPlugin());
        /*
        PersistentDataContainer container = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(PlayerDoll.getPlugin(),"permission");
        container.getOrDefault(key, PersistentDataType.STRING,)

         */
    }
}
