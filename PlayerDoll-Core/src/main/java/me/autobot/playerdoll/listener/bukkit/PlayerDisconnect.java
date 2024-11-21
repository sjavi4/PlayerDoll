package me.autobot.playerdoll.listener.bukkit;


import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerDisconnect implements Listener {
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!DollStorage.ONLINE_DOLLS.containsKey(playerUUID)) {
            if (basicConfig.convertPlayer.getValue()) {
                DollStorage.ONLINE_TRANSFORMS.remove(playerUUID);
            }
            return;
        }
        Doll doll = DollStorage.ONLINE_DOLLS.get(playerUUID);

        Player caller = doll.getCaller();
        if (caller != null) {
            caller.sendMessage(LangFormatter.YAMLReplaceMessage("doll-disconnect", player.getName()));
        }
        //event.getPlayer().setFallDistance(0.0f);

        Entity riding = player.getVehicle();
        if (riding != null) {
            riding.removePassenger(player);
        }
        List<Entity> passengers = player.getPassengers();
        for (Entity p : passengers) {
            player.removePassenger(p);
        }


        if (basicConfig.adjustableMaxPlayer.getValue()) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers()-1);
        }

        if (!basicConfig.broadcastDollDisconnect.getValue()) {
            event.setQuitMessage(null);
        }

        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setSaturation(0.0f);

        DollConfig dollConfig = DollConfig.getOnlineConfig(playerUUID);
        dollConfig.saveConfig();
        DollConfig.DOLL_CONFIGS.remove(playerUUID);
        DollStorage.ONLINE_DOLLS.remove(playerUUID);

        DollMenuHolder holder = DollMenuHolder.HOLDERS.get(playerUUID);
        if (holder != null) {
            holder.onDisconnect();
        }
    }
}
