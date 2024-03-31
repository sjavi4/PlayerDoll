package me.autobot.playerdoll;

import me.autobot.playerdoll.Command.CommandHelper;
import me.autobot.playerdoll.Command.Utils.CommandHelp;
import me.autobot.playerdoll.Command.Utils.CommandLimit;
import me.autobot.playerdoll.Command.Utils.CommandList;
import me.autobot.playerdoll.Command.Utils.CommandReload;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.DollHelper;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.EventListener.*;
import me.autobot.playerdoll.folia.FoliaHelper;
import me.autobot.playerdoll.GUIs.Doll.DollInvStorage;
import me.autobot.playerdoll.GUIs.GUIEventListener;
import me.autobot.playerdoll.GUIs.GUIManager;
import me.autobot.playerdoll.Util.BackupHelper;
import me.autobot.playerdoll.Util.ConfigLoader;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import me.autobot.playerdoll.Util.Configs.FlagConfig;
import me.autobot.playerdoll.Util.Configs.PermConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerDoll extends JavaPlugin {
    private static Plugin plugin;
    private static Logger logger;

    public static String version;
    public static final Map<String, DollInvStorage> dollInvStorage = new HashMap<>();
    public static final Set<DollConfig> pendingRespawnList = new HashSet<>();
    private static int maxPlayer;
    public static final String dollIdentifier = "-";
    private static String dollDirectory = "";

    private static FoliaHelper foliaHelper = null;


    public static Plugin getPlugin() {
        return plugin;
    }
    public static String getDollDirectory() {
        return dollDirectory;
    }
    private static ConfigLoader configLoader;
    public static boolean isSpigot = false;
    public static boolean isPaperSeries = false;
    public static boolean isFolia = false;
    public static FoliaHelper getFoliaHelper() {
        return foliaHelper;
    }
    public static Logger getPluginLogger() {
        return logger;
    }
    private BasicConfig basicConfig;
    public static boolean useBungeeCord = false;
    @Override
    public void onEnable() {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        gameVersionCheck();

        plugin = this;
        logger = getLogger();
        maxPlayer = Bukkit.getMaxPlayers();

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        File dollDirectoryFolder = new File(plugin.getDataFolder() + File.separator + "doll");
        if (!dollDirectoryFolder.exists()) dollDirectoryFolder.mkdirs();
        File backupDirectory = new File(plugin.getDataFolder() + File.separator + "backup");
        if (!backupDirectory.exists()) backupDirectory.mkdirs();


        configLoader = new ConfigLoader(this);
        //configManager = new ConfigManager(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        dollDirectory = PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll";

        basicConfig = BasicConfig.get();
        FlagConfig.get();
        PermConfig.get();
        //if (basicConfig.)
        getServerBranch();

        initialGUI(pluginManager);

        registerCommand();

        registerEvent(pluginManager);

        pluginVersionCheck();

        if (basicConfig.backupStartUp.getValue()) {
            BackupHelper.zip(this.getDataFolder(), new File(dollDirectory));
        }
        /*
        if (ConfigManager.getConfig().getBoolean("Global.Backup.StartUp")) {
            BackupHelper.zip(this.getDataFolder(), new File(dollDirectory));
        }

         */

        thridPartyCheck();
        //if (!luckPermsHelper.exist()) {
        //} else {
        //PermissionManager.newInstance(this);
        //}
        //PermissionManager.initialize(this, luckPermsHelper != null);
        useBungeeCord = getServer().spigot().getConfig().getBoolean("settings.bungeecord");

        if (useBungeeCord) {
            //getServer().getMessenger().registerOutgoingPluginChannel(this, "playerdoll:player");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "playerdoll:doll");
            getServer().getMessenger().registerIncomingPluginChannel(this, "playerdoll:doll", new Messenger());
        }

        try {
            countPlayerDoll();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (isFolia) {
            getFoliaHelper().globalTaskDelayed(this::prepareDollSpawn,5);
        } else {
            Bukkit.getScheduler().runTaskLater(this, this::prepareDollSpawn,5);
        }

    }

    @Override
    public void onDisable() {
        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(maxPlayer);
        }
        /*
        YamlConfiguration config = ConfigManager.getConfig();
        if (config != null && config.getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(maxplayer);
        }

         */
        dollInvStorage.values().forEach(DollInvStorage::closeAllInv);
        if (isFolia) {
            DollManager.ONLINE_DOLL_MAP.values().forEach((DollManager::Folia_Disconnect));
        } else {
            DollManager.ONLINE_DOLL_MAP.values().forEach(IDoll::_disconnect);
        }
        //PermissionManager.save();

        /*
        DollConfigManager.dollConfigManagerMap.values().forEach(d -> {
            if (d != null) {
                d.save();
                d.removeListener();
            }
        });

         */
        if (basicConfig.backupShutDown.getValue()) {
            BackupHelper.zip(this.getDataFolder(), new File(dollDirectory));
        }
        /*
        if (ConfigManager.getConfig().getBoolean("Global.Backup.ShutDown")) {
            BackupHelper.zip(this.getDataFolder(), new File(dollDirectory));
        }
        configManager.stopWatcher();

         */
        if (useBungeeCord) {
            //getServer().getMessenger().unregisterOutgoingPluginChannel(this,"playerdoll:player");
            getServer().getMessenger().unregisterOutgoingPluginChannel(this,"playerdoll:doll");
            getServer().getMessenger().unregisterIncomingPluginChannel(this, "playerdoll:doll");
        }
    }

    public static ConfigLoader getConfigLoader() {
        return configLoader;
    }

    private void registerCommand() {
        getCommand("doll").setExecutor(new CommandHelper());
        getCommand("doll").setTabCompleter(new CommandHelper());

        getCommand("dollReload").setExecutor(new CommandReload());
        getCommand("dollLimit").setExecutor(new CommandLimit());

        CommandHelp help = new CommandHelp();
        CommandList list = new CommandList();

        getCommand("dollHelp").setExecutor(help);
        getCommand("dollHelp").setTabCompleter(help);

        getCommand("dollList").setExecutor(list);
        getCommand("dollList").setTabCompleter(list);
        //getCommand("dollUpgrade").setExecutor(new CommandUpgrade());
    }
    private void registerEvent(PluginManager pluginManager) {
        pluginManager.registerEvents(new DollDisconnectEvent(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new DollJoin(), this);
        pluginManager.registerEvents(new PlayerInteractDollEvent(), this);
        pluginManager.registerEvents(new DollDieEvent(), this);
        pluginManager.registerEvents(new DollRecipeEvent(), this);
        //does not work as good as nms.
        //pluginManager.registerEvents(new DollTargetEvent(),this);
        //pluginManager.registerEvents(new DollKickEvent(), this);
        pluginManager.registerEvents(new DollDamageEvent(), this);
        pluginManager.registerEvents(new AsyncPreLoginEvent(), this);
        if (!PlayerDoll.isSpigot) {
            //pluginManager.registerEvents(new HandshakeEvent(), this);
        }
        DollHelper.registerDollEvent(version);
    }
    private void initialGUI(PluginManager pluginManager) {
        GUIEventListener guiEventListener = new GUIEventListener(new GUIManager());
        pluginManager.registerEvents(guiEventListener, this);
    }
    private void thridPartyCheck() {
        /*
        vaultHelper = new VaultHelper();
        try {
            luckPermsHelper = new LuckPermsHelper();
        } catch (NoClassDefFoundError ignored) {
        }

         */

    }
    private void pluginVersionCheck() {
        if (!basicConfig.checkUpdate.getValue()) {
            return;
        }
        /*
        if (!ConfigManager.getConfig().getBoolean("Global.CheckUpdate")) {
            return;
        }

         */
        new Thread(()->{
            try {
                logger.log(Level.INFO,"Checking New Versions...");
                InputStream release = new URL("https://raw.githubusercontent.com/sjavi4/PlayerDoll/main/ver.txt").openStream();
                String ver = new String(release.readAllBytes(),StandardCharsets.UTF_8).replaceAll("\\r?\\n","");
                if (!ver.equalsIgnoreCase(this.getDescription().getVersion())) {
                    logger.log(Level.INFO, "New version available: " + ver + "(current:"+this.getDescription().getVersion()+")");
                    logger.log(Level.INFO,"Visit https://modrinth.com/plugin/playerdoll/versions#all-versions to download the latest version.");
                } else {
                    logger.log(Level.INFO,"You are running the latest version.");
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
        switch (version) {
            case "v1_20_R3", "v1_20_R4" -> {}
            default -> logger.log(Level.WARNING, "Unknown or Unsupported Versions, Please Use with Cautions.");
            //getPluginLoader().disablePlugin(this);
            //yield false;
        };
    }
    private void getServerBranch() {
        String mod = basicConfig.serverMod.getValue();
        /*
        String mod = ConfigManager.getConfig().getString("Global.Mod");

         */
        if (mod == null || mod.isBlank()) {

            logger.log(Level.INFO, "Auto-detecting Server Mod:" +getServer().getName());
            if (getClass("io.papermc.paper.threadedregions.RegionizedServer")) {
                isFolia = true;
                foliaHelper = new FoliaHelper(this);
            } else if (getClass("com.destroystokyo.paper.PaperConfig") || getClass("io.papermc.paper.configuration.Configuration")) {
                isPaperSeries = true;
            } else if (getClass(("org.spigotmc.SpigotConfig"))) {
                isSpigot = true;
            }
        } else {
            switch (mod.toLowerCase()) {
                case "spigot" -> PlayerDoll.isSpigot = true;
                case "paperseries" -> PlayerDoll.isPaperSeries = true;
                case "folia" -> PlayerDoll.isFolia = true;
                default -> {
                    logger.log(Level.WARNING,"Unknown mod, Disabled Plugin.");
                    getPluginLoader().disablePlugin(this);
                }
            }
        }
    }
    public void prepareDollSpawn() {
        for (DollConfig dollConfig : pendingRespawnList) {
            if (!dollConfig.dollJoinAtStart.getValue()) {
                continue;
            }
            UUID dollUUID = UUID.fromString(dollConfig.dollUUID.getValue());
            DollHelper.callSpawn(null, dollConfig.dollName.getValue(), dollUUID , version);
        }
        pendingRespawnList.clear();

    }
    private boolean getClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
    private void countPlayerDoll() throws ParseException {
        File[] dollFiles = new File(plugin.getDataFolder() + File.separator + "doll").listFiles();
        if (dollFiles == null) return;
        for (File files : dollFiles) {
            String dollName = files.getName().substring(0,files.getName().lastIndexOf("."));
            DollConfig dollConfig = DollConfig.getOfflineDollConfig(dollName);
            String dollUUIDS = dollConfig.dollUUID.getValue();
            UUID dollUUID = UUID.fromString(dollUUIDS);
            if (dollUUIDS.equals(DollConfig.NULL_UUID)) {
                getLogger().log(Level.SEVERE, "Doll ["+ dollName +"] has missing UUID!");
                continue;
            }
            if (basicConfig.removeInactiveDoll.getValue()) {
                String lastSpawn = dollConfig.lastSpawnTimeStamp.getValue();
                Date lastSpawnDate = DollConfig.dateFormat.parse(lastSpawn);
                LocalDate lastSpawnTime = lastSpawnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentTime = LocalDate.now();
                long dateDifference = ChronoUnit.DAYS.between(lastSpawnTime,currentTime);
                if (dateDifference > getConfig().getLong("Global.Remove.Time")) {
                    String uuid = dollConfig.dollUUID.getValue();
                    final File configFile = files;
                    File dat = new File(Bukkit.getServer().getWorldContainer() + File.separator + "world" + File.separator + "playerdata" + File.separator + uuid + ".dat");
                    File dat_old = new File(Bukkit.getServer().getWorldContainer() + File.separator + "world" + File.separator + "playerdata" + File.separator + uuid + ".dat_old");
                    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        configFile.delete();
                        dat.delete();
                        dat_old.delete();
                    }, 1, TimeUnit.SECONDS);
                    continue;
                }
            }
            int count = DollManager.PLAYER_DOLL_COUNT_MAP.getOrDefault(dollUUID,0);
            DollManager.PLAYER_DOLL_COUNT_MAP.put(dollUUID,count+1);
            PlayerDoll.pendingRespawnList.add(dollConfig);
        }
    }
}
