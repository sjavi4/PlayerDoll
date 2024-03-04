package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.DollConfigHelper;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class Create extends SubCommand {
    public Create(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (!sender.hasPermission("playerdoll.command.create")) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandDisabledByPermissionGroup"));
            return;
        }

        DollConfig offlineDollConfig = DollConfig.getOfflineDollConfig(dollName);


        String skin = sender.getName();
        /*
        if (permissionManager == null) {
            permissionManager = PermissionManager.getPlayerPermission(sender);
        }



        if (!(boolean) PermissionManager.getPlayerPermission(sender).groupProperties.get("canCreateDoll")) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandDisabledByPermissionGroup"));
            return;
        }
*/

        int count = 0;
        UUID uuid = sender.getUniqueId();
        Map<UUID, Integer> dollCountMap = PlayerDoll.playerDollCountMap;
        if (dollCountMap.containsKey(uuid)) {
            count = dollCountMap.get(uuid);
        } else {
            dollCountMap.put(uuid,0);
        }
        /*
        int maxCreation = (int) permissionManager.groupProperties.get("maxDollCreation");
        if (maxCreation != -1 && !sender.isOp() && count >= maxCreation) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll", maxCreation));
            return;
        }

         */
        /*
        if (validator.longName()) return;
        if (validator.illegalName()) return;
        if (validator.preservedName()) return;
        if (validator.repeatName()) return;


         */
        //boolean success = PlayerDoll.getVaultHelper().dollCreation(sender);
        //if (!success) {
        //    return;
        //}
        if (args != null && args.length != 0) {
            skin = args[0];
        }
        DollConfigHelper.createDollConfig(sender,dollName,skin,offlineDollConfig);
        //DollConfigHelper.createDollConfig(sender,dollName,skin,permissionManager);
/*
        File dollFile = new File(PlayerDoll.getDollDirectory(), dollName+".yml");
        dollFile.delete();

        YAMLManager configFile = YAMLManager.loadConfig(dollName,true, false);
        dollConfig = configFile.getConfig();

        if (args != null && args.length != 0) {
            skin = args[0];
        }

        DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        String formatedDate = date.format(new Date(System.currentTimeMillis()));
        dollConfig.set("Timestamp", formatedDate);
        dollConfig.set("LastSpawn", formatedDate);
        dollConfig.set("UUID", UUID.randomUUID().toString());
        dollConfig.set("Owner", new LinkedHashMap<String,String>(){{
            put("Name",sender.getName());
            put("UUID",uuid.toString());
            put("Perm",permissionManager.groupName);
        }});
        dollConfig.set("Remove", false);
        dollConfig.set("SkinData", new LinkedHashMap<String,String>());

        dollConfig.createSection("setting", permissionManager.dollDefaultSettings);
        dollConfig.createSection("generalSetting");
        dollConfig.createSection("playerSetting");

        JsonObject textureProperty = null;
        try {
            URL url_playerName = new URL("https://api.mojang.com/users/profiles/minecraft/" + skin);
            String skinUUID = JsonParser.parseReader(new InputStreamReader(url_playerName.openStream())).getAsJsonObject().get("id").getAsString();

            URL url_skinTexture = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + skinUUID + "?unsigned=false");
            textureProperty = JsonParser.parseReader(new InputStreamReader(url_skinTexture.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        } catch (IOException e) {
            System.out.println("Could not get skin data from session servers!");
            //dollFile.delete();
            //e.printStackTrace();
            Map<String,String> skinData = new HashMap<>();
            skinData.put("Name", "");
            skinData.put("Skin", "");
            skinData.put("Cape", "");
            skinData.put("Model", "");
            skinData.put("Signature", "");
            skinData.put("profileId", "");
            skinData.put("timestamp", "");

            dollConfig.set("SkinData",skinData);

            sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateInvalidSkin"));
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

            Map<String,String> skinData = new HashMap<>();
            skinData.put("Name", profile.get("profileName").getAsString());
            skinData.put("Skin", encoder.encodeToString(skinImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Cape", capeImage == null ? "" : encoder.encodeToString(capeImage.getBytes(StandardCharsets.UTF_8)));
            skinData.put("Model",model);
            skinData.put("Signature",signature);
            skinData.put("profileId",profileId);
            skinData.put("timestamp",timestamp);

            dollConfig.set("SkinData",skinData);
        }

 */
//        configFile.saveConfig();
        offlineDollConfig.saveConfig();
        dollCountMap.put(uuid,count+1);
        sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateSucceed", dollName.substring(1)));
    }
}
