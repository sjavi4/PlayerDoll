package me.autobot.playerdoll;

import com.google.common.base.Charsets;
import me.autobot.playerdoll.Command.CommandManager;
import me.autobot.playerdoll.Command.CommandTabManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Events.*;
import me.autobot.playerdoll.GUI.GUIListener;
import me.autobot.playerdoll.GUI.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

public final class PlayerDoll extends JavaPlugin {
    private static Plugin plugin;

    public static final Map<String,DollManager> dollManagerMap = new HashMap<>();
    public static final Map<String,Integer> playerDollCountMap = new HashMap<>();
    private FileConfiguration newFlagConfig = null;
    private FileConfiguration newPermissionConfig = null;
    private final File flagConfig = new File(getDataFolder(), "flag.yml");
    private final File permissionConfig = new File(getDataFolder(), "permission.yml");

    private static GUIManager guiManager;

    public static Plugin getPlugin() {
        return plugin;
    }

    private static int maxplayer;
    public static GUIManager getGuiManager() {
        return guiManager;
    }

    private static String dollPrefix = "";

    private static String dollDirectory = "";

    public static String getDollPrefix() {
        return dollPrefix;
    }
    public static String getDollDirectory() {
        return dollDirectory;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!Bukkit.getVersion().contains("1.20")) {
            System.out.println("This Plugin Might Went Wrong If Version is Not 1.20");
        }

        plugin = this;

        dollDirectory =  PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll";

        File dollDirectory = new File(PlayerDoll.getPlugin().getDataFolder() + File.separator +"doll");
        YAMLManager.loadConfig(dollDirectory,null,true);


        String language = "english";

        this.saveDefaultConfig();

        YAMLManager config = YAMLManager.loadConfig(new File(this.getDataFolder(),"config.yml"),"config",false);
        if (config != null) {
            Map<String,Object> globalConfig = config.getConfig().getConfigurationSection("Global").getValues(false);
            dollPrefix = globalConfig.getOrDefault("DollPrefix", "BOT-").toString();
            language = globalConfig.getOrDefault("Language", "english").toString();
        }

        YAMLManager.loadConfig(flagConfig,"flag",false);
        YAMLManager.loadConfig(permissionConfig,"permission",false);


        File langDirectory = new File(getDataFolder() + File.separator + "language", "english.yml");
        final boolean langExist = langDirectory.exists();
        YAMLManager lang = YAMLManager.loadConfig(langDirectory,"lang",true);
        if (lang != null) {
            if (!langExist) {
                InputStream english = this.getResource("english.yml");
                if (english != null) {
                    lang.saveToFile(YamlConfiguration.loadConfiguration(new InputStreamReader(english, StandardCharsets.UTF_8)));
                }
            }
        }

        langDirectory = new File(getDataFolder() + File.separator + "language", language +".yml");
        final boolean customLangExist = langDirectory.exists();
        if (customLangExist) {
            YAMLManager.loadConfig(langDirectory, "lang", true);
        }

        maxplayer = Bukkit.getMaxPlayers();

        PluginManager pluginManager = Bukkit.getPluginManager();

        guiManager = new GUIManager();

        GUIListener guiListener = new GUIListener(guiManager);
        pluginManager.registerEvents(guiListener, this);


        getCommand("doll").setExecutor(new CommandManager());
        getCommand("doll").setTabCompleter(new CommandTabManager());

        pluginManager.registerEvents(new DollDisconnectEvent(),this);
        pluginManager.registerEvents(new DollJoinEvent(),this);
        pluginManager.registerEvents(new PlayerInteractDollEvent(),this);
        pluginManager.registerEvents(new DollDieEvent(),this);
        pluginManager.registerEvents(new DollRecipeEvent(),this);
        //pluginManager.registerEvents(new PlayerInventoryEvent(),this);
        //pluginManager.registerEvents(new TestPlayerinvHolder(),this);


        File[] dollFiles = new File(PlayerDoll.getDollDirectory()).listFiles();
        if (dollFiles != null) {
            for (File f : dollFiles) {
                try {
                    Scanner scanner = new Scanner(f);
                    while (scanner.hasNextLine()) {
                        String l = scanner.nextLine();
                        if (l.startsWith("Owner: ")) {
                            String[] a = l.split(" ");
                            if (playerDollCountMap.containsKey(a[1])) {
                                playerDollCountMap.put(a[1], playerDollCountMap.get(a[1]) + 1);
                            } else {
                                playerDollCountMap.put(a[1], 1);
                            }
                            break;
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(maxplayer);
        }
        //dollManagerMap.values().forEach(d -> YAMLManager.saveConfig(d.getDollName(),true));
        dollManagerMap.values().forEach(DollManager::disconnect);
        //YAMLManager.unloadAllConfig();
    }


    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        if (!this.flagConfig.exists()) {
            this.saveResource("flag.yml", false);
        }
        if (!this.permissionConfig.exists()) {
            this.saveResource("permission.yml", false);
        }
    }
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.newFlagConfig = YamlConfiguration.loadConfiguration(this.flagConfig);
        InputStream defFlagConfigStream = this.getResource("flag.yml");
        if (defFlagConfigStream != null) {
            this.newFlagConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defFlagConfigStream, Charsets.UTF_8)));
        }
        this.newPermissionConfig = YamlConfiguration.loadConfiguration(this.permissionConfig);
        InputStream defPermissionConfigStream = this.getResource("permission.yml");
        if (defPermissionConfigStream != null) {
            this.newPermissionConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defPermissionConfigStream, Charsets.UTF_8)));
        }
    }
    @Override
    public void saveConfig() {
        super.saveConfig();
        try {
            this.newFlagConfig.save(this.flagConfig);
            this.newPermissionConfig.save(this.permissionConfig);
        } catch (IOException var2) {
            getLogger().log(Level.SEVERE, "Could not save config to " + this.flagConfig, var2);
            getLogger().log(Level.SEVERE, "Could not save config to " + this.permissionConfig, var2);
        }
    }

}
