package me.autobot.playerdoll;

import com.google.common.base.Charsets;
import me.autobot.playerdoll.Command.CommandManager;
import me.autobot.playerdoll.Command.CommandTabManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Events.*;
import me.autobot.playerdoll.InvMenu.InvEventListener;
import me.autobot.playerdoll.InvMenu.InvManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

public final class PlayerDoll extends JavaPlugin {
    private static Plugin plugin;

    public static final Map<String,DollManager> dollManagerMap = new HashMap<>();
    public static final Map<String,Integer> playerDollCountMap = new HashMap<>();
    private static final String configVersion = "0.0.1";
    private static InvManager invManager;

    public static Plugin getPlugin() {
        return plugin;
    }

    private static int maxplayer;

    private static String dollPrefix = "";

    private static String dollDirectory = "";

    public static String getDollPrefix() {
        return dollPrefix;
    }
    public static String getDollDirectory() {
        return dollDirectory;
    }

    public static InvManager getInvManager() {
        return invManager;
    }

    public static String getConfigVersion() {
        return configVersion;
    }

    @Override
    public void onEnable() {

        if (!Bukkit.getVersion().contains("1.20")) {
            System.out.println("This Plugin Might Went Wrong If Version is Not 1.20");
        }

        plugin = this;
        maxplayer = Bukkit.getMaxPlayers();

        dollDirectory =  PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll";
        File dollDirectory = new File(PlayerDoll.getPlugin().getDataFolder() + File.separator +"doll");
        YAMLManager.loadConfig(dollDirectory,null,true);


        File configFile = new File(getDataFolder(), "config.yml");

        YAMLManager configYAML = YAMLManager.loadConfig(configFile,"config",true);
        configYAML.createFromResource("config.yml");
        configYAML = YAMLManager.reloadConfig("config");
        Map<String, Object> globalConfig = configYAML.getConfig().getConfigurationSection("Global").getValues(false);
        dollPrefix = globalConfig.getOrDefault("DollPrefix", "BOT-").toString();
        String languagePref = globalConfig.getOrDefault("Language", "english").toString();

        File languageFile = new File(getDataFolder() + File.separator + "language", languagePref + ".yml");
        if (!languageFile.exists()) {
            languagePref = "english";
            languageFile = new File(getDataFolder() + File.separator + "language", "english.yml");
        }
        YAMLManager languageYAML = YAMLManager.loadConfig(languageFile,"lang",true);
        if (languagePref.equalsIgnoreCase("english")) {
            languageYAML.createFromResource("english.yml");
            languageYAML = YAMLManager.reloadConfig("lang");
        }

        File flagFile = new File(getDataFolder(), "flag.yml");
        YAMLManager flagYAML = YAMLManager.loadConfig(flagFile,"flag",true);
        flagYAML.createFromResource("flag.yml");
        flagYAML = YAMLManager.reloadConfig("flag");

        PluginManager pluginManager = Bukkit.getPluginManager();

        invManager = new InvManager();
        InvEventListener invEventListener = new InvEventListener(invManager);
        pluginManager.registerEvents(invEventListener,this);

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
                    scanner.close();
                    new FileReader(f).close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(this,() -> {
            try {
                System.out.println("Checking New Versions...");
                InputStream release = new URL("https://raw.githubusercontent.com/sjavi4/PlayerDoll/main/version.txt").openStream();
                String urlVer = new String(release.readAllBytes(),StandardCharsets.UTF_8).replace("\n", "").replace("\r", "");
                String currentVer = this.getDescription().getVersion();
                if (!urlVer.equalsIgnoreCase(currentVer)) {
                    System.out.println("New version available: " + urlVer + "(current:"+currentVer+")");
                    System.out.println("Visit https://github.com/sjavi4/PlayerDoll/releases to download the latest version.");
                    System.out.println("Visit https://modrinth.com/plugin/playerdoll/versions#all-versions to download the latest version.");
                } else {
                    System.out.println("You are running the latest version.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onDisable() {
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(maxplayer);
        }
        if (invManager != null) {
            invManager.invManager.keySet().forEach(HumanEntity::closeInventory);
        }
        //dollManagerMap.values().forEach(d -> YAMLManager.saveConfig(d.getDollName(),true));
        dollManagerMap.values().forEach(DollManager::disconnect);
        //YAMLManager.unloadAllConfig();
    }

}
