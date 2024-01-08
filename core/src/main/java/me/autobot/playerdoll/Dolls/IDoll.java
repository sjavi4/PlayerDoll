package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.PermissionManager;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface IDoll {
    void setDollSkin(String property, String signature);
    void teleportTo();
    void setDollLookAt();
    boolean canBeSeenAsEnemy();
    void die(DamageSource damageSource);
    void disconnect();
    static void foliaDisconnect(boolean remove,Player doll, IDoll iDoll) {
        if (remove) {
            FoliaSupport.entityTask(doll, iDoll::_kill, 1);
        } else {
            FoliaSupport.entityTask(doll, iDoll::_disconnect, 1);
        }
    }
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
    static boolean canSetSkin() {
        YamlConfiguration globalConfig = ConfigManager.getConfig();
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
        if (PermissionManager.getInstance(dollConfig.getString("Owner.Perm")).restrictSkin) {
            return;
        }
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
                else FoliaSupport.entityTask(doll,r,1);
            }
        }
        return damaged;
    }
    static void PaperRemoveChunkLoader(Object serverLevel, Object serverPlayer) {
        try {
            Object playerChunkLoader = serverLevel.getClass().getField("playerChunkLoader").get(serverLevel);
            playerChunkLoader.getClass().getDeclaredMethod("removePlayer", ServerPlayer.class).invoke(playerChunkLoader, serverPlayer);
        } catch (NoSuchMethodException | NoSuchFieldException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception while invoking Paper's playerChunkLoader");
            throw new RuntimeException(e);
        }
    }
}
