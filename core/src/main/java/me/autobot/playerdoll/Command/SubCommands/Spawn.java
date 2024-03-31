package me.autobot.playerdoll.Command.SubCommands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import me.autobot.playerdoll.Util.Configs.PermConfig;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;


public class Spawn extends SubCommand {

    public Spawn(Player sender, String dollName, String[] args) {
        super(sender, dollName);

        if (PlayerDoll.dollInvStorage.containsKey(dollName)) {
            PlayerDoll.dollInvStorage.get(dollName).closeOfflineInv();
        }
        dollConfig.saveConfig();
        //dollYAML.reloadConfig();

        if (dollConfig.dollUUID.getValue().equals(DollConfig.NULL_UUID)) {
            return;
        }

        int serverMaxDoll = BasicConfig.get().serverMaxDoll.getValue();// getInt("Global.ServerMaxDoll");
        if (serverMaxDoll > -1 && !sender.isOp() && DollManager.ONLINE_DOLL_MAP.size() >= serverMaxDoll) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("DollCapacityIsFull"));
            return;
        }
        String ownerUUID = dollConfig.ownerUUID.getValue();// getString("Owner.UUID");
        //Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
        /*
        if (owner != null) {
            PermissionManager perm = PermissionManager.getPlayerPermission(owner);
            if (!perm.groupName.equals(permissionManager.groupName)) {
                permissionManager = perm;
                dollConfig.set("Owner.Perm",perm.groupName);
                DateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                dollConfig.set("LastSpawn",date.format(new Date(System.currentTimeMillis())));
                dollYAML.saveConfig();
            }
        }

         */

        //Map<String, Object> map = dollConfig.getConfigurationSection("setting").getValues(false);
        //permissionManager.dollDefaultSettings.forEach(map::putIfAbsent);



        PermChecker permChecker = (perm) ->{
            boolean pass = true;
            int count = 0;
            for (IDoll d : DollManager.ONLINE_DOLL_MAP.values()) {
                if (DollConfig.getOnlineDollConfig(d.getBukkitPlayer().getUniqueId()).ownerUUID.getValue().equals(ownerUUID)) {
                    count++;
                }
            }
            if (perm.enable.getValue()) {
                if (sender.isOp() && perm.opBypass.getValue()) {
                    return true;
                }
                var maxSpawn = perm.maxDollSpawn.getValue();
                Optional<String> match = maxSpawn.keySet().stream().filter(sender::hasPermission).findFirst();
                if (match.isPresent()) {
                    int max = maxSpawn.get(match.get());
                    if (count >= max) {
                        sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll", max));
                        pass = false;
                    }
                }
            }
            return pass;
        };

        if (!permChecker.check(PermConfig.get())) {
            return;
        }
/*
        if (perm.enable.getValue()) {
            if (!sender.isOp() || !perm.opBypass.getValue()) {

            }
            var maxSpawn = perm.maxDollSpawn.getValue();
            Optional<String> match = maxSpawn.keySet().stream().filter(sender::hasPermission).findFirst();
            if (match.isPresent()) {
                int max = maxSpawn.get(match.get());
                if (count >= max) {
                    sender.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll", max));
                    return;
                }
            }
        }


 */
/*
        if (!player.isOp()) {
            int count = 0;
            for (IDoll d : DollManager.ONLINE_DOLL_MAP.values()) {
                if (d.getConfigManager().config.getString("Owner.UUID").equals(ownerUUID)) {
                    count++;
                }
            }
            int maxDollSpawn = (int) permissionManager.groupProperties.get("maxDollSpawn");
            if (count >= maxDollSpawn) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll",maxDollSpawn));
                return;
            }
        }

 */
        /*
        if (!player.isOp() && !(boolean)permissionManager.groupProperties.get("bypassMaxPlayer") && Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("ServerReachMaxPlayer"));
            return;
        }

         */
        boolean align = args != null && args.length > 0 && checkArgumentValid(ArgumentType.ALIGN_IN_GRID,args[0]);
        //dollConfig = dollYAML.reloadConfig().getConfig();
        //UUID configUUID = UUID.fromString(dollConfig.getString("UUID"));
        if (PlayerDoll.useBungeeCord && !BasicConfig.get().dollMultiInstance.getValue()) {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeInt(1);
            output.writeUTF(dollConfig.dollUUID.getValue()); // doll UUID
            output.writeUTF(dollName); // doll Name
            output.writeUTF(sender.getUniqueId().toString()); // caller UUID
            output.writeBoolean(align); // align

            Bukkit.getServer().sendPluginMessage(PlayerDoll.getPlugin(),"playerdoll:doll", output.toByteArray());
            //sender.sendPluginMessage(PlayerDoll.getPlugin(), "playerdoll:player", output.toByteArray());
            return;
            // Wait for BungeeCord reply
        }

        if (Bukkit.hasWhitelist()) {
            Bukkit.getOfflinePlayer(UUID.fromString(dollConfig.dollUUID.getValue())).setWhitelisted(true);
            Bukkit.reloadWhitelist();
        }

        DollManager.getInstance().spawnDoll(dollName, UUID.fromString(dollConfig.dollUUID.getValue()),sender,align);
    }
}
