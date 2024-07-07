package me.autobot.playerdoll.listener.doll;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.PermConfig;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.event.DollJoinEvent;
import me.autobot.playerdoll.event.DollSettingEvent;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DollJoin implements Listener {

    @EventHandler
    public void onDollJoin(DollJoinEvent event) {
        // Doll Joining

        Player player = event.getPlayer();
        // Remove temporary OP
        if (player.isOp()) {
            Runnable runnable = () -> {
                player.setOp(false);
                player.setGameMode(GameMode.SURVIVAL);
            };
            PlayerDoll.scheduler.entityTask(runnable, player);
        }

        Player caller = event.getCaller();
        Doll doll = event.getDoll();
        DollGUIHolder.getGUIHolder(doll);

        if (caller != null) {
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("spawn-success"));
            if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
                PlayerDoll.scheduler.foliaTeleportAync(player, caller.getLocation());
            } else {
                player.teleport(caller);
            }
        }

        PermissionAttachment attachment = player.addAttachment(PlayerDoll.PLUGIN);

        BasicConfig basicConfig = BasicConfig.get();
        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }

        for (String perm : basicConfig.dollPermission.getValue()) {
            attachment.setPermission(perm,true);
        }

        DollManager.DOLL_PERMISSIONS.put(player.getUniqueId(),attachment);
        DollManager.ONLINE_DOLLS.put(player.getUniqueId(), event.getDoll());

        player.setSleepingIgnored(true);

        DollConfig dollConfig = DollConfig.getOnlineDollConfig(player.getUniqueId());

        if (PlayerDoll.BUNGEECORD) {
            PlayerDoll.LOGGER.info("Capture Login Success");
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeInt(1);
            output.writeUTF(player.getUniqueId().toString());
            PlayerDoll.sendBungeeCordMessage(output.toByteArray());

            String serverName = DollManager.DOLL_BUNGEE_SERVERS.remove(player.getUniqueId());
            if (serverName != null) {
                dollConfig.dollLastJoinServer.setNewValue(serverName);
            }
        }



        // Sync data
        Arrays.stream(DollConfig.DollSettings.values())
                .forEach(settings -> {
                    boolean value = dollConfig.dollSetting.get(settings.getType()).getValue();
                    DollSettingEvent e = new DollSettingEvent(null, doll, settings, value);
                    PlayerDoll.callSyncEvent(e);
                });
        // Disable push when Spawn
        PlayerDoll.callSyncEvent(new DollSettingEvent(null, doll, DollConfig.DollSettings.PUSHABLE, false));


/*
        String configPrefix = globalConfig.getString("Global.DollPrefix");
        String prefix = ChatColor.translateAlternateColorCodes('&',configPrefix == null? "" : configPrefix);
        String configSuffix = globalConfig.getString("Global.DollSuffix");
        String suffix = ChatColor.translateAlternateColorCodes('&',configSuffix == null? "" : configSuffix);
        player.setPlayerListName(prefix + player.getName() + suffix);
        player.setDisplayName(prefix + player.getName() + suffix);


 */


        // Trigger this after Doll Placed in World

        String prefix = "[BOT]";
        String suffix = "";


        OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(dollConfig.ownerUUID.getValue()));
        if (offlineOwner.isOnline()) {
            Player owner = (Player) offlineOwner;
            PermConfig permConfig = PermConfig.get();
            if (permConfig.enable.getValue()) {
                Map<String, String> prefixes = permConfig.dollPrefixes;
                for (String group : prefixes.keySet()) {
                    if (owner.hasPermission(PermConfig.PERM_PREFIX_STRING + group)) {
                        prefix = prefixes.get(group);
                    }
                }
                Map<String, String> suffixes = permConfig.dollSuffixes;
                for (String group : suffixes.keySet()) {
                    if (owner.hasPermission(PermConfig.PERM_SUFFIX_STRING + group)) {
                        suffix = suffixes.get(group);
                    }
                }
            }
        } else if (caller != null) {
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("owner-offline"));
        }


        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        suffix = ChatColor.translateAlternateColorCodes('&', suffix);
        player.setDisplayName(prefix + player.getName() + suffix);
        player.setPlayerListName(prefix + player.getName() + suffix);

        if (Bukkit.hasWhitelist() && player.isWhitelisted()) {
            player.setWhitelisted(false);
            Bukkit.reloadWhitelist();
        }

        List<String> messageList = basicConfig.dollChatWhenJoin.getValue();
        if (!messageList.isEmpty()) {
            long count = 0;
            long interval = Long.valueOf(basicConfig.dollChatWhenJoinInterval.getValue());
            for (String s : messageList) {
                if (s.isEmpty()) {
                    continue;
                }
                String replaced = s.replaceAll("%name%",player.getName()).replaceAll("%uuid%",player.getUniqueId().toString());
                Runnable task = () -> player.chat(replaced);
                PlayerDoll.scheduler.entityTaskDelayed(task, player, 1 + count*interval);
                count++;
            }
        }
    }
}
