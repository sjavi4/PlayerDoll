package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.PermissionManager;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
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
    void setNoPhantom(boolean b);

    boolean getNoPhantom();

    OfflinePlayer getOwner();
    DollConfigManager getConfigManager();
    EntityPlayerActionPack getActionPack();
    void _resetLastActionTime();
    void _resetAttackStrengthTicker();
    void _setJumping(boolean b);
    void _jumpFromGround();
    void _kill();
    void _disconnect();
    void _setPos(double x, double y, double z);
    void _setMaxUpStep(float h);
    /*
    static void initialDoll(DollConfigManager configManager, String uuid) {
        if ((boolean)configManager.getData().get("Initial")) {
            configManager.setDollSetting("Initial", false);
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat");
            File dat_old = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+uuid+".dat_old");
            dat.delete();
            dat_old.delete();
        }
    }

     */
    static void setConfigInformation(Player player) {
        YamlConfiguration dollConfig = YamlConfiguration.loadConfiguration(new File(PlayerDoll.getDollDirectory(),player.getName()+".yml"));
        //dollConfig = YAMLManager.getConfig(this.getGameProfile().getName());
        new DollConfigManager(dollConfig, player);
        //configManager.addListener(dollSettingMonitor);

    }
    static void setSkin(Player player, IDoll iDoll) {
        if (!Bukkit.getOnlineMode()) {
            return;
        }
        YamlConfiguration dollConfig = DollConfigManager.dollConfigManagerMap.get(player.getUniqueId()).config;
        if ((boolean)PermissionManager.getPermissionGroup(dollConfig.getString("Owner.Perm")).dollProperties.get("restrictSkin")) {
            return;
        }
        String skinName = dollConfig.getString("SkinData.Name");
        if (skinName == null || skinName.isBlank()) {
            return;
        }
        var dollSkinData = dollConfig.getConfigurationSection("SkinData");
        if (dollSkinData != null && dollSkinData.getString("Name").equalsIgnoreCase(skinName)) {
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
    static void resetPhantomStatistic(Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST,0);
    }
    boolean getHurtMarked();
    void setHurtMarked(boolean b);
    static boolean executeHurt(IDoll iDoll, Player doll, boolean damaged) {
        if (damaged) {
            if (iDoll.getHurtMarked()) {
                iDoll.setHurtMarked(false);
                Runnable r = () -> iDoll.setHurtMarked(true);
                if (!PlayerDoll.isFolia) Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),r);
                else PlayerDoll.getFoliaHelper().entityTask(doll,r,1); //FoliaSupport.entityTask(doll,r,1);
            }
        }
        return damaged;
    }
}
