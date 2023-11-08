package me.autobot.playerdoll.Configs;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        if (!file.getName().contains(".yml")) {
            System.out.println("Not A YAML config");
            return null;
        }
        if (force) {
            if (!createFile(file)) {
                return null;
            }
        }
        return new YAMLManager(file,mapKey);
    }

    public static boolean createFile(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean success = parent.mkdirs();
            if (!success) {
                System.out.println("Could NOT FORCE Load Config "+ parent.getName() +" Directory Creation Fail!");
                return false;
            }
        }
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.out.println("Could NOT FORCE Load Config "+ file.getName() +", File Creation Fail!");
                    return false;
                }
            } catch (IOException ignored) {}
        }
        return true;
    }

    private boolean checkVersion() {
        return this.config.contains("version") && this.config.getString("version").equalsIgnoreCase(PlayerDoll.getConfigVersion());
    }
    public YAMLManager createFromResource(String resourceFile) {
        if (this.file.length() == 0 || !checkVersion()) {
            InputStream resource = PlayerDoll.getPlugin().getResource(resourceFile);
            if (resource != null) {
                this.saveToFile(YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8)));
                try {
                    resource.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return YAMLManager.reloadConfig(this.mapKey);
            }
        }
        return null;
    }
    public static void reloadAllConfig() {
        Map<File,String> ref = new HashMap<>();
        YAMLManager.configMap.forEach((k,v) -> {
            ref.put(v.file,k);
            if (PlayerDoll.dollManagerMap.keySet().contains(PlayerDoll.getDollPrefix() + k)) {
                v.saveConfig();
            }
        });
        YAMLManager.configMap.clear();
        ref.forEach((f,s) -> loadConfig(f,s,false));

        //YAMLManager.configMap.values().forEach(YAMLManager::reloadConfig);
    }

    public static void unloadAllConfig() {
        YAMLManager.configMap.keySet().forEach(s -> {
            try {
                new FileReader(YAMLManager.getFile(s)).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        YAMLManager.configMap.clear();
        //YAMLManager.configMap.values().forEach(YAMLManager::unloadConfig);
    }
    public static YAMLManager reloadConfig(String mapKey) {
        if (!YAMLManager.configMap.containsKey(mapKey)) {
            return null;
        }
        YAMLManager value = YAMLManager.configMap.get(mapKey);
        if (PlayerDoll.dollManagerMap.keySet().contains(PlayerDoll.getDollPrefix() + mapKey)) {
            value.saveConfig();
        }
        //value.saveConfig();
        value.unloadConfig();
        return loadConfig(value.file,mapKey,false);
    }

    private void reloadConfig() {
        //this.saveConfig();
        if (PlayerDoll.dollManagerMap.keySet().contains(PlayerDoll.getDollPrefix() + this.mapKey)) {
            this.saveConfig();
        }
        this.unloadConfig();

        loadConfig(this.file,this.mapKey,false);
    }
/*
    public static boolean unloadConfig(String mapKey) {
        if (!configMap.containsKey(mapKey)) {
            return false;
        }
        configMap.remove(mapKey);
        return true;
    }


 */
    public void unloadConfig() {
        try {
            new FileReader(this.file).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        YAMLManager.configMap.remove(this.mapKey);
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
            //new FileReader(this.file).close();
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
