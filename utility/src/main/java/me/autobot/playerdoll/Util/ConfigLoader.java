package me.autobot.playerdoll.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.logging.Level;

public class ConfigLoader {
    private static ConfigLoader INSTANCE;
    private static Plugin plugin;
    public static String CUSTOM_LANGUAGE_NAME;
    private static final EnumMap<ConfigType, YamlConfiguration> CONFIGS = new EnumMap<>(ConfigType.class);
    public ConfigLoader(Plugin plugin) {
        ConfigLoader.plugin = plugin;
        INSTANCE = this;
    }
    public static ConfigLoader get() {
        return INSTANCE;
    }

    public YamlConfiguration getConfig(ConfigType type) {
        if (CONFIGS.containsKey(type)) {
            return CONFIGS.get(type);
        }
        YamlConfiguration config;

        if (type.getFile().exists()) {
            plugin.getLogger().log(Level.INFO, "Config ["+ type +"] Found, Load From Exist.");
            config = YamlConfiguration.loadConfiguration(type.getFile());
        } else {
            plugin.getLogger().log(Level.WARNING, "Config ["+type+"] Not Found, Generate From Resource.");
            config = getFromResource(type);
        }
        CONFIGS.put(type,config);
        return config;
    }

    private YamlConfiguration getFromResource(ConfigType type) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(type.getResource()));
        saveConfig(config,type);
        return config;
    }
    public void saveConfig(YamlConfiguration config, ConfigType type) {
        try {
            config.save(type.getFile());
            plugin.getLogger().log(Level.INFO, "Config ["+type+"] Saved Successfully");
        } catch (IOException ignored) {
            plugin.getLogger().log(Level.WARNING, "Config ["+type+"] Failed to Save.");
        }
    }
    public enum ConfigType {
        BASIC {
            @Override
            File getFile() {
                return new File(plugin.getDataFolder(),"config.yml");
            }
            @Override
            InputStream getResource() {
                return plugin.getResource("config.yml");
            }
        },
        LANGUAGE {
            @Override
            File getFile() {
                return new File(plugin.getDataFolder() + File.separator + "language","english.yml");
            }
            @Override
            InputStream getResource() {
                return plugin.getResource("english.yml");
            }
        },
        CUSTOM_LANGUAGE {
            @Override
            File getFile() {
                return new File(plugin.getDataFolder() + File.separator + "language",CUSTOM_LANGUAGE_NAME+".yml");
            }
            @Override
            InputStream getResource() {
                return LANGUAGE.getResource();
            }
            @Override
            public String toString() {
                return CUSTOM_LANGUAGE_NAME;
            }
        },
        FLAG {
            @Override
            File getFile() {
                return new File(plugin.getDataFolder(),"flag.yml");
            }

            @Override
            InputStream getResource() {
                return plugin.getResource("flag.yml");
            }
        };

        abstract File getFile();
        abstract InputStream getResource();
    }
}
