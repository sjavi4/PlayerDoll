package me.autobot.playerdoll.api.config;

import me.autobot.playerdoll.api.PlayerDollAPI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class AbstractConfig {
    public static final int VERSION = 32;
    private final boolean checkUpdate;
    private final File yamlFile;
    private YamlConfiguration yamlConfiguration;
    private int configVersion;

    public AbstractConfig(File file, boolean checkUpdate) {
        this.yamlFile = file;
        this.checkUpdate = checkUpdate;
        loadYAML();
        configVersion = yamlConfiguration.getInt("version");
        if (checkVersion()) {
            yamlConfiguration.set("version",VERSION);
            configVersion = VERSION;
        }
    }

    public abstract String name();
    public YamlConfiguration loadYAML() {
        return this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.yamlFile);
    }
    public void unloadYAML() {
        yamlConfiguration = null;
    }

    public boolean checkVersion() {
        if (checkUpdate && configVersion < VERSION) {
            PlayerDollAPI.getLogger().log(Level.WARNING,
                    "Config [{0}] version does not match the latest ({1}).\nDefault values will be used.",
                    new Object[]{name(), VERSION}
            );
            return false;
        }
        return true;
    }

    public void saveConfig() {
        if (this.yamlConfiguration == null) {
            loadYAML();
        }
        try {
            this.yamlConfiguration.save(this.yamlFile);
            PlayerDollAPI.getLogger().log(Level.INFO, "Saved config [{0}]", name());
        } catch (IOException e) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Fail to Save config [{0}]", name());
            throw new RuntimeException(e);
        }
    }

    public File getYamlFile() {
        return yamlFile;
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration == null ? loadYAML() : yamlConfiguration;
    }
}
