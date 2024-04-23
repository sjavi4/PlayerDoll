package me.autobot.playerdoll.EventListener;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.UUID;


public class DollDisconnectEvent implements Listener {
    @EventHandler
    public void onDollDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        BasicConfig basicConfig = BasicConfig.get();

        var dollMap = DollManager.ONLINE_DOLL_MAP;
        if (!dollMap.containsKey(uuid)) {
            if (basicConfig.convertPlayer.getValue()) {
                DollManager.ONLINE_PLAYER_MAP.remove(uuid);
            }
            //playerDisconnect(event);
            return;
        }
        dollMap.get(uuid).getCaller().sendMessage( LangFormatter.YAMLReplaceMessage("DollDisconnected",name));
        event.getPlayer().setFallDistance(0.0f);


        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()-1);
        }

        PermissionAttachment attachment = DollManager.DOLL_PERMISSION_MAP.get(uuid);
        for (String perm : basicConfig.dollPermission.getValue()) {
            attachment.unsetPermission(perm);
        }
        DollManager.DOLL_PERMISSION_MAP.remove(uuid);
        /*
        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers() - 1);
        }

        DollConfigManager dollConfigManager = DollConfigManager.getConfigManager(player);
        dollConfigManager.save();
        if (dollConfigManager.config.getBoolean("Remove")) {
            DollManager.getInstance().removeDoll(name);
            /*
            //String uuid = player.getUniqueId().toString();
            File config = new File(PlayerDoll.getDollDirectory(),player.getName()+".yml");
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(()->{
                config.delete();
                dat.delete();
                dat_old.delete();
            }, 2, TimeUnit.SECONDS);

             */

        //dollConfigManager.removeListener();
    /*
        if (!globalConfig.getBoolean("Global.DollDisconnectMessage")) {
            event.setQuitMessage(null);
        }

     */
        if (!basicConfig.broadcastDollDisconnect.getValue()) {
            event.setQuitMessage(null);
        }
        dollMap.remove(uuid);
        //var invMap = PlayerDoll.dollInvStorage;
        //invMap.get(name).closeAllInv();
    }
/*
    private void playerDisconnect(PlayerQuitEvent event) {
        PermissionManager.removePlayer(event.getPlayer());
    }

 */
}
