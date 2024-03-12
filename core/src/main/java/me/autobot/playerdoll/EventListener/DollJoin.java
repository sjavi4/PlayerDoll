package me.autobot.playerdoll.EventListener;

import me.autobot.playerdoll.CustomEvent.DollJoinEvent;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.GUIs.Doll.DollInvStorage;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

public class DollJoin implements Listener {
    @EventHandler
    public void onDollJoin(DollJoinEvent event) {
        // Doll Joining
        Player player = event.getPlayer();

        PermissionAttachment attachment = player.addAttachment(PlayerDoll.getPlugin());

        BasicConfig basicConfig = BasicConfig.get();
        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }

        for (String perm : basicConfig.dollPermission.getValue()) {
            attachment.setPermission(perm,true);
        }

        DollManager.DOLL_PERMISSION_MAP.put(player.getUniqueId(),attachment);
        DollManager.ONLINE_DOLL_MAP.put(player.getUniqueId(), event.getDoll());
        /*
        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }

         */
        IDoll doll = event.getDoll();

        DollConfig dollConfig = DollConfig.getOnlineDollConfig(player.getUniqueId());
        doll.setDollConfig(dollConfig);

        player.setSleepingIgnored(true);
        //player.setSleepingIgnored((boolean)permissionManager.dollProperties.get("notCountSleeping"));
        /*
        if (!basicConfig.broadcastDollJoin.getValue()) {

        }

         */
        //if (!globalConfig.getBoolean("Global.DollJoinMessage")) {
            //event.setJoinMessage(null);
        //}
/*
        if ((boolean)permissionManager.dollProperties.get("bypassResidence")) {
            player.setMetadata("NPC", new FixedMetadataValue(PlayerDoll.getPlugin(), "NPC")); //Residence identifier
        }

 */
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setSaturation(0.0f);
        dollConfig.changeSetting(null, DollConfig.SettingType.PUSHABLE,false);
        //DollConfigManager.getConfigManager(player).setDollSetting("pushable",false);
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


        //new DollInvStorage(player);

        /*
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
            PlayerDoll.getFoliaHelper().globalTaskDelayed(task,1);
            //FoliaSupport.globalTask(task);
        } else {
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), task, 0);
        }
         */
        player.setFallDistance(0);
        player.setVelocity(new Vector(0,0,0));

        new DollInvStorage(player);

        // Trigger this after Doll Placed in World
        String prefix = "[BOT]";
        String suffix = "";
        //String prefix = ChatColor.translateAlternateColorCodes('&', (String) permissionManager.dollProperties.get("prefix"));
        //String suffix = ChatColor.translateAlternateColorCodes('&', (String) permissionManager.dollProperties.get("suffix"));
        player.setDisplayName(prefix + player.getName() + suffix);
        player.setPlayerListName(prefix + player.getName() + suffix);
        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().entityTeleportTo(player,doll.getCaller().getLocation());
        } else {
            player.teleport(doll.getCaller());
        }

        if (Bukkit.hasWhitelist() && player.isWhitelisted()) {
            Runnable task = () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"whitelist remove " + player.getName());
            if (PlayerDoll.isFolia) {
                PlayerDoll.getFoliaHelper().globalTask(task);
            } else {
                Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), task);
            }
        }

        List<String> messageList = basicConfig.dollChatWhenJoin.getValue();
        if (!messageList.isEmpty()) {
            long count = 0;
            long interval = Long.valueOf(basicConfig.dollChatWhenJoinInterval.getValue());
            for (String s : messageList) {
                if (s.isEmpty()) {
                    continue;
                }
                String replaced = s.replaceAll("%name%",player.getName()).replaceAll("%uuid%",player.getUniqueId().toString());
                Runnable task = () -> player.chat(replaced);
                if (PlayerDoll.isFolia) {
                    PlayerDoll.getFoliaHelper().entityTask(player,task, 1+count*interval);
                } else {
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),task,1+count*interval);
                }
                count++;
            }
        }
    }
}
