package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.GUIs.Doll.DollInvStorage;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
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
        DollConfigManager.getConfigManager(player).setDollSetting("pushable",false);
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

        new DollInvStorage(player);

        Map<RegisteredListener, EventExecutor> backupMap = new HashMap<>();

        HandlerList handlerList = event.getHandlers();
        final RegisteredListener[] registeredListeners = handlerList.getRegisteredListeners();
        for (RegisteredListener listener : registeredListeners) {
            try {
                Class<?> listenerClass = listener.getClass();
                Field executorField = listenerClass.getDeclaredField("executor");
                executorField.setAccessible(true);

                backupMap.put(listener, (EventExecutor) executorField.get(listener));

                executorField.set(listener, (EventExecutor) (listener1, event1) -> {});

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            Runnable task = () -> {
                backupMap.forEach((r,e) -> {
                    try {
                        Class<?> listenerClass = r.getClass();
                        Field executorField = listenerClass.getDeclaredField("executor");
                        executorField.setAccessible(true);

                        executorField.set(r, e);

                    } catch (NoSuchFieldException | IllegalAccessException ignored) {
                        throw new RuntimeException(ignored);
                    }
                });
            };
            if (PlayerDoll.isFolia) {
                FoliaSupport.globalTask(task);
            } else {
                Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), task, 0);
            }
        }
    }
    private void playerJoin(PlayerJoinEvent event) {
        PermissionManager.checkPlayerPermission(event.getPlayer());
    }
}
