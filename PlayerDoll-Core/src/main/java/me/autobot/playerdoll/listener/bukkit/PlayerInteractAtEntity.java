package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractAtEntity implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            return;
        }
        Entity doll = event.getRightClicked();
        if (!DollStorage.ONLINE_DOLLS.containsKey(doll.getUniqueId())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.isSneaking() && player.getInventory().getItemInMainHand().getType().isAir()) {
            player.chat("/playerdoll:doll menu " + DollNameUtil.dollShortName(doll.getName()));
        }
    }
}
