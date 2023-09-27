package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.Configs.YAMLManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class DollConfigManager {
    private final PropertyChangeSupport listener = new PropertyChangeSupport(this);
    private final Map<String, Object> data = new HashMap<>();
    private YamlConfiguration config;
    public DollConfigManager(YamlConfiguration dollConfig) {
        config = dollConfig;
        data.putAll(dollConfig.getValues(true));
    }

    public void addListener(PropertyChangeListener l) {
        listener.addPropertyChangeListener(l);
        data.forEach((k,v) -> listener.firePropertyChange(k,null,v));
        //read config setting
    }
    public void removeListener(PropertyChangeListener l) {
        listener.removePropertyChangeListener(l);
        data.forEach((k,v) -> config.set(k,v));
        //write config setting
    }

    public void setData(String path, Object value) {
        if (data.containsKey(path)) {
            data.put(path,value);
            listener.firePropertyChange(path,null,data.get(path));
        }
    }

    public Map<String,Object> getData() {
        return data;
    }


}
