package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.GUIManager;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;


public class DollDisconnectEvent implements Listener {
    @EventHandler
    public void onDollDisconnect(PlayerQuitEvent event) {
        if (!PlayerDoll.dollManagerMap.containsKey(event.getPlayer().getName())) {
            return;
        }
        if (YAMLManager.getConfig("config").getBoolean("Global.FlexibleServerMaxPlayer")) {
            Bukkit.setMaxPlayers(Bukkit.getMaxPlayers() - 1);
        }
        event.setQuitMessage(null);
        PlayerDoll.dollManagerMap.remove(event.getPlayer().getName());
        boolean success = YAMLManager.saveConfig(event.getPlayer().getName().substring(PlayerDoll.getDollPrefix().length()),true);
        if (success) {
            System.out.println("Successfully Save Config for Doll " + event.getPlayer().getName());
        } else {
            System.out.println("Could Not Save Config for Doll " + event.getPlayer().getName());
        }
        ((InventoryGUI)event.getPlayer().getMetadata("DollArmorMenu").get(0).value()).getInventory().getViewers().forEach(p -> Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),p::closeInventory,0));
        ((InventoryGUI)event.getPlayer().getMetadata("DollHotbarMenu").get(0).value()).getInventory().getViewers().forEach(p -> Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),p::closeInventory,0));
        ((InventoryGUI)event.getPlayer().getMetadata("DollInvenMenu").get(0).value()).getInventory().getViewers().forEach(p -> Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),p::closeInventory,0));
        ((InventoryGUI)event.getPlayer().getMetadata("DollEnderChestMenu").get(0).value()).getInventory().getViewers().forEach(p -> Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),p::closeInventory,0));
    }
}
