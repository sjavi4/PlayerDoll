package me.autobot.playerdoll.Command.SubCommand.operations;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

public class spawn implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,3);

        if (!YAMLManager.getConfig("config").getBoolean("Global.BypassMaxPlayer")) {
            if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
                player.sendMessage(TranslateFormatter.stringConvert("ServerIsFull",'&'));
                return;
            }
        }

        if (YAMLManager.getConfig("config").getInt("Global.ServerMaxDoll") != -1) {
            if (PlayerDoll.dollManagerMap.size() >= YAMLManager.getConfig("config").getInt("Global.ServerMaxDoll")) {
                player.sendMessage(TranslateFormatter.stringConvert("CapacityIsFull",'&'));
                return;
            }
        }

        String prefix = PlayerDoll.getDollPrefix();
        if (dollName.length() > 16 - prefix.length()) {
            player.sendMessage(TranslateFormatter.stringConvert("LongName",'&', "%length%" , Integer.toString(16 - prefix.length())));
            return;
        }

        if (PlayerDoll.dollManagerMap.containsKey(prefix + dollName)) {
            player.sendMessage(TranslateFormatter.stringConvert("RepeatName",'&'));
            return;
        }

        if (!dollName.matches("^[a-zA-Z0-9]*$")) {
            player.sendMessage(TranslateFormatter.stringConvert("IllegalName",'&'));
            return;
        }

        if (YAMLManager.getConfig("config").getStringList("Global.PreservedName").stream().anyMatch(s -> s.equalsIgnoreCase(dollName))) {
            player.sendMessage(TranslateFormatter.stringConvert("PreservedName",'&'));
            return;
        }

        String dollSkin = args.length == 1 ? player.getName() : _args[1];

        String align = _args[2] == null ? (_args[1]==null?"":_args[1]) : _args[2];

        if (YAMLManager.getConfig("config").getBoolean("Global.RestrictSkin")) {
            dollSkin = player.getName();
        }

        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();

        File dollFile = new File(PlayerDoll.getDollDirectory(),dollName + ".yml");
        final boolean exist = dollFile.exists();
        YAMLManager dollYAML = YAMLManager.loadConfig(dollFile,dollName,false);
        if (dollYAML == null) {
            int count = 0;
            if (YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer") != -1) {
                if (PlayerDoll.playerDollCountMap.containsKey(player.getUniqueId().toString())) {
                    count = PlayerDoll.playerDollCountMap.get(player.getUniqueId().toString());
                } else {
                    PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), 0);
                }
                if (count >= YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer")) {
                    player.sendMessage(TranslateFormatter.stringConvert("PlayerTooMuchDoll", '&', "%num%", YAMLManager.getConfig("config").getString("Global.MaxDollPerPlayer")));
                    return;
                }
            }
            YamlConfiguration dollData = YAMLManager.loadConfig(dollFile,dollName,true).getConfig();
            PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), count + 1);

            dollData.set("UUID", UUIDUtil.createOfflinePlayerUUID(dollName).toString());
            dollData.set("Owner", player.getUniqueId().toString());
            dollData.set("Share", new ArrayList<String>());
            dollData.set("Remove", false);
            Map<String, Object> settings = new HashMap<>();

            YamlConfiguration flag = YAMLManager.getConfig("flag");
            if (flag != null) {
                flag.getConfigurationSection("default").getValues(true).forEach((k, v) -> {
                    if (k.endsWith(".toggle")) {
                        settings.put(k, v);
                    }
                });
            }
            dollData.createSection("setting", settings);
        } else {
            YamlConfiguration dollData = dollYAML.getConfig();
            boolean isRemove = dollData.getBoolean("Remove");
            if (exist) {
                if (!isRemove) {
                    if (!(player.isOp() || dollData.getString("Owner").equals(player.getUniqueId().toString()) || dollData.getStringList("Share").contains(player.getUniqueId().toString()))) {
                        player.sendMessage(TranslateFormatter.stringConvert("NoPermission", '&'));
                        return;
                    }
                } else {
                    if (YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer") != -1) {
                        int count = 0;
                        if (PlayerDoll.playerDollCountMap.containsKey(player.getUniqueId().toString())) {
                            count = PlayerDoll.playerDollCountMap.get(player.getUniqueId().toString());
                        } else {
                            PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), 0);
                        }
                        if (!player.isOp() && count >= YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer")) {
                            player.sendMessage(TranslateFormatter.stringConvert("PlayerTooMuchDoll", '&', "%num%", YAMLManager.getConfig("config").getString("Global.MaxDollPerPlayer")));
                            return;
                        }
                        PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), count + 1);
                    }
                }
            }
        }
        /*
        if (!exist) {
            if (YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer") != -1) {
                int count = 0;
                if (PlayerDoll.playerDollCountMap.containsKey(player.getUniqueId().toString())) {
                    count = PlayerDoll.playerDollCountMap.get(player.getUniqueId().toString());
                } else {
                    PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), 0);
                }
                if (count >= YAMLManager.getConfig("config").getInt("Global.MaxDollPerPlayer")) {
                    player.sendMessage(TranslateFormatter.stringConvert("PlayerTooMuchDoll", '&', "%num%", YAMLManager.getConfig("config").getString("Global.MaxDollPerPlayer")));
                    return;
                }
                YAMLManager.loadConfig(dollFile,dollName,true);
                PlayerDoll.playerDollCountMap.put(player.getUniqueId().toString(), count + 1);
            }

            dollData.set("UUID", UUIDUtil.createOfflinePlayerUUID(dollName).toString());
            dollData.set("Owner", player.getUniqueId().toString());
            dollData.set("Share", new ArrayList<String>());
            dollData.set("Remove", false);
            Map<String, Object> settings = new HashMap<>();

            YamlConfiguration flag = YAMLManager.getConfig("flag");
            if (flag != null) {
                flag.getConfigurationSection("default").getValues(true).forEach((k, v) -> {
                    if (k.endsWith(".toggle")) {
                        settings.put(k, v);
                    }
                });
            }
            dollData.createSection("setting", settings);
        }

         */


        DollManager doll = new DollManager(serverPlayer.server,serverPlayer.serverLevel(),new GameProfile(UUIDUtil.createOfflinePlayerUUID(dollName),PlayerDoll.getDollPrefix()+dollName), serverPlayer,dollSkin);
        Location pos = player.getLocation();
        if (align.equalsIgnoreCase("gridded")) {
            doll.setPos(Math.round(pos.getX() * 2) / 2.0, pos.getBlockY(), Math.round(pos.getZ() * 2) / 2.0);
        }
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(PlayerDoll.dollManagerMap.keySet().stream().toList(),Collections.singletonList("gridded"));
    }
}
