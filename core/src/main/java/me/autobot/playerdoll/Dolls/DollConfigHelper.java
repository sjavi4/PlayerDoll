package me.autobot.playerdoll.Dolls;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

public class DollConfigHelper {

    public static File getFile(String dollName) {
        return new File(PlayerDoll.getDollDirectory(),dollName+".yml");
    }
    public static File getPlayerDataFile(String dollUUID) {
        return new File(Bukkit.getServer().getWorldContainer() + File.separator + "world" + File.separator + "playerdata" + File.separator + dollUUID + ".dat");
    }
    public static File getPlayerData_OldFile(String dollUUID) {
        return new File(Bukkit.getServer().getWorldContainer() + File.separator + "world" + File.separator + "playerdata" + File.separator + dollUUID + ".dat_old");
    }
    public static String getDollName(File dollFile) {
        return dollFile.getName().substring(0,dollFile.getName().length()-4);
    }
    public static boolean hasConfig(File dollFile) {
        return dollFile.exists();
    }
    public static boolean hasConfig(String dollName) {
        return getFile(dollName).exists();
    }
    public static YamlConfiguration getConfig(File dollFile) {
        return YamlConfiguration.loadConfiguration(dollFile);
    }
    public static YamlConfiguration getConfig(String dollName) {
        return YamlConfiguration.loadConfiguration(getFile(dollName));
    }
/*
    public static void createDollConfig(Player creator, String dollName, String skinName, PermissionManager permissionGroup) {

        File dollFile = getFile(dollName);
        dollFile.delete();

        YAMLManager configFile = YAMLManager.loadConfig(dollName,true, false);
        YamlConfiguration dollConfig = configFile.getConfig();

        DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        String formatedDate = date.format(new Date(System.currentTimeMillis()));
        dollConfig.set("Timestamp", formatedDate);
        dollConfig.set("LastSpawn", formatedDate);
        dollConfig.set("UUID", UUID.randomUUID().toString());
        dollConfig.set("Owner", new LinkedHashMap<String,String>(){{
            put("Name",creator.getName());
            put("UUID",creator.getUniqueId().toString());
            //put("Perm",permissionGroup.groupName);
        }});
        dollConfig.set("Remove", false);
        dollConfig.set("SkinData", new LinkedHashMap<String,String>());

        //dollConfig.createSection("setting", permissionGroup.dollDefaultSettings);
        dollConfig.createSection("generalSetting");
        dollConfig.createSection("playerSetting");

        JsonObject textureProperty = null;
        Map<String,String> skinData = new LinkedHashMap<>();
        try {
            URL url_playerName = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName);
            String skinUUID = JsonParser.parseReader(new InputStreamReader(url_playerName.openStream())).getAsJsonObject().get("id").getAsString();

            URL url_skinTexture = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + skinUUID + "?unsigned=false");
            textureProperty = JsonParser.parseReader(new InputStreamReader(url_skinTexture.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        } catch (IOException e) {
            PlayerDoll.getPluginLogger().log(Level.INFO, "Could not get skin data from session servers!");
            //System.out.println("Could not get skin data from session servers!");
            skinData.put("Name", "");
            skinData.put("Skin", "");
            skinData.put("Cape", "");
            skinData.put("Model", "");
            skinData.put("Signature", "");
            skinData.put("profileId", "");
            skinData.put("timestamp", "");

            dollConfig.set("SkinData",skinData);

            creator.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateInvalidSkin"));
        }
        if (textureProperty != null) {
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            JsonObject profile = JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(texture)))).getAsJsonObject();
            JsonObject profileTexture = profile.getAsJsonObject("textures");
            JsonObject profileSkin = profileTexture.getAsJsonObject("SKIN");

            String skinImage = profileSkin.get("url").getAsString();
            String model = profileSkin.has("metadata")? "slim" : "";
            String capeImage = profileTexture.has("CAPE") ? profileTexture.getAsJsonObject("CAPE").get("url").getAsString() : null;
            String profileId = profile.get("profileId").getAsString();
            String timestamp = profile.get("timestamp").getAsString();
            var encoder = Base64.getEncoder();

            skinData.put("Name", profile.get("profileName").getAsString());
            skinData.put("Skin", encoder.encodeToString(skinImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Cape", capeImage == null ? "" : encoder.encodeToString(capeImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Model",model);
            skinData.put("Signature",signature);
            skinData.put("profileId",profileId);
            skinData.put("timestamp",timestamp);

            dollConfig.set("SkinData",skinData);
        }
        configFile.saveConfig();
    }


 */
    public static void createDollConfig(Player creator, String dollName, String skinName, DollConfig dollConfig) {
        dollConfig.dollUUID.setNewValue(UUID.randomUUID().toString());
        dollConfig.dollName.setNewValue(dollName);
        dollConfig.ownerName.setNewValue(creator.getName());
        dollConfig.ownerUUID.setNewValue(creator.getUniqueId().toString());

        JsonObject textureProperty = null;
        //Map<String,String> skinData = new LinkedHashMap<>();
        try {
            URL url_playerName = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName);
            String skinUUID = JsonParser.parseReader(new InputStreamReader(url_playerName.openStream())).getAsJsonObject().get("id").getAsString();

            URL url_skinTexture = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + skinUUID + "?unsigned=false");
            textureProperty = JsonParser.parseReader(new InputStreamReader(url_skinTexture.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        } catch (IOException e) {
            PlayerDoll.getPluginLogger().log(Level.INFO, "Could not get skin data from session servers!");
            //System.out.println("Could not get skin data from session servers!");
            creator.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateInvalidSkin"));
        }
        if (textureProperty != null) {
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            JsonObject profile = JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(texture)))).getAsJsonObject();
            JsonObject profileTexture = profile.getAsJsonObject("textures");
            JsonObject profileSkin = profileTexture.getAsJsonObject("SKIN");

            String skinImage = profileSkin.get("url").getAsString();
            String model = profileSkin.has("metadata")? "slim" : "";
            String capeImage = profileTexture.has("CAPE") ? profileTexture.getAsJsonObject("CAPE").get("url").getAsString() : null;
            String profileId = profile.get("profileId").getAsString();
            String timestamp = profile.get("timestamp").getAsString();
            var encoder = Base64.getEncoder();

            dollConfig.skinName.setNewValue(profile.get("profileName").getAsString());
            dollConfig.skinImageEncoded.setNewValue(encoder.encodeToString(skinImage.getBytes(StandardCharsets.UTF_8)));
            dollConfig.skinCape.setNewValue(capeImage == null ? "" : encoder.encodeToString(capeImage.getBytes(StandardCharsets.UTF_8)));
            dollConfig.skinModel.setNewValue(model);
            dollConfig.skinSignature.setNewValue(signature);
            dollConfig.skinProfileID.setNewValue(profileId);
            dollConfig.skinTimestamp.setNewValue(timestamp);
/*
            skinData.put("Name", profile.get("profileName").getAsString());
            skinData.put("Skin", encoder.encodeToString(skinImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Cape", capeImage == null ? "" : encoder.encodeToString(capeImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Model",model);
            skinData.put("Signature",signature);
            skinData.put("profileId",profileId);
            skinData.put("timestamp",timestamp);

            dollConfig.set("SkinData",skinData);

 */
        }



        //DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        //String formatedDate = date.format(new Date(System.currentTimeMillis()));
        //dollConfig.set("Timestamp", formatedDate);
        //dollConfig.set("LastSpawn", formatedDate);
        /*
        dollConfig.set("UUID", UUID.randomUUID().toString());
        dollConfig.set("Owner", new LinkedHashMap<String,String>(){{
            put("Name",creator.getName());
            put("UUID",creator.getUniqueId().toString());
            put("Perm",permissionGroup.groupName);
        }});

         */
        //dollConfig.set("Remove", false);

        //dollConfig.set("SkinData", new LinkedHashMap<String,String>());
/*
        dollConfig.createSection("setting", permissionGroup.dollDefaultSettings);
        dollConfig.createSection("generalSetting");
        dollConfig.createSection("playerSetting");
        configFile.saveConfig();

 */
    }
}
