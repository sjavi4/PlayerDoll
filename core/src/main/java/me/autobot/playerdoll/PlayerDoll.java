package me.autobot.playerdoll;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.*;
import me.autobot.playerdoll.Events.*;
import me.autobot.playerdoll.InvMenu.InvEventListener;
import me.autobot.playerdoll.InvMenu.InvManager;
import me.autobot.playerdoll.newCommand.CommandHandler;
import me.autobot.playerdoll.newCommand.Others.CommandHelp;
import me.autobot.playerdoll.newCommand.Others.CommandList;
import me.autobot.playerdoll.newCommand.Others.CommandReload;
import me.autobot.playerdoll.newCommand.TabSuggestion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class PlayerDoll extends JavaPlugin {

    private static Plugin plugin;

    public static String version = "";
    public static Map<String, IDoll> dollManagerMap = new HashMap<>();
    public static final HashMap<String,Integer> playerDollCountMap = new HashMap<>();
    public static final Set<String> pendingRespawnList = new HashSet<>();
    private static InvManager invManager;
    private static int maxplayer;

    public static final String dollIdentifier = "-";

    private static String dollDirectory = "";

    public static Plugin getPlugin() {
        return plugin;
    }
    public static String getDollDirectory() {
        return dollDirectory;
    }

    public static InvManager getInvManager() {
        return invManager;
    }
    public static ConfigManager configManager;

    public static boolean isSpigot = false;
    public static boolean isPaperSeries = false;
    public static boolean isFolia = false;
    private static ScoreboardHelper scoreboard;
    public static ScoreboardHelper getScoreboard() {
        return scoreboard;
    }

    @Override
    public void onEnable() {
        gameVersionCheck();

        plugin = this;
        maxplayer = Bukkit.getMaxPlayers();

        configManager = new ConfigManager(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        dollDirectory = PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll";

        invManager = new InvManager();
        InvEventListener invEventListener = new InvEventListener(invManager);
        pluginManager.registerEvents(invEventListener, this);

        getCommand("doll").setExecutor(new CommandHandler());
        getCommand("doll").setTabCompleter(new TabSuggestion());

        CommandHelp help = new CommandHelp();
        getCommand("dollHelp").setExecutor(help);
        getCommand("dollHelp").setTabCompleter(help);
        getCommand("dollReload").setExecutor(new CommandReload());
        CommandList list = new CommandList();
        getCommand("dollList").setExecutor(list);
        getCommand("dollList").setExecutor(list);
        pluginManager.registerEvents(new DollDisconnectEvent(), this);
        pluginManager.registerEvents(new DollJoinEvent(), this);
        pluginManager.registerEvents(new PlayerInteractDollEvent(), this);
        pluginManager.registerEvents(new DollDieEvent(), this);
        pluginManager.registerEvents(new DollRecipeEvent(), this);
        //does not work as good as nms.
        //pluginManager.registerEvents(new DollTargetEvent(),this);
        pluginManager.registerEvents(new DollKickEvent(), this);
        getServerBranch();
        pluginVersionCheck();

        var countMap = configManager.countPlayerDoll();
        if (countMap != null && countMap.size() != 0) playerDollCountMap.putAll(configManager.countPlayerDoll());

        //Folia not support
        if (!isFolia) scoreboard = new ScoreboardHelper();

        prepareDollSpawn();
    }

    @Override
    public void onDisable() {
        YamlConfiguration config = ConfigManager.configs.get("config");
        if (config != null && config.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(maxplayer);
        }
        if (invManager != null) {
            invManager.invManager.keySet().forEach(HumanEntity::closeInventory);
        }
        if (isFolia) dollManagerMap.values().forEach(d -> {
            d.foliaDisconnect(false);
        });
        else dollManagerMap.values().forEach(IDoll::_disconnect);
        DollConfigManager.dollConfigManagerMap.values().forEach(d -> {
            d.save();
            d.removeListener();
        });
    }
    private void pluginVersionCheck() {
        new Thread(()->{
            try {
                System.out.println("Checking New Versions...");
                InputStream release = new URL("https://raw.githubusercontent.com/sjavi4/PlayerDoll/main/version.txt").openStream();
                String[] urlVer = new String(release.readAllBytes(),StandardCharsets.UTF_8).split("\\r?\\n");
                String currentVer = this.getDescription().getVersion();
                for (String s: urlVer) {
                    String[] split = s.split(":");
                    if (version.equalsIgnoreCase(split[0])) {
                        if (!split[1].equalsIgnoreCase(currentVer)) {
                            System.out.println("New version available: " + split[1] + "(current:"+currentVer+")");
                            System.out.println("Visit https://modrinth.com/plugin/playerdoll/versions#all-versions to download the latest version.");
                        } else {
                            System.out.println("You are running the latest version.");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

       // });
    }
    private void gameVersionCheck() {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (version) {
            case "v1_20_R1" -> {}
            case "v1_20_R2" -> {}
            default -> {
                System.out.println("Unknown or Unsupported Versions, Please Use with Cautions.");
                //getPluginLoader().disablePlugin(this);
                //yield false;
            }
        };
    }
    private void getServerBranch() {
        YamlConfiguration config = ConfigManager.configs.get("config");
        String mod = config.getString("Global.Mod");
        if (mod == null || mod.isEmpty() || mod.isBlank()) {
            System.out.println("Auto-detecting Server Mod:" +getServer().getName());
            modCheck();
        } else {
            switch (mod.toLowerCase()) {
                case "spigot" -> PlayerDoll.isSpigot = true;
                case "paperseries" -> PlayerDoll.isPaperSeries = true;
                case "folia" -> PlayerDoll.isFolia = true;
                default -> {
                    System.out.println("Unknown mod, Disabled Plugin.");
                    getPluginLoader().disablePlugin(this);
                }
            }
        }
    }
    public void prepareDollSpawn() {
        if (!ConfigManager.configs.get("config").getBoolean("Global.DollJoinAtStart")) return;
        pendingRespawnList.forEach(f -> {
            if (f != null) {
                String stripFileName = f.substring(0, f.lastIndexOf("."));
                YAMLManager yaml = YAMLManager.loadConfig(stripFileName, false);
                if (yaml != null) {
                    var config = yaml.getConfig();
                    if (config.getBoolean("setting.Join At Start")) {
                        PlayerDoll.dollManagerMap.put(stripFileName, null);
                        IDoll doll = (IDoll) DollSpawnHelper.callSpawn(null, stripFileName,PlayerDoll.version);
                        PlayerDoll.dollManagerMap.put(stripFileName, doll);
                    }
                }
            }
        });
    }
    private boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
    private void modCheck() {
        if (getClass("io.papermc.paper.threadedregions.RegionizedServer")) {
            isFolia = true;
        } else if (getClass("com.destroystokyo.paper.PaperConfig") || getClass("io.papermc.paper.configuration.Configuration")) {
            isPaperSeries = true;
        } else if (getClass(("org.spigotmc.SpigotConfig"))) {
            isSpigot = true;
        }
    }
}