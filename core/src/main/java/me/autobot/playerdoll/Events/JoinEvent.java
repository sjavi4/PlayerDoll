package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Dolls.DollConfigManager;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.util.*;
@SuppressWarnings("unchecked")
public class JoinEvent implements Listener {

    private static final Map<RegisteredListener, EventExecutor> backupMap = new HashMap<>();
    static {
        Runnable task = () -> {
            HandlerList handlerList = PlayerJoinEvent.getHandlerList();

            EnumMap<EventPriority,ArrayList<RegisteredListener>> modifiedMap = new EnumMap<>(EventPriority.class);
            ArrayList<RegisteredListener> modifiedList = new ArrayList<>();
            try {
                Field handlerslots = handlerList.getClass().getDeclaredField("handlerslots");
                handlerslots.setAccessible(true);
                EnumMap<EventPriority,ArrayList<RegisteredListener>> map = (EnumMap<EventPriority, ArrayList<RegisteredListener>>) handlerslots.get(handlerList);
                ArrayList<RegisteredListener> list = map.get(EventPriority.LOWEST);

                modifiedMap.putAll(map);
                modifiedList.addAll(list);

                for (RegisteredListener r : list) {
                    if (r.getPlugin() == PlayerDoll.getPlugin()) {
                        Collections.swap(modifiedList, modifiedList.indexOf(r), 0);
                    }
                };
                modifiedMap.put(EventPriority.LOWEST,modifiedList);
                handlerslots.set(handlerList,modifiedMap);

                RegisteredListener[] modifiedHandlers = modifiedList.toArray(new RegisteredListener[0]);

                Field handlers = handlerList.getClass().getDeclaredField("handlers");
                handlers.setAccessible(true);
                handlers.set(handlerList,modifiedHandlers);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            handlerList = PlayerJoinEvent.getHandlerList();
            final RegisteredListener[] registeredListeners = handlerList.getRegisteredListeners();
            for (RegisteredListener listener : registeredListeners) {
                try {
                    Class<?> listenerClass = listener.getClass();
                    Field executorField = listenerClass.getDeclaredField("executor");
                    executorField.setAccessible(true);
                    backupMap.put(listener, (EventExecutor) executorField.get(listener));

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };


        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().globalTask(task);
            //FoliaSupport.globalTask(task);
        } else {
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), task, 0);
        }
    }
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
        PermissionManager permissionManager = PermissionManager.getPermissionGroup(permission);

        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }

        player.setSleepingIgnored((boolean)permissionManager.dollProperties.get("notCountSleeping"));

        if (!globalConfig.getBoolean("Global.DollJoinMessage")) {
            event.setJoinMessage(null);
        }

        if ((boolean)permissionManager.dollProperties.get("bypassResidence")) {
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
        String prefix = ChatColor.translateAlternateColorCodes('&', (String) permissionManager.dollProperties.get("prefix"));
        String suffix = ChatColor.translateAlternateColorCodes('&', (String) permissionManager.dollProperties.get("suffix"));
        player.setDisplayName(prefix + player.getName() + suffix);
        player.setPlayerListName(prefix + player.getName() + suffix);

        new DollInvStorage(player);

        backupMap.forEach((r,e) ->{
            try {
                Class<?> listenerClass = r.getClass();
                Field executorField = listenerClass.getDeclaredField("executor");
                executorField.setAccessible(true);

                executorField.set(r, (EventExecutor) (listener1, event1) -> {});

            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                throw new RuntimeException(ignored);
            }
        });

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
            PlayerDoll.getFoliaHelper().globalTask(task);
            //FoliaSupport.globalTask(task);
        } else {
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), task, 0);
        }
    }
    private void playerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (PlayerDoll.getluckPermsHelper() != null) {
            PermissionManager.addPlayerExtern(p,PlayerDoll.getluckPermsHelper().getPlayerGroupName(p));
        }
        //PermissionManager.checkPlayerPermission(event.getPlayer());
    }
}
