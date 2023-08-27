package me.autobot.playerdoll.Events;

import com.google.common.base.Charsets;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DollJoinEvent implements Listener {
    @EventHandler
    public void OnDollJoin(PlayerJoinEvent event) {
        if (!PlayerDoll.dollManagerMap.containsKey(event.getPlayer().getName())) {
            return;
        }
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }
    }
}
