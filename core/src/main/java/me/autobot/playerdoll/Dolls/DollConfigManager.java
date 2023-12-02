package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DollConfigManager {
    private final PropertyChangeSupport listener = new PropertyChangeSupport(this);
    private final Map<String, Object> data = new HashMap<>();
    public YamlConfiguration config;
    private final Player player;
    public static final HashMap<Player,DollConfigManager> dollConfigManagerMap = new HashMap<>();

    public DollConfigManager(YamlConfiguration dollConfig, Player doll) {
        config = dollConfig;
        this.player = doll;
        data.putAll(dollConfig.getValues(true));
        dollConfigManagerMap.put(doll, this);
    }

    public void addListener(PropertyChangeListener l) {
        listener.addPropertyChangeListener(l);
        data.forEach((k,v) -> listener.firePropertyChange(k,null,v));
        //read config setting
    }
    public void removeListener(PropertyChangeListener l) {
        listener.removePropertyChangeListener(l);
        data.forEach((k,v) -> config.set(k,v));
        dollConfigManagerMap.remove(this.player);
        //write config setting
    }
    public void removeListener() {
        listener.removePropertyChangeListener(listener.getPropertyChangeListeners()[0]);
        data.forEach((k,v) -> config.set(k,v));
        dollConfigManagerMap.remove(this.player);
        //write config setting
    }

    public void setData(String path, Object value) {
        if (data.containsKey(path)) {
            data.put(path,value);
            listener.firePropertyChange(path,null,data.get(path));
        }
    }
    public void save() {
        try {
            File file = new File(PlayerDoll.getDollDirectory(), player.getName() + ".yml");
            data.forEach((k,v) -> config.set(k,v));
            config.save(file);
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,Object> getData() {
        return data;
    }


}
