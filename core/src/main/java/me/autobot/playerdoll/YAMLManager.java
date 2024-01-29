package me.autobot.playerdoll;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YAMLManager {
    private final YamlConfiguration config;
    private final File file;
    private final String dollName;
    private final boolean lossy;
    private static final Map<String, YAMLManager> YAMLMap = new HashMap<>();
    private YAMLManager(File file, String dollName, boolean lossy) {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.file = file;
        this.dollName = dollName;
        this.lossy = lossy;
        if (!lossy) YAMLMap.put(this.dollName,this);
    }
    public static YAMLManager loadConfig(String dollName, boolean force, boolean lossy) {
        if (!lossy && YAMLMap.containsKey(dollName)) {
            return YAMLMap.get(dollName);
        }
        File file = new File(PlayerDoll.getDollDirectory(),dollName+".yml");
        if (!file.getName().contains(".yml")) {
            return null;
        }
        if (!file.exists()) {
            if (force) {
                if (!createFile(file)) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return new YAMLManager(file,dollName, lossy);
    }

    private static boolean createFile(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean success = parent.mkdirs();
            if (!success) {
                return false;
            }
        }
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    return false;
                }
            } catch (IOException ignored) {
                return false;
            }
        }
        return true;
    }
    public YAMLManager reloadConfig() {
        if (PlayerDoll.dollManagerMap.keySet().contains(this.dollName)) {
            this.saveConfig();
        }
        this.unloadConfig();

        return loadConfig(this.dollName,false, this.lossy);
    }
    public void unloadConfig() {
        if (lossy) return;
        try {
            new FileReader(this.file).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        YAMLMap.remove(this.dollName);
    }
    public void saveConfig() {
        if (lossy) return;
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        YAMLMap.remove(this.dollName);
    }
    public YamlConfiguration getConfig() {
        return this.config;
    }

}
