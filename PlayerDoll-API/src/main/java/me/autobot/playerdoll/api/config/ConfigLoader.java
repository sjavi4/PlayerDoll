package me.autobot.playerdoll.api.config;

import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.PlayerDollPlugin;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.config.impl.FlagConfig;
import me.autobot.playerdoll.api.config.impl.PermConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ConfigLoader {

    public static final Map<String, AbstractConfig> CONFIGS = new ConcurrentHashMap<>();

    private final YamlConfiguration languageYAML;

    public ConfigLoader() {
        BasicConfig basicConfig = new BasicConfig(AbsConfigType.BASIC.getLocation());
        FlagConfig flagConfig = new FlagConfig(AbsConfigType.FLAG.getLocation());
        PermConfig permConfig = new PermConfig(AbsConfigType.PERMISSION.getLocation());

        CONFIGS.put(AbsConfigType.BASIC.registerName(), basicConfig);
        CONFIGS.put(AbsConfigType.FLAG.registerName(), flagConfig);
        CONFIGS.put(AbsConfigType.PERMISSION.registerName(), permConfig);

        // Check version for Default Language;
        PlayerDollPlugin plugin = PlayerDollAPI.getInstance();
        FileUtil fileUtil = plugin.getFileUtil();
        File defaultLangFile = fileUtil.getFile(fileUtil.getLanguageDir(), "default.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(defaultLangFile);
        if (langConfig.getInt("version") != AbstractConfig.VERSION) {
            plugin.getLogger().warning("Language Config [default] Not Up to Date, Generate From Resource.");
            langConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(AbsConfigType.LANGUAGE.getResourceLocation())));
            try {
                langConfig.save(defaultLangFile);
                plugin.getLogger().log(Level.INFO, "Config [{0}] Saved Successfully", AbsConfigType.LANGUAGE.registerName());
            } catch (IOException ignored) {
                plugin.getLogger().log(Level.WARNING, "Config [{0}] Failed to Save.", AbsConfigType.LANGUAGE.registerName());
            }
        }

        setLanguage(basicConfig.pluginLanguage.getValue());
        languageYAML = YamlConfiguration.loadConfiguration(AbsConfigType.LANGUAGE.getLocation());
    }

    public <C extends AbstractConfig> C loadConfig(AbsConfigType configType, C config) {
        if (CONFIGS.containsKey(configType.registerName())) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Duplicated Config [{0}] is Loaded", configType.registerName());
            return null;
        }
        CONFIGS.put(configType.registerName(), config);
        return config;
    }

    private void setLanguage(String fileName) {
        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        AbsConfigType.LANGUAGE.setLocation(fileUtil.getFile(fileUtil.getLanguageDir(), fileName.endsWith(".yml") ? fileName : fileName.concat(".yml")));
    }

    public AbstractConfig getConfig(AbsConfigType configType) {
        return CONFIGS.getOrDefault(configType.registerName(), null);
    }

    public <C extends AbstractConfig> C getConfig(AbsConfigType configType, Class<C> configClass) {
        return configClass.cast(getConfig(configType));
    }

    public YamlConfiguration getLanguageYAML() {
        return languageYAML;
    }

    public BasicConfig getBasicConfig() {
        return getConfig(AbsConfigType.BASIC, BasicConfig.class);
    }

    public PermConfig getPermConfig() {
        return getConfig(AbsConfigType.PERMISSION, PermConfig.class);
    }

    public FlagConfig getFlagConfig() {
        return getConfig(AbsConfigType.FLAG, FlagConfig.class);
    }
}
