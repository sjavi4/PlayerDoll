package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

public class PlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        //Player player = event.getEntity();
        if (!DollManager.ONLINE_DOLLS.containsKey(event.getEntity().getUniqueId())) {
            return;
        }
        if (!BasicConfig.get().broadcastDollDeath.getValue()) {
            event.setDeathMessage(null);
        }
//        DollConfig dollConfig = DollConfig.getOnlineDollConfig(player.getUniqueId());
//        if (!dollConfig.removed) {
//            //return;
//        }
        // Clear exp
//        event.setDroppedExp(0);
//
//        int totalExp = (int) (player.getExp() * player.getExpToLevel());
//        player.setLevel(Math.max(0, player.getLevel()-1));
//        while (player.getLevel() >= 0 && totalExp < Integer.MAX_VALUE) {
//            totalExp += player.getExpToLevel();
//            player.setLevel(Math.max(0, player.getLevel()-1));
//        }
//        event.setDroppedExp(totalExp);
//
//        event.getDrops().clear();
//        PlayerInventory inventory = player.getInventory();
//        event.getDrops().addAll(Arrays.asList(inventory.getContents()));
//        event.getDrops().addAll(Arrays.asList(inventory.getArmorContents()));
//        event.getDrops().add(inventory.getItemInOffHand());
//        event.getDrops().addAll(Arrays.asList(player.getEnderChest().getContents()));
    }
}
