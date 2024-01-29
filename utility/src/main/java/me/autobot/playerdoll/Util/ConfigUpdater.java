
package me.autobot.playerdoll.Util;

public class ConfigUpdater {
}
/*
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Vector;

public class ConfigUpdater {

    private final Plugin plugin;
    private final Map<ConfigManager.ConfigType, File> configMap = new EnumMap<>(ConfigManager.ConfigType.class);

    public ConfigUpdater(Plugin plugin) {
        this.plugin = plugin;
        configMap.put(ConfigManager.ConfigType.CONFIG, new File(plugin.getDataFolder(), "config.yml"));
        configMap.put(ConfigManager.ConfigType.LANGUAGE,new File(plugin.getDataFolder() + File.separator + "language", "english.yml"));
        configMap.put(ConfigManager.ConfigType.FLAG,new File(plugin.getDataFolder(), "flag.yml"));
        configMap.put(ConfigManager.ConfigType.PERMISSION,new File(plugin.getDataFolder(), "permission.yml"));
    }

    public boolean checkUpdate(ConfigManager.ConfigType type) {
        return YamlConfiguration.loadConfiguration(configMap.get(type)).getString("version").equals(ConfigManager.configVersion);
    }
    public void setFile(ConfigManager.ConfigType type, File file) {
        configMap.put(type,file);
    }

    public void updateFileFromResource(ConfigManager.ConfigType type) {
        InputStream resource = plugin.getResource(configMap.get(type).getName());
        if (resource == null) {
            return;
        }
        //Vector
        BufferedReader resourceReader = new BufferedReader(new InputStreamReader(resource));
        try {
            while (resourceReader.ready()) {
                resourceReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //this.saveToFile(YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8)), resourceFile);
        //try {
        //    resource.close();
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
    }
}

 */
