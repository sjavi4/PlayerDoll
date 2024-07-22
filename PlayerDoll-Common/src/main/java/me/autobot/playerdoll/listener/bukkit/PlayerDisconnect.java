package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.gui.menu.Menu;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerDisconnect implements Listener {
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        BasicConfig basicConfig = BasicConfig.get();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!DollManager.ONLINE_DOLLS.containsKey(playerUUID)) {
            if (basicConfig.convertPlayer.getValue()) {
                DollManager.ONLINE_PLAYERS.remove(playerUUID);
            }
            return;
        }
        Doll doll = DollManager.ONLINE_DOLLS.get(playerUUID);

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

        //PermissionAttachment attachment = DollManager.DOLL_PERMISSION_MAP.get(uuid);
        //for (String perm : basicConfig.dollPermission.getValue()) {
        //    attachment.unsetPermission(perm);
        //}
        //DollManager.DOLL_PERMISSION_MAP.remove(uuid);

        if (!basicConfig.broadcastDollDisconnect.getValue()) {
            event.setQuitMessage(null);
        }

        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setSaturation(0.0f);

        DollConfig dollConfig = DollConfig.getOnlineDollConfig(playerUUID);
        dollConfig.saveConfig();
        DollManager.ONLINE_DOLLS.remove(playerUUID);

        DollGUIHolder holder = DollGUIHolder.DOLL_GUI_HOLDERS.get(playerUUID);
        if (holder != null) {
            Consumer<Menu> closeInv = menu -> {
                if (menu != null) {
                    List<HumanEntity> clone = new ArrayList<>(menu.getInventory().getViewers());
                    clone.forEach(HumanEntity::closeInventory);
                }
            };
            holder.menus.values().forEach(closeInv);
            holder.psetMenus.values().forEach(closeInv);
        }
    }
}
