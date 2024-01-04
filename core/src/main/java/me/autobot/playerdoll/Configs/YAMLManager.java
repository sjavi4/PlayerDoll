package me.autobot.playerdoll.Configs;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class YAMLManager {
    private final YamlConfiguration config;
    private final File file;
    private final String dollName;
    private YAMLManager(File file, String dollName) {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.file = file;
        this.dollName = dollName;
    }
    public static YAMLManager loadConfig(String dollName, boolean force) {
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
        return new YAMLManager(file,dollName);
    }

    public static boolean createFile(File file) {
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
        //this.saveConfig();
        if (PlayerDoll.dollManagerMap.keySet().contains(this.dollName)) {
            this.saveConfig();
        }
        this.unloadConfig();

        return loadConfig(this.dollName,false);
    }
    public void unloadConfig() {
        try {
            new FileReader(this.file).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public YamlConfiguration getConfig() {
        return this.config;
    }

}
