package me.autobot.playerdoll;

import me.autobot.playerdoll.Command.Utils.CommandLimit;
import me.autobot.playerdoll.Command.CommandHelper;
import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.*;
import me.autobot.playerdoll.Events.*;
import me.autobot.playerdoll.InvMenu.InvEventListener;
import me.autobot.playerdoll.InvMenu.InvManager;
import me.autobot.playerdoll.Command.Utils.CommandHelp;
import me.autobot.playerdoll.Command.Utils.CommandList;
import me.autobot.playerdoll.Command.Utils.CommandReload;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
    public static final HashMap<UUID,Integer> playerDollCountMap = new HashMap<>();
    public static final Set<String> pendingRespawnList = new HashSet<>();
    private static InvManager invManager;
    private static int maxplayer;

    public static final String dollIdentifier = "-";

    private static String dollDirectory = "";
    private static String playerUUIDDirectory = "";

    public static Plugin getPlugin() {
        return plugin;
    }
    public static String getDollDirectory() {
        return dollDirectory;
    }
    public static String getPlayerUUIDDirectory() {
        return playerUUIDDirectory;
    }
    //public static String getPlayerDirectory() {
    //    return playerDirectory;
    //}
    public static NamespacedKey permissionKey;
    public static InvManager getInvManager() {
        return invManager;
    }
    public static ConfigManager configManager;

    public static boolean isSpigot = false;
    public static boolean isPaperSeries = false;
    public static boolean isFolia = false;
    @Override
    public void onEnable() {
        gameVersionCheck();

        plugin = this;
        maxplayer = Bukkit.getMaxPlayers();

        configManager = new ConfigManager(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        dollDirectory = PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll";
        playerUUIDDirectory = PlayerDoll.getPlugin().getDataFolder() + File.separator + "player";
        permissionKey = new NamespacedKey(getPlugin(),"permission");
        PermissionManager.newInstance();
        invManager = new InvManager();
        InvEventListener invEventListener = new InvEventListener(invManager);
        pluginManager.registerEvents(invEventListener, this);

        getCommand("doll").setExecutor(new CommandHelper());
        getCommand("doll").setTabCompleter(new CommandHelper());

        CommandHelp help = new CommandHelp();
        getCommand("dollHelp").setExecutor(help);
        getCommand("dollHelp").setTabCompleter(help);
        getCommand("dollReload").setExecutor(new CommandReload());
        CommandList list = new CommandList();
        getCommand("dollList").setExecutor(list);
        getCommand("dollList").setTabCompleter(list);
        CommandLimit limit = new CommandLimit();
        getCommand("dollLimit").setExecutor(limit);
        pluginManager.registerEvents(new DollDisconnectEvent(), this);
        pluginManager.registerEvents(new JoinEvent(), this);
        pluginManager.registerEvents(new PlayerInteractDollEvent(), this);
        pluginManager.registerEvents(new DollDieEvent(), this);
        pluginManager.registerEvents(new DollRecipeEvent(), this);
        //does not work as good as nms.
        //pluginManager.registerEvents(new DollTargetEvent(),this);
        pluginManager.registerEvents(new DollKickEvent(), this);
        pluginManager.registerEvents(new DollDamageEvent(), this);
        getServerBranch();
        pluginVersionCheck();

        configManager.countPlayerDoll();


        prepareDollSpawn();

    }

    @Override
    public void onDisable() {
        YamlConfiguration config = ConfigManager.getConfig();
        if (config != null && config.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(maxplayer);
        }
        if (invManager != null) {
            invManager.invManager.keySet().forEach(HumanEntity::closeInventory);
        }
        if (isFolia) dollManagerMap.forEach((s,d) -> {
            IDoll.foliaDisconnect(false, Bukkit.getPlayer(s),d);
        });
        else dollManagerMap.values().forEach(IDoll::_disconnect);
        PermissionManager.savePlayerUUIDs();
        DollConfigManager.dollConfigManagerMap.values().forEach(d -> {
            if (d != null) {
                d.save();
                d.removeListener();
            }
        });
    }
    private void pluginVersionCheck() {
        if (!ConfigManager.getConfig().getBoolean("Global.CheckUpdate")) {
            return;
        }
        new Thread(()->{
            try {
                System.out.println("Checking New Versions...");
                InputStream release = new URL("https://raw.githubusercontent.com/sjavi4/PlayerDoll/main/ver.txt").openStream();
                String ver = new String(release.readAllBytes(),StandardCharsets.UTF_8).replaceAll("\\r?\\n","");
                if (!ver.equalsIgnoreCase(this.getDescription().getVersion())) {
                    System.out.println("New version available: " + ver + "(current:"+this.getDescription().getVersion()+")");
                    System.out.println("Visit https://modrinth.com/plugin/playerdoll/versions#all-versions to download the latest version.");
                } else {
                    System.out.println("You are running the latest version.");
                }
                /*
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

                 */
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    private void gameVersionCheck() {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (version) {
            case "v1_20_R1" -> {}
            case "v1_20_R2" -> {}
            case "v1_20_R3" -> {}
            case "v1_20_R4" -> {}
            default -> {
                System.out.println("Unknown or Unsupported Versions, Please Use with Cautions.");
                //getPluginLoader().disablePlugin(this);
                //yield false;
            }
        };
    }
    private void getServerBranch() {
        YamlConfiguration config = ConfigManager.getConfig();
        String mod = config.getString("Global.Mod");
        if (mod == null || mod.isBlank()) {
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
        for (String s : pendingRespawnList) {
            YAMLManager yamlManager = YAMLManager.loadConfig(s,false);
            if (yamlManager == null) {
                continue;
            }
            YamlConfiguration config = yamlManager.getConfig();
            PermissionManager permissionManager = PermissionManager.getInstance(config.getString("Owner.Perm"));
            if (permissionManager == null) {
                continue;
            }
            if (!permissionManager.canJoinAtStart) {
                continue;
            }
            if (!config.getBoolean("setting.Join_At_Start")) {
                continue;
            }
            IDoll doll = (IDoll) DollHelper.callSpawn(null, s, version);
            if (doll != null) {
                dollManagerMap.put(s, doll);
            }
        }
        pendingRespawnList.clear();
        /*
        if (!ConfigManager.getConfig().getBoolean("Global.DollJoinAtStart")) return;
        pendingRespawnList.forEach(f -> {
            if (f != null) {
                String stripFileName = f.substring(0, f.lastIndexOf("."));
                YAMLManager yaml = YAMLManager.loadConfig(stripFileName, false);
                if (yaml != null) {
                    var config = yaml.getConfig();
                    if (config.getBoolean("setting.Join_At_Start")) {
                        PlayerDoll.dollManagerMap.put(stripFileName, null);
                        IDoll doll = (IDoll) DollHelper.callSpawn(null, stripFileName,PlayerDoll.version);
                        PlayerDoll.dollManagerMap.put(stripFileName, doll);
                    }
                }
            }
        });

         */
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
