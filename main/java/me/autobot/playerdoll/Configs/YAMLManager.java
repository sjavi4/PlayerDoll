package me.autobot.playerdoll.Configs;

import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YAMLManager {
    static final Map<String, YAMLManager> configMap = new HashMap<>();

    final Map<String, Object> dataMap = new HashMap<>();
    private final YamlConfiguration config;
    private final File file;
    private final String mapKey;
    private YAMLManager(File file, String mapKey) {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.file = file;
        this.mapKey = mapKey;
        this.dataMap.putAll(this.config.getValues(true));
        configMap.put(mapKey, this);
    }
    public static YAMLManager loadConfig(File file, String mapKey , boolean force) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            if (!force) {
                System.out.println("Could NOT Load Config "+ parent.getName() +" Directory NOT Found!");
                return null;
            }
            boolean success = parent.mkdirs();
            if (!success) {
                System.out.println("Could NOT FORCE Load Config "+ parent.getName() +" Directory Creation Fail!");
                return null;
            }
        }
        if (!file.getName().contains(".yml")) {
            System.out.println("Not A YAML config");
            return null;
        }
        //return null;
        if (!file.exists()) {
            if (!force) {
                System.out.println("Could NOT Load Config "+ file.getName() +" File NOT Found!");
                return null;
            }
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.out.println("Could NOT FORCE Load Config "+ file.getName() +" File Creation Fail!");
                    return null;
                }
            } catch (IOException ignored) {}
        }
        return new YAMLManager(file,mapKey);

    }

    public static void reloadAllConfig() {
        YAMLManager.configMap.values().forEach(YAMLManager::reloadConfig);
    }
    public static YAMLManager reloadConfig(String mapKey) {
        if (!YAMLManager.configMap.containsKey(mapKey)) {
            return null;
        }
        YAMLManager value = YAMLManager.configMap.get(mapKey);
        value.saveConfig();
        return loadConfig(value.file,mapKey,false);
    }

    private void reloadConfig() {
        this.saveConfig();
        loadConfig(this.file,this.mapKey,false);
    }

    public static boolean saveConfig(String mapKey, boolean unload) {
        if (!configMap.containsKey(mapKey)) {
            return false;
        }
        configMap.get(mapKey).saveConfig();
        if (unload) {
            configMap.remove(mapKey);
        }
        return true;
    }

    public static void saveAllConfig() {
        YAMLManager.configMap.values().forEach(YAMLManager::saveConfig);
    }
    public boolean saveToFile(YamlConfiguration yamlConfiguration) {
        try {
            yamlConfiguration.save(this.file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static YamlConfiguration getConfig(String mapKey) {
        return YAMLManager.configMap.containsKey(mapKey)? YAMLManager.configMap.get(mapKey).config : null;
    }
    public static File getFile(String mapKey) {
        return YAMLManager.configMap.containsKey(mapKey)? YAMLManager.configMap.get(mapKey).file : null;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public File getFile() {
        return this.file;
    }
}
