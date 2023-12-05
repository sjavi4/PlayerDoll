package me.autobot.playerdoll.Command.Execute;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Configs.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class create extends SubCommand {
    Player player;
    File dollFile;
    String dollName;
    String dollSkin;
    YAMLManager dollYAML;
    public create() {
    }
    public create(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Player,false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Create")) return;
        
        player = (Player) sender;

        YamlConfiguration globalConfig = ConfigManager.getConfig();

        if (globalConfig.getInt("Global.ServerMaxDoll") != -1) {
            if (!player.isOp() && PlayerDoll.dollManagerMap.size() >= globalConfig.getInt("Global.ServerMaxDoll")) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("DollCapacityIsFull",'&'));
                return;
            }
        }

        //dollName = PlayerDoll.dollIdentifier + doll;

        int count = 0;
        int max = globalConfig.getInt("Global.MaxDollPerPlayer");
        if (max != -1) {
            if (PlayerDoll.playerDollCountMap.containsKey(player.getUniqueId().toString())) {
                count = PlayerDoll.playerDollCountMap.get(player.getUniqueId().toString());
            } else {
                PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), 0);
            }
            if (!player.isOp() && count >= max) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll", '&', new Pair<>( "%a%", Integer.toString(max))));
                return;
            }
        }

        DollDataValidator validator = new DollDataValidator(player,dollName, dollName.substring(1));

        if (validator.isDollNameTooLong()) return;
        if (validator.isDollNameIllegal()) return;
        if (validator.isDollNamePreserved()) return;
        if (validator.isDollAlreadyOnline()) return;

        dollSkin = args == null ? player.getName() : ((String[])args)[0];

        dollFile = new File(PlayerDoll.getDollDirectory(),dollName + ".yml");

        final boolean exist = dollFile.exists();
        dollYAML = YAMLManager.loadConfig(dollName,false);

        if (exist && dollFile.length() > 0) {
            YamlConfiguration dollData = dollYAML.getConfig();
            if (validator.isDollNameRepeat(dollData)) return;
        }
        PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), count + 1);
        execute();
    }
    @Override
    public void execute() {
        dollYAML = YAMLManager.loadConfig(dollName,true);
        YamlConfiguration dollData = dollYAML.getConfig();

        DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        dollData.set("Timestamp", date.format(new Date(System.currentTimeMillis())));
        dollData.set("UUID", UUID.nameUUIDFromBytes(("OfflinePlayer:" + dollName).getBytes(StandardCharsets.UTF_8)).toString());
        dollData.set("Owner", new LinkedHashMap<String,String>(){{put("Name",player.getName());put("UUID",player.getUniqueId().toString());}});
        dollData.set("Share", new ArrayList<String>());
        dollData.set("SkinData", new LinkedHashMap<String,String>());
        dollData.set("Remove", false);
        Map<String, Object> settings = new LinkedHashMap<>();

        YamlConfiguration flag = ConfigManager.getFlag();
        if (flag != null) {
            Map<String,Object> configs = new LinkedHashMap<>(flag.getConfigurationSection("GlobalFlags").getValues(false));
            configs.keySet().forEach(k -> settings.put(k, flag.getBoolean("GlobalFlags."+k+".Default")));
        }
        dollData.createSection("setting", settings);
        dollData.createSection("generalSetting");
        dollData.createSection("playerSetting");
        try {
            URL url_playerName = new URL("https://api.mojang.com/users/profiles/minecraft/" + dollSkin);
            String uuid = JsonParser.parseReader(new InputStreamReader(url_playerName.openStream())).getAsJsonObject().get("id").getAsString();

            URL url_skinTexture = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            JsonObject textureProperty = JsonParser.parseReader(new InputStreamReader(url_skinTexture.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
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

            Map<String,String> skinData = new HashMap<>();
            skinData.put("Name", profile.get("profileName").getAsString());
            skinData.put("Skin", encoder.encodeToString(skinImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Cape", capeImage == null ? "" : encoder.encodeToString(capeImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Model",model);
            skinData.put("Signature",signature);
            skinData.put("profileId",profileId);
            skinData.put("timestamp",timestamp);

            dollData.set("SkinData",skinData);
        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            e.printStackTrace();
        }
        dollYAML.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateSucceed",'&', new Pair<>("%a%", dollName.substring(1))));
    }

    @Override
    public ArrayList<String> targetSelection(UUID uuid) {
        return new ArrayList<>(Collections.singleton("?"));
    }
    @Override
    public List<Object> tabSuggestion() {
        return List.of("<skin_name>");
    }
}
