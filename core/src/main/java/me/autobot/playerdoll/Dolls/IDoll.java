package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.CarpetMod.IEntityPlayerActionPack;
import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface IDoll {
    void setDollSkin(String property, String signature);
    void teleportTo();
    void setDollLookAt();
    boolean canBeSeenAsEnemy();
    void die(DamageSource damageSource);
    void disconnect();
    void foliaDisconnect(boolean force);
    void setNoPhantom(boolean b);

    boolean getNoPhantom();

    OfflinePlayer getOwner();
    DollConfigManager getConfigManager();
    IEntityPlayerActionPack getActionPack();

    boolean _isCrouching();
    boolean _isSprinting();
    void _kill();
    void _disconnect();
    void _setPos(double x, double y, double z);
    void _setMaxUpStep(float h);
    /*
    static void initialDoll(DollConfigManager configManager, String uuid) {
        if ((boolean)configManager.getData().get("Initial")) {
            configManager.setData("Initial", false);
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            dat.delete();
            dat_old.delete();
        }
    }

     */
    static YamlConfiguration setConfigInformation(Player player) {
        YamlConfiguration dollConfig = YamlConfiguration.loadConfiguration(new File(PlayerDoll.getDollDirectory(),player.getName()+".yml"));
        //dollConfig = YAMLManager.getConfig(this.getGameProfile().getName());
        new DollConfigManager(dollConfig, player);
        //configManager.addListener(dollSettingMonitor);

        return dollConfig;
    }
    static boolean canSetSkin() {
        YamlConfiguration globalConfig = ConfigManager.configs.get("config");
        if (globalConfig != null) {
            return !globalConfig.getBoolean("Global.RestrictSkin");
        }
        return false;
    }
    static void setSkin(Player player, IDoll iDoll) {
        if (!Bukkit.getOnlineMode()) {
            return;
        }
        YamlConfiguration dollConfig = DollConfigManager.dollConfigManagerMap.get(player).config;
        String skinName = dollConfig.getString("SkinData.Name");
        var dollSkinData = dollConfig.getConfigurationSection("SkinData");
        if (dollSkinData != null && (skinName == null || dollSkinData.getString("Name").equalsIgnoreCase(skinName))) {
            String model = "";
            if (dollSkinData.getString("Model").equalsIgnoreCase("slim")) {
                model = """
                          "metadata" : {
                            "model" : "slim"
                          }
                    """;
            }
            String cape = "";
            if (!dollSkinData.getString("Cape").equalsIgnoreCase("")) {
                cape = ",\n    \"CAPE\" : {\n" +
                        "      \"url\" : \""+ new String(Base64.getDecoder().decode(dollSkinData.getString("Cape")), StandardCharsets.UTF_8) +"\"\n" +
                        "    }";
            }
            String jsonData = "{\n" +
                    "  \"timestamp\" : "+ dollSkinData.getString("timestamp") + ",\n" +
                    "  \"profileId\" : \""+ dollSkinData.getString("profileId") +"\",\n" +
                    "  \"profileName\" : \""+ dollSkinData.getString("Name") +"\",\n" +
                    "  \"signatureRequired\" : true,\n" +
                    "  \"textures\" : {\n" +
                    "    \"SKIN\" : {\n" +
                    "      \"url\" : \"" + new String(Base64.getDecoder().decode(dollSkinData.getString("Skin")),StandardCharsets.UTF_8) + "\",\n" +
                    model +
                    "    }" +
                    cape + "\n" +
                    "  }\n" +
                    "}";
            iDoll.setDollSkin(Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8)), dollSkinData.getString("Signature"));
        }

    }
}
