package me.autobot.playerdoll.newCommand.Execute;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import me.autobot.playerdoll.Configs.LangFormatter;
import net.minecraft.core.UUIDUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
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
    public create(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Player,false);
        if (!checkPermission(sender, (String) doll)) return;
        
        player = (Player) sender;

        if (YAMLManager.getConfig("config").getInt("Global.ServerMaxDoll") != -1) {
            if (!player.isOp() && PlayerDoll.dollManagerMap.size() >= YAMLManager.getConfig("config").getInt("Global.ServerMaxDoll")) {
                player.sendMessage(LangFormatter.YAMLReplace("DollCapacityIsFull",'&'));
                return;
            }
        }

        String prefix = PlayerDoll.getDollPrefix();
        dollName = (String) doll;

        int count = 0;
        int max = YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer");
        if (max != -1) {
            if (PlayerDoll.playerDollCountMap.containsKey(player.getUniqueId().toString())) {
                count = PlayerDoll.playerDollCountMap.get(player.getUniqueId().toString());
            } else {
                PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), 0);
            }
            if (!player.isOp() && count >= max) {
                player.sendMessage(LangFormatter.YAMLReplace("PlayerCreateTooMuchDoll", '&', new Pair<>( "%a%", Integer.toString(max))));
                return;
            }
        }

        if (dollName.length() > 16 - prefix.length()) {
            player.sendMessage(LangFormatter.YAMLReplace("LongDollName",'&', new Pair<>("%a%" , Integer.toString(16 - prefix.length()))));
            return;
        }

        if (!dollName.matches("^[a-zA-Z0-9_]*$")) {
            player.sendMessage(LangFormatter.YAMLReplace("IllegalDollName",'&'));
            return;
        }

        if (YAMLManager.getConfig("config").getStringList("Global.PreservedName").stream().anyMatch(s -> s.equalsIgnoreCase(dollName))) {
            player.sendMessage(LangFormatter.YAMLReplace("PreservedDollName",'&', new Pair<>("%a%", dollName)));
            return;
        }

        for (String names : PlayerDoll.dollManagerMap.keySet()) {
            if (names.equalsIgnoreCase(prefix + dollName)) {
                player.sendMessage(LangFormatter.YAMLReplace("InUseDollName", '&', new Pair<>("%a%",dollName)));
                return;
            }
        }

        dollSkin = args == null ? player.getName() : ((String[])args)[0];

        dollFile = new File(PlayerDoll.getDollDirectory(),dollName + ".yml");

        final boolean exist = dollFile.exists();
        YAMLManager dollYAML = YAMLManager.loadConfig(dollFile,dollName,false);

        if (exist && dollFile.length() > 0) {
            YamlConfiguration dollData = dollYAML.getConfig();
            if (!dollData.getBoolean("Remove")) {
                player.sendMessage(LangFormatter.YAMLReplace("RepeatDollName", '&', new Pair<>("%a%", dollName)));
                dollYAML.unloadConfig();
                return;
            }
        }
        PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), count + 1);
        execute();
    }
    @Override
    public void execute() {
        YamlConfiguration dollData = YAMLManager.loadConfig(dollFile,dollName,true).getConfig();

        DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        dollData.set("Timestamp", date.format(new Date(System.currentTimeMillis())));
        dollData.set("UUID", UUIDUtil.createOfflinePlayerUUID(dollName).toString());
        dollData.set("Owner", new HashMap<String,String>(){{put("Name",player.getName());put("UUID",player.getUniqueId().toString());}});
        dollData.set("Share", new ArrayList<String>());
        dollData.set("SkinData", new HashMap<String,String>());
        dollData.set("Remove", false);
        dollData.set("Initial",true);
        Map<String, Object> settings = new HashMap<>();

        YamlConfiguration flag = YAMLManager.getConfig("flag");
        if (flag != null) {
            settings.putAll(flag.getConfigurationSection("default").getValues(true));
        }
        dollData.createSection("setting", settings);

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
        YAMLManager.saveConfig(dollName,true);
        player.sendMessage(LangFormatter.YAMLReplace("PlayerCreateSucceed",'&', new Pair<>("%a%", dollName)));
    }
    public static List<Object> tabSuggestion() {
        return List.of("<skin_name>");
    }
}
