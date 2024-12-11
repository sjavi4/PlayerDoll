package me.autobot.playerdoll.listener.doll;

import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.config.impl.PermConfig;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollSetting;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.event.doll.DollJoinEvent;
import me.autobot.playerdoll.api.event.doll.DollSettingEvent;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

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
                // Try disable op
                //player.setOp(false);
                player.setGameMode(GameMode.SURVIVAL);
            };
            PlayerDollAPI.getScheduler().entityTask(runnable, player);
        }

        Player caller = event.getCaller();
        Doll doll = event.getDoll();

        DollStorage.ONLINE_DOLLS.put(player.getUniqueId(), doll);
        new DollMenuHolder(event.getDoll());
        //DollMenuHolder.HOLDERS.get(player.getUniqueId());

        if (caller != null) {
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("spawn-success"));
            // keep flying
//            if (caller.getGameMode() == GameMode.CREATIVE && player.getAllowFlight()) {
//                player.setFlying(caller.isFlying());
//            }
//            if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
//                SchedulerHelper.scheduler.foliaTeleportAync(player, caller.getLocation());
//            } else {
//                player.teleport(player);
//            }
        }


        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();
        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()+1);
        }

        Plugin plugin = PlayerDollAPI.getInstance();
        PermissionAttachment attachment = player.addAttachment(plugin);

        for (String perm : basicConfig.dollPermission.getValue()) {
            if (perm.isEmpty()) {
                continue;
            }
            attachment.setPermission(perm,true);
        }

        DollStorage.DOLL_PERMISSIONS.put(player.getUniqueId(),attachment);

        player.setSleepingIgnored(true);

        DollConfig dollConfig = DollConfig.getOnlineConfig(player.getUniqueId());

        // Hide players
        boolean hide = dollConfig.generalSetting.get(PersonalFlagButton.HIDDEN);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (DollStorage.ONLINE_DOLLS.containsKey(onlinePlayer.getUniqueId())) {
                continue;
            }
            if (player.isOp() && basicConfig.opCanSeeHiddenDoll.getValue()) {
                continue;
            }
            Map<PersonalFlagButton, Boolean> playerSettings = dollConfig.playerSetting.get(onlinePlayer.getUniqueId());
            if (playerSettings == null) {
                if (hide) {
                    onlinePlayer.hidePlayer(plugin, player);
                } else {
                    onlinePlayer.showPlayer(plugin, player);
                }
                continue;
            }
            boolean playerHide = playerSettings.get(PersonalFlagButton.HIDDEN);
            if (playerHide) {
                onlinePlayer.hidePlayer(plugin, player);
            } else {
                onlinePlayer.showPlayer(plugin, player);
            }
        }

        // Sync data
        DollSetting.SETTINGS.forEach(s -> {
            boolean value = dollConfig.dollSetting.get(s.getType()).getValue();
            DollSettingEvent e = new DollSettingEvent(null, doll, s, value);
            Bukkit.getPluginManager().callEvent(e);
        });
        // Disable push when Spawn
        Bukkit.getPluginManager().callEvent(new DollSettingEvent(null, doll, DollSetting.PUSHABLE, false));

        PermConfig permConfig = PlayerDollAPI.getConfigLoader().getPermConfig();

        String prefix = permConfig.dollPrefixes.getOrDefault("default", "[BOT]");
        String suffix = permConfig.dollSuffixes.getOrDefault("default", "");


        OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(dollConfig.ownerUUID.getValue()));
        if (offlineOwner.isOnline()) {
            Player owner = (Player) offlineOwner;
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
                PlayerDollAPI.getScheduler().entityTaskDelayed(task, player, 1 + count*interval);
                count++;
            }
        }
    }
}
