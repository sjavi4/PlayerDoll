package me.autobot.playerdoll.Util.Configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

public abstract class AbstractConfig {
    public YamlConfiguration yamlConfiguration;
    public static final int CURRENT_VERSION = 18;
    public int version;
    public AbstractConfig(YamlConfiguration config) {
        this.yamlConfiguration = config;
        this.version = config.getInt("version");
        checkVersion();
    }
    public void checkVersion() {
        if (version != CURRENT_VERSION) {
            Bukkit.getLogger().log(Level.WARNING,"[PlayerDoll] Config [" + getName() + "] version does not " +
                    "Match the Latest ("+ CURRENT_VERSION +"). " +
                    "Default value of missing keys will be used."
            );
        }
    }

    public abstract String getName();

    public Object getOrDefault(String path, Object defaultValue) {
        if (yamlConfiguration.contains(path)) {
            return yamlConfiguration.get(path);
        } else {
            yamlConfiguration.set(path,defaultValue);
            return defaultValue;
        }
    }
}
