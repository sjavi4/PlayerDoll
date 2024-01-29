package me.autobot.playerdoll.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;


public final class ConfigManager {
    private static Plugin plugin;
    public static final String configVersion = "17";
    private static final EnumMap<ConfigType, File> configMap = new EnumMap<>(ConfigType.class);
    private static final EnumMap<ConfigType, YamlConfiguration> configs = new EnumMap<>(ConfigType.class);
    private ConfigFileWatcher watcher = null;

    public ConfigManager(Plugin plugin) {
        ConfigManager.plugin = plugin;

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        File dollDirectory = new File(plugin.getDataFolder() + File.separator + "doll");
        if (!dollDirectory.exists()) dollDirectory.mkdirs();
        File playerDirectory = new File(plugin.getDataFolder(), "playerPerm.yml");
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
            if (globalConfig.getBoolean("Global.AutoReload")) {
                System.out.println("AutoReload Enabled");
                registerWatcher();
            } else {
                System.out.println("AutoReload Disabled");
            }
        }
    }
    public void unloadAndSaveAllConfig() {
        configs.forEach( (s,c)-> saveToFile(c, configMap.get(s)));
    }
    private void loadConfig() {
        YamlConfiguration globalConfig = loadConfig(configMap.get(ConfigType.CONFIG), ConfigType.CONFIG);
        configMap.forEach( (s,f) -> {
            YamlConfiguration config = loadConfig(f, s);
            String version = config.getString("version");
            if (version == null || !version.equalsIgnoreCase(configVersion)) {
                if (globalConfig.getBoolean("Global.ReplaceConfig")) {
                    createFromResource(f);
                } else {
                    updateFromResource(f, config);
                }
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

    private void updateFromResource(File resourceFile, YamlConfiguration config) {
        InputStream resource = plugin.getResource(resourceFile.getName());
        if (resource != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8));
            config.addDefaults(defaultConfig);
            config.set("version", defaultConfig.getString("version"));
            config.options().copyDefaults(true);
            this.saveToFile(config, resourceFile);
            config.options().copyDefaults(false);
            try {
                resource.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
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
    /*
    public static void reloadAllConfig() {
        configs.put(ConfigType.CONFIG, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.CONFIG)));
        configs.put(ConfigType.LANGUAGE, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.LANGUAGE)));
        configs.put(ConfigType.FLAG, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.FLAG)));
        configs.put(ConfigType.PERMISSION, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.PERMISSION)));
    }

     */
    public static void reloadConfig(String name) {
        final String language = getConfig().getString("Global.Language");
        switch (name.toLowerCase()) {
            case "config" -> configs.put(ConfigType.CONFIG, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.CONFIG)));
            case "flag" -> configs.put(ConfigType.FLAG, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.FLAG)));
            case "permission" -> configs.put(ConfigType.PERMISSION, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.PERMISSION)));
        }
        if (name.equalsIgnoreCase(language)) {
            configs.put(ConfigType.LANGUAGE, YamlConfiguration.loadConfiguration(configMap.get(ConfigType.LANGUAGE)));
        }
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

    private void registerWatcher() {
        try {
            watcher = new ConfigFileWatcher(configMap.values().stream().map(File::toPath).toArray(Path[]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void stopWatcher() {
        if (watcher != null) {
            watcher.stop();
        }
    }
    public static YamlConfiguration getPermission() {
        return configs.get(ConfigType.PERMISSION);
    }
    public enum ConfigType {
        CONFIG,FLAG,LANGUAGE,PERMISSION
    }
}
