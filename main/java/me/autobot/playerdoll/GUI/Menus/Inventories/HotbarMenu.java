package me.autobot.playerdoll.GUI.Menus.Inventories;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.GUI.Menus.Inventories.Packets.SetDollInventory;
import me.autobot.playerdoll.GUI.Menus.InventoryMenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HotbarMenu extends InventoryGUI {
    private Player doll;
    public HotbarMenu(Player doll) {this.doll = doll;}
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9 , YAMLManager.getConfig("lang").getString("title.hotbar"));
    }


    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> {
            super.onClick(event);
            if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }
            if (event.getClickedInventory() == null) {
                PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), (Player) event.getWhoClicked());
            }
            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                if (cursor.getType().equals(Material.AIR)) { //Take Item from doll

                } else { //Place Item
                    if (currentItem != doll.getInventory().getContents()[event.getSlot()]) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), () -> PlayerDoll.getGuiManager().openGUI(new HotbarMenu(this.doll), (Player) event.getWhoClicked()));
                    }
                    event.setCancelled(false);
                    int currentSlot = event.getSlot();
                    SetDollInventory.UpdateDollInventory(doll, event.getCurrentItem(), currentSlot);
                }
            }

        });
    }

    @Override
    public void decorate(Player player) {
        ItemStack[] hotbar = new ItemStack[9];
        System.arraycopy(doll.getInventory().getContents(),0, hotbar, 0,9);
        for (int i = 0 ; i< hotbar.length;i++) {
            this.addButton(i,this.createMainMenuButton(hotbar[i]));
        }
        super.decorate(player);
    }
    private InventoryButton createMainMenuButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }
}