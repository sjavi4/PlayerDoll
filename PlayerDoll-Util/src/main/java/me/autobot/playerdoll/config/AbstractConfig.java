package me.autobot.playerdoll.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class AbstractConfig {
    public YamlConfiguration yamlConfiguration;
    public static final int CURRENT_VERSION = 28;
    public int version;
    public AbstractConfig(YamlConfiguration config) {
        this.yamlConfiguration = config;
        this.version = config.getInt("version");
        if (!checkVersion()) {
            config.set("version",CURRENT_VERSION);
        }
    }
    public boolean checkVersion() {
        if (version != CURRENT_VERSION) {
            Bukkit.getLogger().warning("[PlayerDoll] Config [" + getName() + "] version does not " +
                    "Match the Latest ("+ CURRENT_VERSION +"). " +
                    "Default value of missing keys will be used."
            );
            Bukkit.getLogger().info("[PlayerDoll] Config [" + getName() + "] version updated.");
            return false;
        }
        return true;
    }

    public abstract String getName();
/*
    public Object getOrDefault(String path, Object defaultValue) {
        if (yamlConfiguration.contains(path)) {
            return yamlConfiguration.get(path);
        } else {
            yamlConfiguration.set(path,defaultValue);
            return defaultValue;
        }
    }

 */
}
