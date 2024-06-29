package me.autobot.playerdoll.doll.config;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.AbstractConfig;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.configkey.ConfigKey;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DollConfig extends AbstractConfig {
    private static final FileUtil FILE_UTIL = FileUtil.INSTANCE;
    public static final Map<UUID,DollConfig> DOLL_CONFIGS = new ConcurrentHashMap<>();
    //public static final Map<String,DollConfig> OFFLINE_DOLL_CONFIGS = new HashMap<>();
    public static final String NULL_UUID = "00000000-0000-0000-0000-000000000000";
    public BaseEntity doll;
    public String name;
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    private String today;
    public File dollFile;
    public ConfigKey<DollConfig,String> creationTimeStamp;
    //public String creationTimeStamp;
    public ConfigKey<DollConfig,String> lastSpawnTimeStamp;
    public ConfigKey<DollConfig,String> dollName;
    public ConfigKey<DollConfig,String> dollUUID;
    public ConfigKey<DollConfig,String> ownerName;
    public ConfigKey<DollConfig,String> ownerUUID;
    //public final String ownerPermGroup;
    //public final String skin;
//    public ConfigKey<DollConfig,String> skinName;
//    public ConfigKey<DollConfig,String> skinImageEncoded;
//    public ConfigKey<DollConfig,String> skinCape;
//    public ConfigKey<DollConfig,String> skinModel;
//    public ConfigKey<DollConfig,String> skinSignature;
//    public ConfigKey<DollConfig,String> skinProfileID;
//    public ConfigKey<DollConfig,String> skinTimestamp;

    public ConfigKey<DollConfig, String> skinProperty;
    public ConfigKey<DollConfig, String> skinSignature;
    // cached these 4
    public ConfigKey<DollConfig,Boolean> dollHostility;
    public ConfigKey<DollConfig,Boolean> dollPhantom;
    public ConfigKey<DollConfig,Boolean> dollRealPlayerTickUpdate;
    public ConfigKey<DollConfig,Boolean> dollRealPlayerTickAction;
    // For BungeeCord record (when Auto-re-join)
    public ConfigKey<DollConfig,String> dollLastJoinServer;

    public Map<FlagConfig.GlobalFlagType, ConfigKey<DollConfig,Boolean>> dollSetting = new EnumMap<>(FlagConfig.GlobalFlagType.class);
    public Map<FlagConfig.PersonalFlagType, Boolean> generalSetting = new EnumMap<>(FlagConfig.PersonalFlagType.class);
    public Map<UUID, EnumMap<FlagConfig.PersonalFlagType, Boolean>> playerSetting = new LinkedHashMap<>();

    public static DollConfig createNewConfig(File dollConfigFile) {
        if (!dollConfigFile.exists()) {
            return null;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(dollConfigFile);
        return new DollConfig(dollConfigFile, configuration);
    }

    public static DollConfig getTemporaryConfig(String name) {
        String fullName = DollManager.dollFullName(name);
        Optional<DollConfig> existConfig = DOLL_CONFIGS.values().stream().filter(config -> config.dollName.getValue().equals(name)).findAny();
        if (existConfig.isPresent()) {
            return existConfig.get();
        }
        FileUtil fileUtil = FileUtil.INSTANCE;
        File dollFile = fileUtil.getFile(fileUtil.getDollDir(), name + ".yml");
        return new DollConfig(fullName, dollFile, YamlConfiguration.loadConfiguration(dollFile));
    }
    public static DollConfig getDollConfigForOnline(Doll doll, String dollName, UUID dollUUID) {
        String fullName = DollManager.dollFullName(dollName);
        if (DOLL_CONFIGS.containsKey(dollUUID)) {
            DOLL_CONFIGS.get(dollUUID).saveConfig();
            DOLL_CONFIGS.remove(dollUUID);
        }
        // Doll is not online yet
        File file = FILE_UTIL.getOrCreateFile(FILE_UTIL.getDollDir(), fullName + ".yml");
        return new DollConfig(doll, YamlConfiguration.loadConfiguration(file));
    }

    public static DollConfig getOnlineDollConfig(UUID dollUUID) {
        if (DOLL_CONFIGS.containsKey(dollUUID)) {
            return DOLL_CONFIGS.get(dollUUID);
        }
        Doll doll = DollManager.ONLINE_DOLLS.get(dollUUID);
        File file = FILE_UTIL.getOrCreateFile(FILE_UTIL.getDollDir(), doll.getBukkitPlayer().getName() + ".yml");
        //File file = new File(PlayerDoll.getDollDirectory(), doll.getBukkitPlayer().getName()+".yml");
        return new DollConfig(doll, YamlConfiguration.loadConfiguration(file));
    }

    private DollConfig(File dollFile, YamlConfiguration config) {
        super(config);
        this.doll = null;
        this.name = dollFile.getName().substring(0, dollFile.getName().length() - ".yml".length());
        //System.out.println(name);
        this.dollFile = dollFile;
        getData();
        //OFFLINE_DOLL_CONFIGS.put(name,this);
        //Bukkit.getPluginManager().callEvent(new DollConfigLoadEvent(this.doll,this));
        //saveConfig();
    }
//    private DollConfig(String dollName, File dollFile, YamlConfiguration config) {
//        super(config);
//        this.doll = null;
//        this.name = dollName;
//        this.dollFile = dollFile;
//        getData();
//        OFFLINE_DOLL_CONFIGS.put(name,this);
//        //Bukkit.getPluginManager().callEvent(new DollConfigLoadEvent(this.doll,this));
//        //saveConfig();
//    }
    private DollConfig(String dollName, File dollFile , YamlConfiguration config) {
        super(config);
        this.dollFile = dollFile;
        this.dollName = new ConfigKey<> (this,"Doll-Name", dollName);
        this.dollUUID = new ConfigKey<> (this,"Doll-UUID", NULL_UUID);
        this.ownerName = new ConfigKey<> (this,"Owner-Name", "");
        this.ownerUUID = new ConfigKey<> (this,"Owner-UUID", NULL_UUID);
        this.skinProperty = new ConfigKey<>(this, "skin-property", "");
        this.skinSignature = new ConfigKey<>(this, "skin-signature", "");
        this.dollLastJoinServer = new ConfigKey<>(this, "last-join-server", "");
        getDollSetting();
        getPlayerSetting();
        getGeneralSetting();
    }

    public DollConfig(Doll doll, YamlConfiguration config) {
        super(config);
        this.doll = doll;
        //this.dollName = new ConfigKey<>(this, "Doll-Name", doll.getBukkitPlayer().getName());
        this.dollFile = FILE_UTIL.getOrCreateFile(FILE_UTIL.getDollDir(), doll.getBukkitPlayer().getName() + ".yml");
        //this.dollFile = new File(PlayerDoll.getDollDirectory(),doll.getBukkitPlayer().getName()+".yml");
        getData();
        DOLL_CONFIGS.put(doll.getBukkitPlayer().getUniqueId(),this);
        //Bukkit.getPluginManager().callEvent(new DollConfigLoadEvent(this.doll,this));
        //saveConfig();
    }

    @SuppressWarnings("unchecked")
    private void getData() {
        this.today = dateFormat.format(new Date(System.currentTimeMillis()));
        this.creationTimeStamp = new ConfigKey<>(this,"Creation-Date", today);
        this.lastSpawnTimeStamp = new ConfigKey<>(this,"Last-Spawn-Date",today);
        this.dollName = new ConfigKey<> (this,"Doll-Name",doll == null? name : doll.getBukkitPlayer().getName());
        this.dollUUID = new ConfigKey<> (this,"Doll-UUID", NULL_UUID);
        this.ownerName = new ConfigKey<> (this,"Owner-Name", "");
        this.ownerUUID = new ConfigKey<> (this,"Owner-UUID", NULL_UUID);
        this.skinProperty = new ConfigKey<>(this, "skin-property", "");
        this.skinSignature = new ConfigKey<>(this, "skin-signature", "");
        this.dollLastJoinServer = new ConfigKey<>(this, "last-join-server", "");
//        this.skinName = new ConfigKey<>(this,"Skin-Data.Name","");
//        this.skinImageEncoded = new ConfigKey<>(this,"Skin-Data.Skin", "");
//        this.skinCape = new ConfigKey<>(this,"Skin-Data.Cape","");
//        this.skinModel = new ConfigKey<>(this,"Skin-Data.Model","");
//        this.skinSignature = new ConfigKey<>(this,"Skin-Data.Signature","");
//        this.skinProfileID = new ConfigKey<>(this,"Skin-Data.ProfileID","");
//        this.skinTimestamp = new ConfigKey<>(this,"Skin-Data.Timestamp","");

        getDollSetting();
        getPlayerSetting();
        getGeneralSetting();

        this.dollHostility = dollSetting.get(FlagConfig.GlobalFlagType.HOSTILITY);
        this.dollPhantom = dollSetting.get(FlagConfig.GlobalFlagType.PHANTOM);
        this.dollRealPlayerTickUpdate = dollSetting.get(FlagConfig.GlobalFlagType.REAL_PLAYER_TICK_UPDATE);
        this.dollRealPlayerTickAction = dollSetting.get(FlagConfig.GlobalFlagType.REAL_PLAYER_TICK_ACTION);
    }
    @Override
    public boolean checkVersion() {
        // Do Not Check Version For Doll Config
        return true;
    }

//    public void changeSetting(Player player, SettingType type, boolean b) {
//        if (doll == null) {
//            return;
//        }
//        //if player has permission
//        //then is nms?
//        //nms then event
//        boolean trigger = switch (type) {
//            case ENDER_CHEST -> {
//                this.dollEChest.setNewValue(b);
//                yield true;
//            }
//            case INVENTORY -> {
//                this.dollInv.setNewValue(b);
//                yield true;
//            }
//            case JOIN_AT_START -> {
//                this.dollJoinAtStart.setNewValue(b);
//                yield true;
//            }
//            case HOSTILITY -> {
//                this.dollHostility.setNewValue(b);
//                yield true;
//            }
//
//            case INVULNERABLE -> {
//                this.dollInvulnerable.setNewValue(b);
//                yield false;
//            }
//            case PUSHABLE -> {
//                this.dollPushable.setNewValue(b);
//                yield false;
//            }
//            case PICKABLE -> {
//                this.dollPickable.setNewValue(b);
//                yield false;
//            }
//            case PHANTOM -> {
//                this.dollPhantom.setNewValue(b);
//                yield false;
//            }
//            case GRAVITY -> {
//                this.dollGravity.setNewValue(b);
//                yield false;
//            }
//            case GLOW -> {
//                this.dollGlow.setNewValue(b);
//                yield false;
//            }
//            case LARGE_STEP_SIZE -> {
//                this.dollLargeStepSize.setNewValue(b);
//                yield false;
//            }
//            case REAL_PLAYER_TICK_UPDATE -> {
//                this.dollRealPlayerTickUpdate.setNewValue(b);
//                yield false;
//            }
//            case REAL_PLAYER_TICK_ACTION -> {
//                this.dollRealPlayerTickAction.setNewValue(b);
//                yield false;
//            }
//        };
//
//        if (trigger) {
//            return;
//        }
        //Bukkit.getPluginManager().callEvent(new DollSettingChangeEvent(player, doll, type, b));
    //}

    private void getDollSetting() {
        Arrays.stream(DollSettings.values()).forEach(settings -> {
            ConfigKey<DollConfig, Boolean> configKey = new ConfigKey<>(this, settings.path, settings.getDefaultSetting());
            dollSetting.put(settings.type, configKey);
        });
    }

    private void getPlayerSetting() {
        if (yamlConfiguration.contains("Player-Setting")) {
            Map<String, Object> uuidMap = yamlConfiguration.getConfigurationSection("Player-Setting").getValues(false);
            uuidMap.forEach((uuid,o) -> {
                UUID playerUUID = UUID.fromString(uuid);
                EnumMap<FlagConfig.PersonalFlagType,Boolean> map = new EnumMap<>(FlagConfig.PersonalFlagType.class);
                this.playerSetting.put(playerUUID, map);
                Map<String, Object> settingMap = yamlConfiguration.getConfigurationSection("Player-Setting." + playerUUID).getValues(true);
                settingMap.forEach((s, b) -> {
                    map.put(FlagConfig.PersonalFlagType.valueOf(s.toUpperCase()), (boolean) b);
                });
            });
        }
    }

    private void getGeneralSetting() {
        if (yamlConfiguration.contains("General-Setting")) {
            Arrays.stream(FlagConfig.PersonalFlagType.values()).forEach(personalFlagType -> {
                this.generalSetting.put(personalFlagType, yamlConfiguration.getBoolean("General-Setting." + personalFlagType.name().toLowerCase()));
            });
        } else {
            Map<FlagConfig.PersonalFlagType, ?> flagMap = FlagConfig.get().getPersonalFlags();
            flagMap.keySet().forEach(s -> this.generalSetting.put(s, false));
        }
    }
    public void saveConfig() {
        this.generalSetting.forEach((s,b) -> {
            yamlConfiguration.set("General-Setting." + s.getCommand().toLowerCase(), b);
        });
        this.playerSetting.forEach((uuid,map) -> {
            // not to save when values are all false (in default state)
            if (!map.containsValue(true)) {
                return;
            }
            map.forEach((s,b) -> {
                yamlConfiguration.set("Player-Setting." + uuid.toString() + "." + s.getCommand().toLowerCase(), b);
            });
        });
        try {
            yamlConfiguration.save(dollFile);
            PlayerDoll.LOGGER.log(Level.INFO, "Doll Config [{0}] Saved Successfully", dollName.getValue());
        } catch (IOException e) {
            PlayerDoll.LOGGER.log(Level.WARNING, "Doll Config [{0}] Saved Successfully", dollName.getValue());
            PlayerDoll.LOGGER.log(Level.WARNING,"Could not Save Doll Config [{0}]", dollName.getValue());
        }
        //Bukkit.getPluginManager().callEvent(new DollConfigUnLoadEvent(this.doll,this));
        DOLL_CONFIGS.remove(UUID.fromString(dollUUID.getValue()));
    }
    public enum DollSettings {
        //ECHEST(FlagConfig.GlobalFlagType.ECHEST, "Doll-Setting.echest", true),
        GLOW(FlagConfig.GlobalFlagType.GLOW, "Doll-Setting.glow", false),
        GRAVITY(FlagConfig.GlobalFlagType.GRAVITY, "Doll-Setting.gravity", true),
        HOSTILITY(FlagConfig.GlobalFlagType.HOSTILITY, "Doll-Setting.hostility", true),
        //INV(FlagConfig.GlobalFlagType.INV, "Doll-Setting.inv", true),
        INVULNERABLE(FlagConfig.GlobalFlagType.INVULNERABLE, "Doll-Setting.invulnerable", false),
        JOIN_AT_START(FlagConfig.GlobalFlagType.JOIN_AT_START, "Doll-Setting.join_at_start", false),
        LARGE_STEP_SIZE(FlagConfig.GlobalFlagType.LARGE_STEP_SIZE, "Doll-Setting.large_step_size", false),
        PHANTOM(FlagConfig.GlobalFlagType.PHANTOM, "Doll-Setting.phantom", false),
        PICKABLE(FlagConfig.GlobalFlagType.PICKABLE, "Doll-Setting.pickable", true),
        PUSHABLE(FlagConfig.GlobalFlagType.PUSHABLE, "Doll-Setting.pushable", false),
        REAL_PLAYER_TICK_UPDATE(FlagConfig.GlobalFlagType.REAL_PLAYER_TICK_UPDATE, "Doll-Setting.real_player_tick_update", false),
        REAL_PLAYER_TICK_ACTION(FlagConfig.GlobalFlagType.REAL_PLAYER_TICK_ACTION, "Doll-Setting.real_player_tick_action", false);

        private final FlagConfig.GlobalFlagType type;
        private final String path;
        private final boolean b;
        DollSettings(FlagConfig.GlobalFlagType type, String path, boolean b) {
            this.type = type;
            this.path = path;
            this.b = b;
        }
        public FlagConfig.GlobalFlagType getType() {
            return type;
        }
        public String getPath() {
            return path;
        }
        public boolean getDefaultSetting() {
            return b;
        }

    }

//    public enum SettingType {
//        ENDER_CHEST {
//            @Override
//            public String getSettingName() {
//                return "echest";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollEChest;
//            }
//        },
//        GLOW {
//            @Override
//            public String getSettingName() {
//                return "glow";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollGlow;
//            }
//        },
//        GRAVITY {
//            @Override
//            public String getSettingName() {
//                return "gravity";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollGravity;
//            }
//        },
//        HOSTILITY {
//            @Override
//            public String getSettingName() {
//                return "hostility";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollHostility;
//            }
//        },
//        INVENTORY {
//            @Override
//            public String getSettingName() {
//                return "inv";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollInv;
//            }
//        },
//        INVULNERABLE {
//            @Override
//            public String getSettingName() {
//                return "invulnerable";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollInvulnerable;
//            }
//        },
//        JOIN_AT_START {
//            @Override
//            public String getSettingName() {
//                return "join_at_start";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollJoinAtStart;
//            }
//        },
//        LARGE_STEP_SIZE {
//            @Override
//            public String getSettingName() {
//                return "large_step_size";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollLargeStepSize;
//            }
//        },
//        PHANTOM {
//            @Override
//            public String getSettingName() {
//                return "phantom";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollPhantom;
//            }
//        },
//        PICKABLE {
//            @Override
//            public String getSettingName() {
//                return "pickable";
//            }
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollPickable;
//            }
//        },
//        PUSHABLE {
//            @Override
//            public String getSettingName() {
//                return "pushable";
//            }
//
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollPushable;
//            }
//        },
//        REAL_PLAYER_TICK_UPDATE {
//            @Override
//            public String getSettingName() {
//                return "real_player_tick_update";
//            }
//
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollRealPlayerTickUpdate;
//            }
//        },
//        REAL_PLAYER_TICK_ACTION {
//            @Override
//            public String getSettingName() {
//                return "real_player_tick_action";
//            }
//
//            @Override
//            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
//                return config.dollRealPlayerTickAction;
//            }
//        };
//
//        public abstract String getSettingName();
//        public abstract ConfigKey<DollConfig,Boolean> getConfigKey(DollConfig config);
//    }

    @Override
    public String getName() {
        return ""; //"Doll Config of [" + doll.getBukkitPlayer().getName() + "]";
    }
}
