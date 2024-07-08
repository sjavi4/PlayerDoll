package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.EnumMap;

public class PlayerJoin implements Listener {
    public static boolean firstJoin = false;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BasicConfig basicConfig = BasicConfig.get();
        if (!firstJoin && PlayerDoll.BUNGEECORD) {
            firstJoin = true;
            // wait for 5s
            PlayerDoll.scheduler.globalTaskDelayed(() -> PlayerDoll.PLUGIN.prepareDollSpawn(basicConfig.proxyAutoJoinDelay.getValue()), 100);
        }
        Player player = event.getPlayer();
        if (player.getName().startsWith("-") && !basicConfig.broadcastDollJoin.getValue()) {
            event.setJoinMessage(null);
        }

        // Hide players
        if (!player.isOp() || (player.isOp() && !basicConfig.opCanSeeHiddenDoll.getValue())) {
            DollManager.ONLINE_DOLLS.values().forEach(doll -> {
                Player dollPlayer = doll.getBukkitPlayer();
                DollConfig dollConfig = DollConfig.getTemporaryConfig(dollPlayer.getName());
                boolean hide = dollConfig.generalSetting.get(FlagConfig.PersonalFlagType.HIDDEN);
                if (!player.getName().startsWith("-")) {
                    EnumMap<FlagConfig.PersonalFlagType, Boolean> playerSettings = dollConfig.playerSetting.get(player.getUniqueId());
                    if (playerSettings == null) {
                        if (hide) {
                            player.hidePlayer(PlayerDoll.PLUGIN, dollPlayer);
                        } else {
                            player.showPlayer(PlayerDoll.PLUGIN, dollPlayer);
                        }
                    } else {
                        boolean playerHide = playerSettings.get(FlagConfig.PersonalFlagType.HIDDEN);
                        if (playerHide) {
                            player.hidePlayer(PlayerDoll.PLUGIN, dollPlayer);
                        } else {
                            player.showPlayer(PlayerDoll.PLUGIN, dollPlayer);
                        }
                    }
                }
            });
        }
//        if (!DollManager.ONLINE_DOLLS.containsKey(event.getPlayer().getUniqueId())) {
//            return;
//        }


    }
}
