package me.autobot.playerdoll.util;

import me.autobot.playerdoll.config.AbstractConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;

public class ConfigLoader {
    private static ConfigLoader INSTANCE;
    private static final FileUtil fileUtil = FileUtil.INSTANCE;
    private static Plugin plugin;
    public static String CUSTOM_LANGUAGE_NAME;
    private static EnumMap<ConfigType, YamlConfiguration> CONFIGS;
    public ConfigLoader(Plugin plugin) {
        ConfigLoader.plugin = plugin;
        INSTANCE = this;
        CONFIGS = new EnumMap<>(ConfigType.class);
    }
    public static ConfigLoader get() {
        return INSTANCE;
    }

    public YamlConfiguration getConfig(ConfigType type) {
        if (CONFIGS.containsKey(type)) {
            return CONFIGS.get(type);
        }
        if (type == ConfigType.CUSTOM_LANGUAGE) {
            if (type.file.getName().endsWith("null.yml")) {
                type.setFile(fileUtil.getFile(fileUtil.getLanguageDir(), CUSTOM_LANGUAGE_NAME + ".yml"));
            }
        }

        if (type == ConfigType.LANGUAGE || type == ConfigType.CUSTOM_LANGUAGE) {
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(ConfigType.LANGUAGE.getFile());
            if (langConfig.getInt("version") != AbstractConfig.CURRENT_VERSION) {
                plugin.getLogger().warning("Config ["+ConfigType.LANGUAGE+"] Not Up to Date, Generate From Resource.");
                plugin.getLogger().warning("Config ["+type+"] Not Up to Date, Please Check Language File.");
                getFromResource(ConfigType.LANGUAGE);
            }
        }

        YamlConfiguration config;

        if (type.getFile().exists()) {
            plugin.getLogger().info("Config ["+ type +"] Found, Load From Exist.");
            config = YamlConfiguration.loadConfiguration(type.getFile());
        } else {
            plugin.getLogger().warning("Config ["+type+"] Not Found, Generate From Resource.");
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
            plugin.getLogger().info("Config ["+type+"] Saved Successfully");
        } catch (IOException ignored) {
            plugin.getLogger().warning("Config ["+type+"] Failed to Save.");
        }
    }
    public enum ConfigType {
        BASIC(fileUtil.getFile(fileUtil.getPluginPath(), "config.yml"), plugin.getResource("config.yml")),
        LANGUAGE(fileUtil.getFile(fileUtil.getLanguageDir(), "default.yml"), plugin.getResource("default.yml")),
        CUSTOM_LANGUAGE(fileUtil.getFile(fileUtil.getLanguageDir(), CUSTOM_LANGUAGE_NAME + ".yml"), LANGUAGE.getResource()) {
            @Override
            public String toString() {
                return CUSTOM_LANGUAGE_NAME;
            }
        },
        FLAG(fileUtil.getFile(fileUtil.getPluginPath(), "flag.yml"), plugin.getResource("flag.yml")),
        PERMISSION(fileUtil.getFile(fileUtil.getPluginPath(), "perm.yml"), plugin.getResource("perm.yml"));

        private File file;
        private final InputStream resource;
        ConfigType(File file, InputStream resource) {
            this.file = file;
            this.resource = resource;
        }

        public File getFile() {
            return file;
        }
        public void setFile(File file) {
            this.file = file;
        }
        public InputStream getResource() {
            return resource;
        }
    }
}
