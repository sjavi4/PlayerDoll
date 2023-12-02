package me.autobot.playerdoll.Configs;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;


public final class ConfigManager {
    private static Plugin plugin;
    private static final String configVersion = "0.0.9";
    private final HashMap<String, File> configMap = new HashMap<>();
    public static final HashMap<String, YamlConfiguration> configs = new HashMap<>();
    public ConfigManager(Plugin plugin) {
        ConfigManager.plugin = plugin;

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        File dollDirectory = new File(plugin.getDataFolder() + File.separator + "doll");
        if (!dollDirectory.exists()) dollDirectory.mkdirs();


        configMap.put("config",new File(plugin.getDataFolder(), "config.yml"));
        configMap.put("lang",new File(plugin.getDataFolder() + File.separator + "language", "english.yml"));
        configMap.put("flag",new File(plugin.getDataFolder(), "flag.yml"));

        loadConfig();

        YamlConfiguration globalConfig = configs.get("config");
        if (globalConfig != null) {
            String language = globalConfig.getString("Global.Language");
            if (!(language == null || language.isEmpty() || language.isBlank())) {
                File langFile = new File(plugin.getDataFolder() + File.separator + "language", language + ".yml");
                if (langFile.exists()) {
                    configMap.put("lang", new File(plugin.getDataFolder() + File.separator + "language", language + ".yml"));
                    loadConfig(configMap.get("lang"),"lang");
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

    public HashMap<String,Integer> countPlayerDoll() {
        final HashMap<String, Integer> map = new HashMap<>();

        File[] dollFiles = new File(plugin.getDataFolder() + File.separator + "doll").listFiles();
        if (dollFiles == null || dollFiles.length == 0) return null;
        for (File files : dollFiles) {
            try (Scanner scanner = new Scanner(files)) {
                loop:while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    switch (line) {
                        case "Remove: true" -> {break loop;}
                        case "Owner:" -> {
                            String s = scanner.nextLine();
                            if (s.startsWith("UUID: ",2)) s = scanner.nextLine();
                            if (s.startsWith("Name: ",2)) {
                                String name = s.split(": ")[1];
                                if (map.containsKey(name)) {
                                    map.put(name, map.get(name) + 1);
                                } else {
                                    map.put(name, 1);
                                }
                            }
                        }
                        default -> PlayerDoll.pendingRespawnList.add(files.getName());
                    }
                }
                scanner.close();
                new FileReader(files).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private YamlConfiguration loadConfig(File file, String key) {
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
    private void reloadConfig(String key, File file) {
        configs.put(key, YamlConfiguration.loadConfiguration(file));
    }
}
