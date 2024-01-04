package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class DollDisconnectEvent implements Listener {
    @EventHandler
    public void onDollDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        var dollMap = PlayerDoll.dollManagerMap;
        if (!dollMap.containsKey(name)) {
            playerDisconnect(event);
            return;
        }
        event.getPlayer().setFallDistance(0.0f);
        YamlConfiguration globalConfig = ConfigManager.getConfig();
        if (globalConfig.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers() - 1);
        }

        DollConfigManager dollConfigManager = DollConfigManager.getConfigManager(player);
        dollConfigManager.save();
        if (dollConfigManager.config.getBoolean("Remove")) {
            String uuid = player.getUniqueId().toString();
            File config = new File(PlayerDoll.getDollDirectory(),player.getName()+".yml");
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(()->{
                config.delete();
                dat.delete();
                dat_old.delete();
            }, 2, TimeUnit.SECONDS);
        }
        dollConfigManager.removeListener();
        if (!globalConfig.getBoolean("Global.DollDisconnectMessage")) {
            event.setQuitMessage(null);
        }
        dollMap.remove(name);
    }

    private void playerDisconnect(PlayerQuitEvent event) {
        PermissionManager.playerPermissionGroup.remove(event.getPlayer());
    }
}
