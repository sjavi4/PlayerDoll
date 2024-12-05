package me.autobot.playerdoll.api.doll;


import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.config.AbstractConfig;
import me.autobot.playerdoll.api.config.key.ConfigKey;
import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;
import me.autobot.playerdoll.api.inv.button.InvButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class DollConfig extends AbstractConfig {

    /**
     * Storage for Online Doll
     */
    public static final Map<UUID,DollConfig> DOLL_CONFIGS = new ConcurrentHashMap<>();

    public static final String NULL_UUID = "00000000-0000-0000-0000-000000000000";

    public final ConfigKey<DollConfig,String> dollName;
    public final ConfigKey<DollConfig,String> dollUUID;
    public final ConfigKey<DollConfig,String> ownerName;
    public final ConfigKey<DollConfig,String> ownerUUID;

    public final ConfigKey<DollConfig, String> skinProperty;
    public final ConfigKey<DollConfig, String> skinSignature;
    // cached these 4
    public final ConfigKey<DollConfig,Boolean> dollHostility;
    public final ConfigKey<DollConfig,Boolean> dollPhantom;
    public final ConfigKey<DollConfig,Boolean> dollRealPlayerTickUpdate;
    public final ConfigKey<DollConfig,Boolean> dollRealPlayerTickAction;

    public Map<GlobalFlagButton, ConfigKey<DollConfig,Boolean>> dollSetting = new LinkedHashMap<>();
    public Map<PersonalFlagButton, Boolean> generalSetting = new LinkedHashMap<>();
    public Map<UUID, LinkedHashMap<PersonalFlagButton, Boolean>> playerSetting = new LinkedHashMap<>();

    /**
     * @param dollUUID Online Doll UUID
     * @return A cached config instance (discard and save when Doll offline)
     */
    public static DollConfig getOnlineConfig(UUID dollUUID) {
        return getOnlineConfig(DollStorage.ONLINE_DOLLS.get(dollUUID));
    }


    /**
     * @param doll Online Doll
     * @return A cached config instance (discard and save when Doll offline)
     */
    public static DollConfig getOnlineConfig(Doll doll) {
        Objects.requireNonNull(doll, "Cannot Get Online Config from Doll. (Offline or Not a Doll)");
        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        Player dollPlayer = doll.getBukkitPlayer();
        UUID dollUUID = dollPlayer.getUniqueId();
        if (DOLL_CONFIGS.containsKey(dollUUID)) {
            return DOLL_CONFIGS.get(dollUUID);
        }
        DollConfig config = new DollConfig(fileUtil.getFile(fileUtil.getDollDir(), DollNameUtil.dollShortName(dollPlayer.getName()).concat(".yml")));
        DOLL_CONFIGS.put(dollUUID, config);
        return config;
    }

    /**
     * @param dollName Doll Config name without extension
     * @return A new instance of Doll Config (Do not cache it)
     */
    public static DollConfig getTemporaryConfig(String dollName) {
        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        return getTemporaryConfig(fileUtil.getFile(fileUtil.getDollDir(), dollName.concat(".yml")));
    }

    /**
     * @param fileName Doll Config File
     * @return A new instance of Doll Config (Do not cache it)
     */
    public static DollConfig getTemporaryConfig(File fileName) {
        Objects.requireNonNull(fileName, "File is Null For DollConfig.");
        return new DollConfig(fileName);
    }



    private DollConfig(File file) {
        super(file, false);
        String filename = file.getName();
        this.dollName = new ConfigKey<> (this,"Doll-Name", filename.substring(0, filename.lastIndexOf(".")));
        this.dollUUID = new ConfigKey<> (this,"Doll-UUID", NULL_UUID);
        this.ownerName = new ConfigKey<> (this,"Owner-Name", "");
        this.ownerUUID = new ConfigKey<> (this,"Owner-UUID", NULL_UUID);
        this.skinProperty = new ConfigKey<>(this, "skin-property", "");
        this.skinSignature = new ConfigKey<>(this, "skin-signature", "");


        DollSetting.SETTINGS.forEach(s -> {
            ConfigKey<DollConfig, Boolean> configKey = new ConfigKey<>(this, s.getPath(), s.defaultSetting());
            dollSetting.put(s.getType(), configKey);
        });

        var list = InvButton.getButtons().values().stream().filter(b -> b instanceof PersonalFlagButton).map(b -> (PersonalFlagButton)b).toList();
        // Player settings
        if (getYamlConfiguration().contains("Player-Setting")) {
            Map<String, Object> uuidMap = getYamlConfiguration().getConfigurationSection("Player-Setting").getValues(false);
            uuidMap.forEach((uuid,o) -> {
                UUID playerUUID = UUID.fromString(uuid);

                LinkedHashMap<PersonalFlagButton, Boolean> defaultPlayerSettings = new LinkedHashMap<>();
                this.playerSetting.put(playerUUID, defaultPlayerSettings);

                Map<String, Object> settingMap = getYamlConfiguration().getConfigurationSection("Player-Setting." + playerUUID).getValues(true);
                list.forEach(b -> {
                    defaultPlayerSettings.put(b, (boolean) settingMap.getOrDefault(b.registerName(), false));
                });

            });
        }
        // General settings
        list.forEach(b -> {
            this.generalSetting.put(b, getYamlConfiguration().getBoolean("General-Setting.".concat(b.registerName().toLowerCase()), false));
        });

        this.dollHostility = dollSetting.get(GlobalFlagButton.HOSTILITY);
        this.dollPhantom = dollSetting.get(GlobalFlagButton.PHANTOM);
        this.dollRealPlayerTickUpdate = dollSetting.get(GlobalFlagButton.REAL_PLAYER_TICK_UPDATE);
        this.dollRealPlayerTickAction = dollSetting.get(GlobalFlagButton.REAL_PLAYER_TICK_ACTION);
        unloadYAML();
    }

    @Override
    public void saveConfig() {
        this.dollSetting.forEach((s,key) -> {
            getYamlConfiguration().set("Doll-Setting." + s.registerName().toLowerCase(), key.getValue());
        });
        this.generalSetting.forEach((s,b) -> {
            getYamlConfiguration().set("General-Setting." + s.registerName().toLowerCase(), b);
        });
        this.playerSetting.forEach((uuid,map) -> {
            map.forEach((s,b) -> {
                getYamlConfiguration().set("Player-Setting." + uuid.toString() + "." + s.registerName().toLowerCase(), b);
            });
        });
        super.saveConfig();
        PlayerDollAPI.getLogger().log(Level.INFO, "Doll Config [{0}] Saved Successfully", dollName.getValue());
    }

    @Override
    public String name() {
        return String.format("Doll Config (%s)", dollName.getValue());
    }
}
