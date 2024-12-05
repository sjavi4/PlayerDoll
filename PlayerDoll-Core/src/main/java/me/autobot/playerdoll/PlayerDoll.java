package me.autobot.playerdoll;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.autobot.playerdoll.api.*;
import me.autobot.playerdoll.api.command.CommandBuilder;
import me.autobot.playerdoll.api.config.ConfigLoader;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.connection.ConnectionFetcher;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.constant.AbsServerVersion;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.event.CommandRegisterEvent;
import me.autobot.playerdoll.api.event.SetConvertPlayerCheckProtocolEvent;
import me.autobot.playerdoll.api.event.SetDollLoginListenerEvent;
import me.autobot.playerdoll.api.inv.EventWatcher;
import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;
import me.autobot.playerdoll.api.registry.AddonRegistry;
import me.autobot.playerdoll.api.scheduler.SchedulerAPI;
import me.autobot.playerdoll.connection.DollConnection;
import me.autobot.playerdoll.listener.APIEventCall;
import me.autobot.playerdoll.listener.bukkit.*;
import me.autobot.playerdoll.listener.doll.DollJoin;
import me.autobot.playerdoll.listener.doll.DollSetting;
import me.autobot.playerdoll.loader.AddonClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerDoll extends JavaPlugin implements PlayerDollPlugin {

    private FileUtil fileUtil;
    private ConfigLoader configLoader;
    private int originalMaxPlayer;
    private AbsServerVersion serverVersion;
    private AbsServerBranch serverBranch;
    private AddonRegistry addonRegistry;
    private Connection connection;
    private CommandBuilder commandBuilder;

    private final Map<String, AddonClassLoader> addonMap = new HashMap<>();

    @Override
    public void onEnable() {
        PlayerDollAPI.setInstance(this);
        originalMaxPlayer = Bukkit.getMaxPlayers();
        fileUtil = new FileUtil();
        configLoader = new ConfigLoader();
        addonRegistry = new AddonRegistry();

        checkUpdate();

        BuiltinImpl.init();

        Bukkit.getPluginManager().registerEvents(new APIEventCall(), this);


        commandBuilder = new CommandBuilder();
        // Load addon (other separate plugin)
        // Load self events

        loadAddon();
        addonRegistry.addons.values().forEach(Addon::onEnable);


        initServerBranch();
        initServerVersion();

        Bukkit.getPluginManager().callEvent(new CommandRegisterEvent(commandBuilder.getRoot()));
        //CommandBuilder.commandImpl.add(new BuiltinCommandBuilder());

        registerEventHandlers();


        // RunTask for plugin that is not inside addon
        PlayerDollAPI.getScheduler().globalTaskDelayed(this::postEnable, 1L);
        PlayerDollAPI.getScheduler().globalTaskDelayed(this::prepareDollSpawn, 5L);
    }

    private void postEnable() {
        PlayerDollAPI.setConnection(this.connection = new DollConnection());

        SetDollLoginListenerEvent dollLoginListenerEvent = new SetDollLoginListenerEvent();
        Bukkit.getPluginManager().callEvent(dollLoginListenerEvent);
        ConnectionFetcher.setDollCustomLoginListener(dollLoginListenerEvent.getConstructor());

        SetConvertPlayerCheckProtocolEvent checkProtocolEvent = new SetConvertPlayerCheckProtocolEvent();
        Bukkit.getPluginManager().callEvent(checkProtocolEvent);
        AsyncPlayerPreLogin.checkProtocol = checkProtocolEvent.getCheckProtocol();
    }

    @Override
    public void onDisable() {
        // Discard everything

        // Discard addon
        addonRegistry.addons.values().forEach(Addon::onDisable);
        addonMap.values().forEach(AddonClassLoader::unloadJarFiles);


        BasicConfig config = PlayerDollAPI.getConfigLoader().getBasicConfig();
        if (serverBranch != AbsServerBranch.FOLIA) {
            // folia should not have Server Reload
            String kickReason = config.broadcastConvertShutdown.getValue() ? "(ConvertPlayer) Server Closed" : null;

            DollStorage.ONLINE_TRANSFORMS.values().forEach(extendPlayer -> extendPlayer.getBukkitPlayer().kickPlayer(kickReason));
        }

        if (config.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(getOriginalMaxPlayer());
        }
        // Folia don't call playerQuitEvent when shutdown
        //if (serverBranch == ServerBranch.FOLIA) {
        DollConfig.DOLL_CONFIGS.values().forEach(DollConfig::saveConfig);
        //}

        DollStorage.ONLINE_DOLLS.values().forEach(Doll::dollDisconnect);

        if (connection != null) {
            connection.shutDown();
        }

        PlayerDollAPI.removeInstance();
    }

    private void initServerBranch() {
        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();
        String configMod = basicConfig.serverMod.getValue();
        if (configMod.isEmpty() || configMod.isBlank()) {
            getLogger().info("Config did not assign Server Branch. Detecting...");
            serverBranch = AbsServerBranch.parse();
            getLogger().info("Server is running on ".concat(serverBranch.registerName()));
        } else {
            serverBranch = AbsServerBranch.parse(configMod);
            getLogger().info("(Config Assigned) Server is running on ".concat(serverBranch.registerName()));
        }
        if (serverBranch == null) {
            getLogger().severe("Server Mod cannot be detected or not assigned from Config, Disabling Plugin");
            getLogger().severe("Please check config.yml -> server-mod");
            getPluginLoader().disablePlugin(this);
        }
        if (ReflectionUtil.hasClass(serverBranch.getPath())) {
            Class<?> schedulerClass = ReflectionUtil.getClass(serverBranch.getPath());
            PlayerDollAPI.setScheduler(ReflectionUtil.newInstance(SchedulerAPI.class, schedulerClass.getDeclaredConstructors()[0]));
        } else if (ReflectionUtil.hasAddonClass(serverBranch.getPath(), serverBranch.getAddon())) {
            Class<?> schedulerClass = ReflectionUtil.getAddonClass(serverBranch.getPath(), serverBranch.getAddon());
            PlayerDollAPI.setScheduler(ReflectionUtil.newInstance(SchedulerAPI.class, schedulerClass.getDeclaredConstructors()[0]));
        } else {
            getLogger().severe("Cannot get/create Scheduler Class from ServerBranch!");
            getLogger().warning("Please check your ServerBranch implementation");
            getPluginLoader().disablePlugin(this);
        }
    }


    private void initServerVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
        serverVersion = AbsServerVersion.parse(bukkitVersion).orElseThrow();
        getLogger().log(Level.INFO ,"Detected Server Version [{0} ({1})]", new Object[]{bukkitVersion, serverVersion.registerVersion()});
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

        pluginManager.registerEvents(commandBuilder, this);

        EventWatcher.init();
        pluginManager.registerEvents(EventWatcher.getInstance(), this);

        // Paper Event
//        if (serverBranch == ServerBranch.PAPER || serverBranch == ServerBranch.FOLIA) {
//            ReflectionUtil.getPluginClass("listener.paper.PlayerConnectionClose");
//        }


        // Custom Event
        pluginManager.registerEvents(new DollJoin(), this);
        pluginManager.registerEvents(new DollSetting(), this);
    }

    @Override
    public FileUtil getFileUtil() {
        return fileUtil;
    }

    @Override
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    @Override
    public int getOriginalMaxPlayer() {
        return originalMaxPlayer;
    }

    @Override
    public AbsServerVersion getServerVersion() {
        return serverVersion;
    }

    @Override
    public AbsServerBranch getServerBranch() {
        return serverBranch;
    }

    @Override
    public AddonRegistry getAddonRegistry() {
        return addonRegistry;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public CommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    private void checkUpdate() {
//        getLogger().info("You are using Dev version of PlayerDoll");
//        getLogger().info("Please report Bugs and Suggestions on https://github.com/sjavi4/PlayerDoll/issues");
        

        if (!configLoader.getBasicConfig().checkUpdate.getValue()) {
            return;
        }
        new Thread(() -> {
            getLogger().info("Checking New Versions...");
            try (InputStream release = new URL("https://raw.githubusercontent.com/sjavi4/PlayerDoll/main/ver.txt").openStream()) {
                final String remoteVersion = new String(release.readAllBytes(), StandardCharsets.UTF_8).replaceAll("\\r?\\n","");

                if (!remoteVersion.equalsIgnoreCase(this.getDescription().getVersion())) {
                    getLogger().log(Level.INFO, "New version available: {0} (current: {1})", new String[]{remoteVersion, this.getDescription().getVersion()});
                    getLogger().info("Visit https://modrinth.com/plugin/playerdoll/versions#all-versions to download the latest version");
                } else {
                    getLogger().info("You are running the latest version");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void prepareDollSpawn() {
        Connection dollConnection = getConnection();
        if (dollConnection == null) {
            getLogger().warning("Missing Doll Connection Instance. Cannot Spawn Doll.");
            return;
        }

        FileUtil fileUtil = getFileUtil();
        File[] dollConfigs = fileUtil.getDollDir().toFile().listFiles((file, name) -> name.endsWith(".yml"));
        if (dollConfigs == null) {
            return;
        }
        BasicConfig basicConfig = configLoader.getBasicConfig();
        String dollIdentifier = basicConfig.dollIdentifier.getValue();
        long delayOfEach = basicConfig.autoJoinDelay.getValue();
        int index = 1;
        for (File dollFile : dollConfigs) {
            String fileName = dollFile.getName();
            DollConfig config = DollConfig.getTemporaryConfig(fileName.substring(0, fileName.length() - ".yml".length()));
            // count Creation
            UUID ownerUUID = UUID.fromString(config.ownerUUID.getValue());
            DollStorage.PLAYER_CREATION_COUNTS.merge(ownerUUID, 1, Integer::sum);
            // auto Join
            if (config.dollSetting.get(GlobalFlagButton.JOIN_AT_START).getValue()) {
                GameProfile profile = new GameProfile(UUID.fromString(config.dollUUID.getValue()), dollIdentifier.concat(config.dollName.getValue()));
                profile.getProperties().clear();
                profile.getProperties().put("textures", new Property("textures", config.skinProperty.getValue(), config.skinSignature.getValue()));
                Runnable r = () -> connection.connect(profile, null);
                PlayerDollAPI.getScheduler().globalTaskDelayed(r, delayOfEach * 20 * index);
                index++;
            }
        }
    }


    public void loadAddon() {
        //this.addonMap.clear();

        File[] addonFiles = getJarFiles(fileUtil.getAddonDir().toFile());
        if (addonFiles == null) {
            return;
        }
        List<URL> urlList = new ArrayList<>();
        try {
            for (File file : addonFiles) {
                urlList.add(file.toURI().toURL());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Pattern pattern = Pattern.compile("addon/(.*)\\.jar");
        String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];

        for (URL url : urlList) {
            AddonClassLoader loader = new AddonClassLoader();
            String addonName;
            String jarName = url.getFile();

            Matcher matcher = pattern.matcher(jarName);

            if (matcher.find()) {
                addonName = matcher.group(1);
            } else {
                getLogger().warning("Failed to Parse addon Name. Skipping");
                continue;
            }
            getLogger().log(Level.INFO, "Loading Addon [{0}].", addonName);

            loader.addURLFile(url);
            addLoader(addonName, loader);


            try (JarFile jar = new JarFile(addonFiles[urlList.indexOf(url)])) {
                JarEntry addon = jar.getJarEntry("addon.yml");
                if (addon == null) {
                    getLogger().warning("addon.yml does not exist. Skipping");
                    unloadAddon(addonName);
                    continue;
                }
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(jar.getInputStream(addon)));
                String mainClass = yamlConfiguration.getString("main");
                if (mainClass == null) {
                    getLogger().warning("addon.yml does not contain 'main'. Skipping");
                    unloadAddon(addonName);
                    continue;
                }
                List<String> supportVersions = yamlConfiguration.getStringList("versions");
                if (!supportVersions.contains("all")) {
                    if (!supportVersions.contains(bukkitVersion)) {
                        getLogger().warning("'versions' not match with Server. Skipping");
                        unloadAddon(addonName);
                        continue;
                    }
                }
                Class<?> main;
                try {
                    main = Class.forName(mainClass, true, loader);
                } catch (ClassNotFoundException e) {
                    getLogger().warning("Main class not found. Skipping");
                    unloadAddon(addonName);
                    continue;
                }
                addonRegistry.register(jarName.concat(":").concat(main.getPackageName()), ReflectionUtil.newInstance(Addon.class, main.getDeclaredConstructors()[0]));
            } catch (IOException e) {
                getLogger().warning("Cannot process Jar file. Skipping");
                unloadAddon(addonName);
                continue;
            }
            getLogger().log(Level.INFO, "Addon [{0}] Loaded.", addonName);
        }
    }
    public void unloadAddon(String pluginName){
        this.addonMap.get(pluginName).unloadJarFiles();
        this.addonMap.remove(pluginName);
    }

    private void addLoader(String pluginName, AddonClassLoader loader){
        this.addonMap.put(pluginName, loader);
    }

    private File[] getJarFiles(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            return dir.listFiles((d, name) -> name.endsWith(".jar"));
        } else {
            getLogger().warning("No Addon Found. Some functionalities might be affected.");
        }
        return null;
    }
}
