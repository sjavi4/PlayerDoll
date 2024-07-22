package me.autobot.playerdoll;

import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.GUIManager;
import me.autobot.playerdoll.gui.MenuWatcher;
import me.autobot.playerdoll.listener.bukkit.*;
import me.autobot.playerdoll.listener.doll.DollJoin;
import me.autobot.playerdoll.listener.doll.DollRespawn;
import me.autobot.playerdoll.listener.doll.DollSetting;
import me.autobot.playerdoll.scheduler.BukkitScheduler;
import me.autobot.playerdoll.scheduler.FoliaScheduler;
import me.autobot.playerdoll.scheduler.Scheduler;
import me.autobot.playerdoll.socket.SocketHelper;
import me.autobot.playerdoll.util.ConfigLoader;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerDoll extends JavaPlugin {
    public static final boolean isDev = false;
    public static PlayerDoll PLUGIN;
    public static Logger LOGGER;
    public static String SERVER_VERSION;
    public static String INTERNAL_VERSION;
    public static ServerBranch serverBranch;
    public static boolean BUNGEECORD;
    public static Scheduler scheduler;

    // SPIGOT < PAPER < FOLIA
    public enum ServerBranch {
        SPIGOT {
            @Override
            void setupScheduler(Plugin plugin) {
                scheduler = new BukkitScheduler(plugin);
            }
        }, PAPER {
            @Override
            void setupScheduler(Plugin plugin) {
                scheduler = new BukkitScheduler(plugin);
            }
        }, FOLIA {
            @Override
            void setupScheduler(Plugin plugin) {
                scheduler = new FoliaScheduler(plugin);
            }
        };

        abstract void setupScheduler(Plugin plugin);
    }

    public static void callSyncEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
    public static void sendServerCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }
    public static void sendBungeeCordMessage(byte[] b) {
        //scheduler.globalTask(() -> {
            Bukkit.getServer().sendPluginMessage(PLUGIN, "playerdoll:doll", b);
        //});
    }

    private BasicConfig basicConfig;
    private int maxPlayer;
    @Override
    public void onEnable() {
        // Plugin startup logic

        PLUGIN = this;
        LOGGER = getLogger();
        new FileUtil(this);
        new ConfigLoader(this);

        basicConfig = BasicConfig.get();
        maxPlayer = Bukkit.getMaxPlayers();
        //ConfigLoader.CUSTOM_LANGUAGE_NAME = basicConfig.pluginLanguage.getValue();

        initServerVersion();
        ReflectionUtil.gameVersion = INTERNAL_VERSION;
        initServerBranch();

        checkBungeeCord();


        registerEventHandlers();
        registerCommands();

        checkUpdate();

//        if (basicConfig.convertPlayer.getValue()) {
//            convertConnection = new ConvertPlayerConnection();
//            convertConnection.start();
//        }

        // in BungeeCord mode, messaging require 1 player in Game
        // Trigger this in PlayerJoin
        if (!BUNGEECORD) {
            scheduler.globalTaskDelayed(() -> prepareDollSpawn(0), 5);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        if (basicConfig.convertPlayer.getValue()) {
//            convertConnection.interrupt();
//        }
        if (serverBranch != ServerBranch.FOLIA) {
            // folia should not have Server Reload
            String kickReason = basicConfig.broadcastConvertShutdown.getValue() ? "(ConvertPlayer) Server Closed" : null;

            DollManager.ONLINE_PLAYERS.values().forEach(extendPlayer -> {
                extendPlayer.getBukkitPlayer().kickPlayer(kickReason);
            });
        }

        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(maxPlayer);
        }
        // Folia don't call playerQuitEvent when shutdown
        //if (serverBranch == ServerBranch.FOLIA) {
            DollConfig.DOLL_CONFIGS.values().forEach(DollConfig::saveConfig);
        //}

        DollManager.ONLINE_DOLLS.values().forEach(Doll::dollDisconnect);

        if (BUNGEECORD) {
            getServer().getMessenger().unregisterOutgoingPluginChannel(this,"playerdoll:doll");
            getServer().getMessenger().unregisterIncomingPluginChannel(this, "playerdoll:doll");
        }
    }

    private void registerEventHandlers() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        // Bukkit Event
        pluginManager.registerEvents(new PlayerCommandSend(), this);
        pluginManager.registerEvents(new PlayerInteractAtEntity(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PlayerRecipeDiscover(), this);
        pluginManager.registerEvents(new PlayerDisconnect(), this);
        pluginManager.registerEvents(new PlayerDeath(), this);
        pluginManager.registerEvents(new AsyncPlayerPreLogin(), this);
        pluginManager.registerEvents(new ServerListPing(), this);

        // Event for Register command
        pluginManager.registerEvents(new ServerLoad(), this);

        pluginManager.registerEvents(new MenuWatcher(new GUIManager()), this);
        // Paper Event
//        if (serverBranch == ServerBranch.PAPER || serverBranch == ServerBranch.FOLIA) {
//            ReflectionUtil.getPluginClass("listener.paper.PlayerConnectionClose");
//        }


        // Custom Event
        pluginManager.registerEvents(new DollJoin(), this);
        pluginManager.registerEvents(new DollSetting(), this);
        pluginManager.registerEvents(new DollRespawn(), this);
    }

    private void registerCommands() {
    }

    private void initServerVersion() {
        SERVER_VERSION = Bukkit.getBukkitVersion().split("-")[0];
        switch (SERVER_VERSION) {
            case "1.20.2" -> INTERNAL_VERSION = "v1_20_R2";
            case "1.20.3", "1.20.4" -> INTERNAL_VERSION = "v1_20_R3";
            case "1.20.5", "1.20.6" -> INTERNAL_VERSION = "v1_20_R4";
            case "1.21" -> INTERNAL_VERSION = "v1_21_R1";
            default -> LOGGER.severe("Unknown or Unsupported Versions, Please Use with Cautions.");
        }
    }
    private void initServerBranch() {
        final String configMod = basicConfig.serverMod.getValue();
        if (configMod.isEmpty() || configMod.isBlank()) {
            LOGGER.info("Config did not assign Server Mod. Detecting...");
            if (ReflectionUtil.hasClass("io.papermc.paper.threadedregions.RegionizedServer")) {
                serverBranch = ServerBranch.FOLIA;
                ReflectionUtil.initialFoliaRegionizedServer();
            } else if (ReflectionUtil.hasClass("com.destroystokyo.paper.PaperConfig") || ReflectionUtil.hasClass("io.papermc.paper.configuration.Configuration")) {
                serverBranch = ServerBranch.PAPER;
            } else if (ReflectionUtil.hasClass("org.spigotmc.SpigotConfig")) {
                serverBranch = ServerBranch.SPIGOT;
            }
            LOGGER.info("Server is running on " + serverBranch);
        } else {
            switch (configMod.toLowerCase()) {
                case "spigot" -> {
                    serverBranch = ServerBranch.SPIGOT;
                }
                case "paperseries" -> {
                    serverBranch = ServerBranch.PAPER;
                }
                case "folia" -> {
                    serverBranch = ServerBranch.FOLIA;
                }
                default -> {
                    LOGGER.severe("Unknown Mod in Config, Disable Plugin");
                    getPluginLoader().disablePlugin(this);
                }
            }
        }
        serverBranch.setupScheduler(this);
    }
    private void checkBungeeCord() {
        BUNGEECORD = getServer().spigot().getConfig().getBoolean("settings.bungeecord");
        if (BUNGEECORD) {
            LOGGER.info("Server Is on BungeeCord Mode");
            LOGGER.warning("Doll Auto Rejoin can ONLY be triggered after the First Player has Joined");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "playerdoll:doll");
            getServer().getMessenger().registerIncomingPluginChannel(this, "playerdoll:doll", new PluginMessenger());
        }
    }
    private void checkUpdate() {
        if (isDev) {
            LOGGER.info("You are using a Dev Branch of PlayerDoll");
            LOGGER.info("If any error was printed");
            LOGGER.info("Welcome to report Bugs and Suggestions on https://github.com/sjavi4/PlayerDoll/issues");
        }

        if (!basicConfig.checkUpdate.getValue()) {
            return;
        }
        new Thread(() -> {
            LOGGER.info("Checking New Versions...");
            try (InputStream release = new URL("https://raw.githubusercontent.com/sjavi4/PlayerDoll/main/ver.txt").openStream()) {
                final String remoteVersion = new String(release.readAllBytes(), StandardCharsets.UTF_8).replaceAll("\\r?\\n","");

                if (!remoteVersion.equalsIgnoreCase(this.getDescription().getVersion())) {
                    LOGGER.log(Level.INFO, "New version available: {0} (current: {1})", new String[]{remoteVersion, this.getDescription().getVersion()});
                    LOGGER.info("Visit https://modrinth.com/plugin/playerdoll/versions#all-versions to download the latest version");
                } else {
                    LOGGER.info("You are running the latest version");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void prepareDollSpawn(long delayOfEach) {
        FileUtil fileUtil = FileUtil.INSTANCE;
        File[] dollConfigs = fileUtil.getDollDir().toFile().listFiles((file, name) -> name.endsWith(".yml"));
        if (dollConfigs == null) {
            return;
        }
        int index = 1;
        for (File dollFile : dollConfigs) {
            String fileName = dollFile.getName();
            DollConfig config = DollConfig.getTemporaryConfig(fileName.substring(0, fileName.length() - ".yml".length()));
            // count Creation
            UUID ownerUUID = UUID.fromString(config.ownerUUID.getValue());
            DollManager.PLAYER_CREATION_COUNTS.merge(ownerUUID, 1, Integer::sum);
            // auto Join
            if (config.dollSetting.get(FlagConfig.GlobalFlagType.JOIN_AT_START).getValue()) {
                if (BUNGEECORD && config.dollLastJoinServer.getValue().isEmpty()) {
                    continue;
                }
                Runnable r = () -> SocketHelper.createConnection(config.dollName.getValue(), UUID.fromString(config.dollUUID.getValue()), null);
                scheduler.globalTaskDelayed(r, delayOfEach * 20 * index);
                index++;
            }
        }
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }
}
