package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.CustomEvent.DollConfigLoadEvent;
import me.autobot.playerdoll.CustomEvent.DollConfigUnLoadEvent;
import me.autobot.playerdoll.CustomEvent.DollSettingChangeEvent;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.ConfigLoader;
import me.autobot.playerdoll.Util.Configs.AbstractConfig;
import me.autobot.playerdoll.Util.Configs.FlagConfig;
import me.autobot.playerdoll.Util.Keys.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class DollConfig extends AbstractConfig {
    public static final Map<UUID,DollConfig> DOLL_CONFIGS = new HashMap<>();
    public static final Map<String,DollConfig> OFFLINE_DOLL_CONFIGS = new HashMap<>();
    public static final String NULL_UUID = "00000000-0000-0000-0000-000000000000";
    public IDoll doll;
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
    public ConfigKey<DollConfig,String> skinName;
    public ConfigKey<DollConfig,String> skinImageEncoded;
    public ConfigKey<DollConfig,String> skinCape;
    public ConfigKey<DollConfig,String> skinModel;
    public ConfigKey<DollConfig,String> skinSignature;
    public ConfigKey<DollConfig,String> skinProfileID;
    public ConfigKey<DollConfig,String> skinTimestamp;
    public ConfigKey<DollConfig,Boolean> dollEChest;
    public ConfigKey<DollConfig,Boolean> dollGlow;
    public ConfigKey<DollConfig,Boolean> dollGravity;
    //public boolean dollHideFromList;
    public ConfigKey<DollConfig,Boolean> dollHostility;
    public ConfigKey<DollConfig,Boolean> dollInv;
    public ConfigKey<DollConfig,Boolean> dollInvulnerable;
    public ConfigKey<DollConfig,Boolean> dollJoinAtStart;
    public ConfigKey<DollConfig,Boolean> dollLargeStepSize;
    public ConfigKey<DollConfig,Boolean> dollPhantom;
    public ConfigKey<DollConfig,Boolean> dollPickable;
    public ConfigKey<DollConfig,Boolean> dollPushable;
    public ConfigKey<DollConfig,Boolean> dollRealPlayerTickUpdate;
    public ConfigKey<DollConfig,Boolean> dollRealPlayerTickAction;

    public Map<String, Boolean> generalSetting = new LinkedHashMap<>();
    public Map<UUID, LinkedHashMap<String, Boolean>> playerSetting = new LinkedHashMap<>();
    public static DollConfig getOfflineDollConfig(String dollName) {
        if (OFFLINE_DOLL_CONFIGS.containsKey(dollName)) {
            return OFFLINE_DOLL_CONFIGS.get(dollName);
        }
        File file = new File(PlayerDoll.getDollDirectory(), dollName+".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new DollConfig(dollName, file, YamlConfiguration.loadConfiguration(file));
    }
    public static DollConfig getOnlineDollConfig(UUID dollUUID) {
        if (DOLL_CONFIGS.containsKey(dollUUID)) {
            return DOLL_CONFIGS.get(dollUUID);
        }
        IDoll doll = DollManager.ONLINE_DOLL_MAP.get(dollUUID);
        File file = new File(PlayerDoll.getDollDirectory(), doll.getBukkitPlayer().getName()+".yml");
        return new DollConfig(doll, YamlConfiguration.loadConfiguration(file));
    }
    private DollConfig(String dollName, File dollFile, YamlConfiguration config) {
        super(config);
        this.doll = null;
        this.name = dollName;
        this.dollFile = dollFile;
        getData();
        OFFLINE_DOLL_CONFIGS.put(name,this);
        Bukkit.getPluginManager().callEvent(new DollConfigLoadEvent(this.doll,this));
        //saveConfig();
    }

    public DollConfig(IDoll doll, YamlConfiguration config) {
        super(config);
        this.doll = doll;
        //this.dollName = new ConfigKey<>(this, "Doll-Name", doll.getBukkitPlayer().getName());
        this.dollFile = new File(PlayerDoll.getDollDirectory(),doll.getBukkitPlayer().getName()+".yml");
        getData();
        DOLL_CONFIGS.put(doll.getBukkitPlayer().getUniqueId(),this);
        Bukkit.getPluginManager().callEvent(new DollConfigLoadEvent(this.doll,this));
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
        this.skinName = new ConfigKey<>(this,"Skin-Data.Name","");
        this.skinImageEncoded = new ConfigKey<>(this,"Skin-Data.Skin", "");
        this.skinCape = new ConfigKey<>(this,"Skin-Data.Cape","");
        this.skinModel = new ConfigKey<>(this,"Skin-Data.Model","");
        this.skinSignature = new ConfigKey<>(this,"Skin-Data.Signature","");
        this.skinProfileID = new ConfigKey<>(this,"Skin-Data.ProfileID","");
        this.skinTimestamp = new ConfigKey<>(this,"Skin-Data.Timestamp","");
        this.dollEChest = new ConfigKey<>(this,"Doll-Setting.echest",true);
        this.dollGlow = new ConfigKey<>(this,"Doll-Setting.glow",false);
        this.dollGravity = new ConfigKey<>(this,"Doll-Setting.gravity",true);
        this.dollHostility = new ConfigKey<>(this,"Doll-Setting.hostility",true);
        this.dollInv = new ConfigKey<>(this,"Doll-Setting.inv",true);
        this.dollInvulnerable = new ConfigKey<>(this,"Doll-Setting.invulnerable",false);
        this.dollJoinAtStart = new ConfigKey<>(this,"Doll-Setting.join_at_start",false);
        this.dollLargeStepSize = new ConfigKey<>(this,"Doll-Setting.large_step_size",false);
        this.dollPhantom = new ConfigKey<>(this,"Doll-Setting.phantom",false);
        this.dollPickable = new ConfigKey<>(this,"Doll-Setting.pickable",true);
        this.dollPushable = new ConfigKey<>(this,"Doll-Setting.pushable",false);
        this.dollRealPlayerTickUpdate = new ConfigKey<>(this, "Doll-Setting.real_player_tick_update", false);
        this.dollRealPlayerTickAction = new ConfigKey<>(this, "Doll-Setting.real_player_tick_action", false);


        if (yamlConfiguration.contains("Player-Setting")) {
            Map<String, Object> uuidMap = yamlConfiguration.getConfigurationSection("Player-Setting").getValues(false);
            uuidMap.forEach((uuid,o) -> {
                String[] split = uuid.split("\\.");
                if (split.length == 2) {
                    UUID playerUUID = UUID.fromString(split[1]);
                    Map<String,Boolean> map = this.playerSetting.put(playerUUID, new LinkedHashMap<>());
                    Map<String, Object> settingMap = yamlConfiguration.getConfigurationSection("Player-Setting."+split[1]).getValues(true);
                    settingMap.forEach((s,b) -> {
                        String[] splits = s.split("\\n");
                        if (splits.length == 2) {
                            map.put(splits[1],(boolean)b);
                        }
                    });
                }
            });
        }
        //this.playerSetting = (Map<UUID, LinkedHashMap<String, Boolean>>) getOrDefault("Player-Setting", new LinkedHashMap<UUID,LinkedHashMap<String,Boolean>>());
        if (yamlConfiguration.contains("General-Setting")) {
            Map<String,Object> settingMap = yamlConfiguration.getConfigurationSection("General-Setting").getValues(true);
            settingMap.forEach((s,b) -> {
                String[] split = s.split("\\.");
                if (split.length == 2) {
                    this.generalSetting.put(split[1],(boolean)b);
                }
            });
        } else {
            Map<String, Material> flagMap = FlagConfig.PERSONAL_FLAG_MAP;
            flagMap.keySet().forEach(s -> {
                this.generalSetting.put(s,false);
            });
        }
        //this.generalSetting = (Map<String, Boolean>) getOrDefault("General-Setting", new LinkedHashMap<String,Boolean>());
    }
    @Override
    public boolean checkVersion() {
        // Do Not Check Version For Doll Config
        return true;
    }

    public void changeSetting(Player player, SettingType type, boolean b) {
        if (doll == null) {
            return;
        }
        //if player has permission
        //then is nms?
        //nms then event
        boolean trigger = switch (type) {
            case ENDER_CHEST -> {
                this.dollEChest.setNewValue(b);
                yield true;
            }
            case INVENTORY -> {
                this.dollInv.setNewValue(b);
                yield true;
            }
            case JOIN_AT_START -> {
                this.dollJoinAtStart.setNewValue(b);
                yield true;
            }
            case HOSTILITY -> {
                this.dollHostility.setNewValue(b);
                yield true;
            }

            case INVULNERABLE -> {
                this.dollInvulnerable.setNewValue(b);
                yield false;
            }
            case PUSHABLE -> {
                this.dollPushable.setNewValue(b);
                yield false;
            }
            case PICKABLE -> {
                this.dollPickable.setNewValue(b);
                yield false;
            }
            case PHANTOM -> {
                this.dollPhantom.setNewValue(b);
                yield false;
            }
            case GRAVITY -> {
                this.dollGravity.setNewValue(b);
                yield false;
            }
            case GLOW -> {
                this.dollGlow.setNewValue(b);
                yield false;
            }
            case LARGE_STEP_SIZE -> {
                this.dollLargeStepSize.setNewValue(b);
                yield false;
            }
            case REAL_PLAYER_TICK_UPDATE -> {
                this.dollRealPlayerTickUpdate.setNewValue(b);
                yield false;
            }
            case REAL_PLAYER_TICK_ACTION -> {
                this.dollRealPlayerTickAction.setNewValue(b);
                yield false;
            }
        };
        /*
        if (trigger) {
            return;
        }

         */
        Bukkit.getPluginManager().callEvent(new DollSettingChangeEvent(player, doll, type, b));
    }

    public void saveConfig() {
        this.generalSetting.forEach((s,b) -> {
            yamlConfiguration.set("General-Setting."+s,b);
        });
        this.playerSetting.forEach((uuid,map) -> {
            map.forEach((s,b) -> {
                yamlConfiguration.set("Player-Setting." + uuid.toString() + s,b);
            });
        });
        try {
            yamlConfiguration.save(dollFile);
            PlayerDoll.getPluginLogger().log(Level.INFO, "Doll Config ["+dollName.getValue()+"] Saved Successfully.");
        } catch (IOException e) {
            PlayerDoll.getPluginLogger().log(Level.WARNING,"Could not Save Doll Config ["+dollName.getValue()+"].");
        }
        Bukkit.getPluginManager().callEvent(new DollConfigUnLoadEvent(this.doll,this));
        DOLL_CONFIGS.remove(UUID.fromString(dollUUID.getValue()));
    }
    public enum SettingType {
        ENDER_CHEST {
            @Override
            public String getSettingName() {
                return "echest";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollEChest;
            }
        },
        GLOW {
            @Override
            public String getSettingName() {
                return "glow";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollGlow;
            }
        },
        GRAVITY {
            @Override
            public String getSettingName() {
                return "gravity";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollGravity;
            }
        },
        HOSTILITY {
            @Override
            public String getSettingName() {
                return "hostility";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollHostility;
            }
        },
        INVENTORY {
            @Override
            public String getSettingName() {
                return "inv";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollInv;
            }
        },
        INVULNERABLE {
            @Override
            public String getSettingName() {
                return "invulnerable";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollInvulnerable;
            }
        },
        JOIN_AT_START {
            @Override
            public String getSettingName() {
                return "join_at_start";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollJoinAtStart;
            }
        },
        LARGE_STEP_SIZE {
            @Override
            public String getSettingName() {
                return "large_step_size";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollLargeStepSize;
            }
        },
        PHANTOM {
            @Override
            public String getSettingName() {
                return "phantom";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollPhantom;
            }
        },
        PICKABLE {
            @Override
            public String getSettingName() {
                return "pickable";
            }
            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollPickable;
            }
        },
        PUSHABLE {
            @Override
            public String getSettingName() {
                return "pushable";
            }

            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollPushable;
            }
        },
        REAL_PLAYER_TICK_UPDATE {
            @Override
            public String getSettingName() {
                return "real_player_tick_update";
            }

            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollRealPlayerTickUpdate;
            }
        },
        REAL_PLAYER_TICK_ACTION {
            @Override
            public String getSettingName() {
                return "real_player_tick_action";
            }

            @Override
            public ConfigKey<DollConfig, Boolean> getConfigKey(DollConfig config) {
                return config.dollRealPlayerTickAction;
            }
        };

        public abstract String getSettingName();
        public abstract ConfigKey<DollConfig,Boolean> getConfigKey(DollConfig config);
    }

    @Override
    public String getName() {
        return ""; //"Doll Config of [" + doll.getBukkitPlayer().getName() + "]";
    }
}
