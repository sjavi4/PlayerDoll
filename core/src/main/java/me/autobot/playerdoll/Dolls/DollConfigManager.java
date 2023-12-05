package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DollConfigManager {
    private final PropertyChangeSupport listener = new PropertyChangeSupport(this);
    private final Map<String, Object> dollSetting = new HashMap<>();
    //private final Map<String, Object> generalSetting = new HashMap<>();
    public YamlConfiguration config;
    private final Player player;
    public static final HashMap<Player,DollConfigManager> dollConfigManagerMap = new HashMap<>();

    public DollConfigManager(YamlConfiguration dollConfig, Player doll) {
        config = dollConfig;
        this.player = doll;
        dollSetting.putAll(config.getConfigurationSection("setting").getValues(true));
        //generalSetting.putAll(config.getConfigurationSection("generalSetting").getValues(true));
        dollConfigManagerMap.put(doll, this);
    }

    public void addListener(PropertyChangeListener l) {
        listener.addPropertyChangeListener(l);
        dollSetting.forEach((k, v) -> listener.firePropertyChange(k,null,v));
        //read config setting
    }
    public void removeListener(PropertyChangeListener l) {
        listener.removePropertyChangeListener(l);
        dollSetting.forEach((k, v) -> config.set("setting."+k,v));
        dollConfigManagerMap.put(this.player, null);
        //write config setting
    }
    public void removeListener() {
        listener.removePropertyChangeListener(listener.getPropertyChangeListeners()[0]);
        dollSetting.forEach((k, v) -> config.set("setting."+k,v));
        dollConfigManagerMap.put(this.player, null);
        //write config setting
    }

    public void setDollSetting(String path, Object value) {
        if (dollSetting.containsKey(path)) {
            dollSetting.put(path,value);
            listener.firePropertyChange(path,null, dollSetting.get(path));
        }
    }
    public void save() {
        try {
            File file = new File(PlayerDoll.getDollDirectory(), player.getName() + ".yml");
            dollSetting.forEach((k, v) -> config.set("setting."+k,v));
            //generalSetting.forEach((k,v) -> config.set("generalSetting."+k,v));
            config.save(file);
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,Object> getDollSetting() {
        return dollSetting;
    }
    public Map<String,Object> getGeneralSetting() {
        if (config.contains("generalSetting")) {
            return config.getConfigurationSection("generalSetting").getValues(true);
        } else {
            config.createSection("generalSetting");
            return new HashMap<>();
        }
    }
    public Map<String,Object> getPlayerSetting(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return getPlayerSetting(uuid.toString());
    }
    public Map<String,Object> getPlayerSetting(String uuid) {
        if (config.contains("playerSetting."+uuid)) {
            return config.getConfigurationSection("playerSetting." + uuid).getValues(true);
        } else {
            config.createSection("playerSetting." + uuid);
            return new HashMap<>();
        }
    }
    public void setGeneralSetting(String path, Object value) {
        config.set("generalSetting."+path,value);
    }
    public void setPlayerSetting(Player player, String path, Object value) {
        config.set("playerSetting."+player.getUniqueId()+"."+path,value);
    }
    public void setPlayerSetting(String uuid, String path, Object value) {
        config.set("playerSetting."+uuid+"."+path,value);
    }

    public static DollConfigManager getConfigManager(Player player) {
        return dollConfigManagerMap.get(player);
    }
    public static DollConfigManager getConfigManager(String player) {
        return dollConfigManagerMap.get(Bukkit.getPlayer(player));
    }
}
