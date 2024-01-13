package me.autobot.playerdoll.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public final class ConfigManager {
    private static Plugin plugin;
    private static final String configVersion = "15";
    private final EnumMap<ConfigType, File> configMap = new EnumMap<>(ConfigType.class);
    private static final EnumMap<ConfigType, YamlConfiguration> configs = new EnumMap<>(ConfigType.class);

    public ConfigManager(Plugin plugin) {
        ConfigManager.plugin = plugin;

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        File dollDirectory = new File(plugin.getDataFolder() + File.separator + "doll");
        if (!dollDirectory.exists()) dollDirectory.mkdirs();
        File playerDirectory = new File(plugin.getDataFolder() + File.separator + "player", "uuids.yml");
        if (!playerDirectory.exists()) {
            playerDirectory.getParentFile().mkdirs();
            try {
                playerDirectory.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        File backupDirectory = new File(plugin.getDataFolder() + File.separator + "backup");
        if (!backupDirectory.exists()) backupDirectory.mkdirs();

        configMap.put(ConfigType.CONFIG,new File(plugin.getDataFolder(), "config.yml"));
        configMap.put(ConfigType.LANGUAGE,new File(plugin.getDataFolder() + File.separator + "language", "english.yml"));
        configMap.put(ConfigType.FLAG,new File(plugin.getDataFolder(), "flag.yml"));
        configMap.put(ConfigType.PERMISSION,new File(plugin.getDataFolder(), "permission.yml"));

        loadConfig();

        YamlConfiguration globalConfig = configs.get(ConfigType.CONFIG);
        if (globalConfig != null) {
            String language = globalConfig.getString("Global.Language");
            if (!(language == null || language.isBlank())) {
                File langFile = new File(plugin.getDataFolder() + File.separator + "language", language + ".yml");
                if (langFile.exists()) {
                    configMap.put(ConfigType.LANGUAGE, new File(plugin.getDataFolder() + File.separator + "language", language + ".yml"));
                    loadConfig(configMap.get(ConfigType.LANGUAGE),ConfigType.LANGUAGE);
                }
            }
        }
    }
    public void unloadAndSaveAllConfig() {
        configs.forEach( (s,c)-> saveToFile(c, configMap.get(s)));
    }
    private void loadConfig() {
        configMap.forEach( (s,f) -> {
            YamlConfiguration config = loadConfig(f, s);
            String version = config.getString("version");
            if (version == null || !version.equalsIgnoreCase(configVersion)) {
                createFromResource(f);
                reloadConfig(s,f);
            }
        });
    }

    private YamlConfiguration loadConfig(File file, ConfigType key) {
        if (!file.exists()) createFromResource(file);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(key,config);
        return config;
    }
    private void createFromResource(File resourceFile) {
        InputStream resource = plugin.getResource(resourceFile.getName());
        if (resource != null) {
            this.saveToFile(YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8)), resourceFile);
            try {
                resource.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void saveToFile(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void reloadConfig(ConfigType key, File file) {
        configs.put(key, YamlConfiguration.loadConfiguration(file));
    }

    public static YamlConfiguration getConfig() {
        return configs.get(ConfigType.CONFIG);
    }
    public static YamlConfiguration getLanguage() {
        return configs.get(ConfigType.LANGUAGE);
    }
    public static YamlConfiguration getFlag() {
        return configs.get(ConfigType.FLAG);
    }
    public static YamlConfiguration getPermission() {
        return configs.get(ConfigType.PERMISSION);
    }
    public enum ConfigType {
        CONFIG,FLAG,LANGUAGE,PERMISSION
    }
}
