package me.autobot.playerdoll;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.AbstractDoll;
import me.autobot.playerdoll.Dolls.FoliaDollImpl;
import me.autobot.playerdoll.Dolls.PaperDollImpl;
import me.autobot.playerdoll.Dolls.SpigotDollImpl;
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
    public static Map<String, AbstractDoll> dollManagerMap = new HashMap<>();
    public static final Map<String,Integer> playerDollCountMap = new HashMap<>();
    public static final List<String> pendingRespawnList = new ArrayList<>();
    private static final String configVersion = "0.0.6";
    private static InvManager invManager;
    private static int maxplayer;

    private static String dollPrefix = "";

    private static String dollDirectory = "";

    public static Plugin getPlugin() {
        return plugin;
    }
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

    public static boolean isSpigot = false;
    public static boolean isPaperSeries = false;
    public static boolean isFolia = false;

    @Override
    public void onEnable() {
        if (!gameVersionCheck()) return;

        plugin = this;
        maxplayer = Bukkit.getMaxPlayers();

        configCheck();

        PluginManager pluginManager = Bukkit.getPluginManager();

        invManager = new InvManager();
        InvEventListener invEventListener = new InvEventListener(invManager);
        pluginManager.registerEvents(invEventListener,this);

        //getCommand("doll").setExecutor(new CommandManager());
        //getCommand("doll").setTabCompleter(new CommandTabManager());

        getCommand("doll").setExecutor(new CommandHandler());
        getCommand("doll").setTabCompleter(new TabSuggestion());

        CommandHelp help = new CommandHelp();
        getCommand("dollHelp").setExecutor(help);
        getCommand("dollHelp").setTabCompleter(help);
        getCommand("dollReload").setExecutor(new CommandReload());
        CommandList list = new CommandList();
        getCommand("dollList").setExecutor(list);
        getCommand("dollList").setExecutor(list);
        pluginManager.registerEvents(new DollDisconnectEvent(),this);
        //pluginManager.registerEvents(new DollJoinEvent(),this);
        pluginManager.registerEvents(new PlayerInteractDollEvent(),this);
        pluginManager.registerEvents(new DollDieEvent(),this);
        pluginManager.registerEvents(new DollRecipeEvent(),this);
        pluginManager.registerEvents(new DollKickEvent(), this);
        //pluginManager.registerEvents(new PlayerInventoryEvent(),this);
        //pluginManager.registerEvents(new TestPlayerinvHolder(),this);
        getServerBranch();
        pluginVersionCheck();

        countPlayerDoll();

        prepareDollSpawn();
    }

    @Override
    public void onDisable() {
        YamlConfiguration config = YAMLManager.getConfig("config");
        if (config != null && config.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(maxplayer);
        }
        if (invManager != null) {
            invManager.invManager.keySet().forEach(HumanEntity::closeInventory);
        }

        if (isFolia) dollManagerMap.values().forEach(d -> {
            ((FoliaDollImpl) d).foliaDisconnect(false);
            d.dollConfig.set("setting", d.getConfigManager().getData());
        });
        else dollManagerMap.values().forEach(AbstractDoll::disconnect);
        //YAMLManager.saveAllConfig();
        //YAMLManager.unloadAllConfig();
    }
    private void pluginVersionCheck() {
        //Bukkit.getScheduler().runTaskAsynchronously(this,() -> {
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
       // });
    }
    private boolean gameVersionCheck() {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return switch (version) {
            case "v1_20_R1" -> {
                System.out.println("Game Running in Version: 1.20");
                yield true;
            }
            case "v1_20_R2" -> {
                System.out.println("Game Running in Version: 1.20.2");
                yield true;
            }
            default -> {
                System.out.println("Unknown or Unsupported Versions, Disabling PlayerDoll.");
                getPluginLoader().disablePlugin(this);
                yield false;
            }
        };
    }
    private void getServerBranch() {
        String mod = YAMLManager.getConfig("config").getString("Global.Mod");
        if (mod == null || mod.isEmpty() || mod.isBlank()) {
            System.out.println("Cannot Get Mod from Config, Searching...");
            System.out.println("Found Server running in Mod:" +getServer().getName());
            if (isFolia()) {
                PlayerDoll.isFolia = true;
                return;
            }
            switch (getServer().getName().toLowerCase()) {
                case "craftbukkit" -> PlayerDoll.isSpigot = true;
                case "purpur", "paper" -> PlayerDoll.isPaperSeries = true;
                default -> {
                    System.out.println("Cannot detect any Mod, Disable Plugin.");
                    getPluginLoader().disablePlugin(this);
                }
            }
        } else {
            switch (YAMLManager.getConfig("config").getString("Global.Mod").toLowerCase()) {
                case "spigot" -> PlayerDoll.isSpigot = true;
                case "paperseries" -> PlayerDoll.isPaperSeries = true;
                case "folia" -> PlayerDoll.isFolia = true;
                default -> {
                    System.out.println("Cannot detect any Mod, Disable Plugin.");
                    getPluginLoader().disablePlugin(this);
                }
            }
        }

        /*
        try {
            Enumeration<URL> resources = getServer().getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                String s = manifest.getMainAttributes().getValue("Implementation-Version");
                if (s != null) {
                    serverBranch = s.split("-")[1];
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */

    }
    private void configCheck() {
        //Bukkit.getScheduler().runTaskAsynchronously(this,() -> {
            dollDirectory = PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll";
            File dollDirectory = new File(PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll");
            YAMLManager.loadConfig(dollDirectory, null, true);

            File configFile = new File(getDataFolder(), "config.yml");

            YAMLManager configYAML = YAMLManager.loadConfig(configFile, "config", true);
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
            YAMLManager languageYAML = YAMLManager.loadConfig(languageFile, "lang", true);
            if (languagePref.equalsIgnoreCase("english")) {
                languageYAML = languageYAML.createFromResource("english.yml");
            }

            File flagFile = new File(getDataFolder(), "flag.yml");
            YAMLManager flagYAML = YAMLManager.loadConfig(flagFile, "flag", true);
            flagYAML = flagYAML.createFromResource("flag.yml");
        //});
    }
    private void countPlayerDoll() {
        //Bukkit.getScheduler().runTaskAsynchronously(this,() -> {
            File[] dollFiles = new File(PlayerDoll.getDollDirectory()).listFiles();
            if (dollFiles != null) {
                for (File f : dollFiles) {
                    try {
                        Scanner scanner = new Scanner(f);
                        loop:while (scanner.hasNextLine()) {
                            String l = scanner.nextLine();
                            switch (l.toLowerCase()) {
                                case "remove: true" -> {break loop;}
                                case "owner:" -> {
                                    String s = scanner.nextLine();
                                    if (s.startsWith("UUID: ",2)) s = scanner.nextLine();
                                    if (s.startsWith("Name: ",2)) {
                                        String name = s.split(": ")[1];
                                        if (playerDollCountMap.containsKey(name)) {
                                            playerDollCountMap.put(name, playerDollCountMap.get(name) + 1);
                                        } else {
                                            playerDollCountMap.put(name, 1);
                                        }
                                    }
                                }
                                case "initial: false" -> pendingRespawnList.add(f.getName());
                            }
                        }
                        scanner.close();
                        new FileReader(f).close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        //});
    }

    public void prepareDollSpawn() {
        if (!YAMLManager.getConfig("config").getBoolean("Global.DollJoinAtStart")) return;
        pendingRespawnList.forEach(f -> {
            if (f != null) {
                File dollFile = new File(PlayerDoll.getDollDirectory(), f);
                String stripFileName = f.substring(0, f.lastIndexOf("."));
                YAMLManager yaml = YAMLManager.loadConfig(dollFile, stripFileName, false);
                if (yaml != null) {
                    var config = yaml.getConfig();
                    if (config.getBoolean("setting.Join At Start")) {
                        String name = PlayerDoll.getDollPrefix() + stripFileName;
                        AbstractDoll doll = null;
                        if (PlayerDoll.isSpigot) doll = SpigotDollImpl.staticSpawn(name);
                        if (PlayerDoll.isPaperSeries) doll = PaperDollImpl.staticSpawn(name);
                        if (PlayerDoll.isFolia) doll = FoliaDollImpl.staticSpawn(name);
                        yaml.getConfig();
                        PlayerDoll.dollManagerMap.put(stripFileName, doll);
                    }
                }
            }
        });
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
