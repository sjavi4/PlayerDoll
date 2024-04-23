package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.DollConfigHelper;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Util.Configs.PermConfig;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermChecker;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
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

        int[] count = {0};
        UUID uuid = sender.getUniqueId();
        Map<UUID, Integer> dollCountMap = DollManager.PLAYER_DOLL_COUNT_MAP;
        PermChecker permChecker = (perm) ->{
            boolean pass = true;

            if (dollCountMap.containsKey(uuid)) {
                count[0] = dollCountMap.get(uuid);
            } else {
                dollCountMap.put(uuid,0);
            }
            if (perm.enable.getValue()) {
                if (sender.isOp() && perm.opBypass.getValue()) {
                    return true;
                }
                var maxCreate = perm.maxDollCreate.getValue();
                Optional<String> match = maxCreate.keySet().stream().filter(sender::hasPermission).findFirst();
                if (match.isPresent()) {
                    int max = maxCreate.get(match.get());
                    if (count[0] >= max) {
                        sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll", max));
                        pass = false;
                    }
                }
            }
            return pass;
        };

        if (!permChecker.check(PermConfig.get())) {
            return;
        }

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
        dollCountMap.put(uuid,count[0]+1);
        sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateSucceed", dollName.substring(1)));
    }
}
