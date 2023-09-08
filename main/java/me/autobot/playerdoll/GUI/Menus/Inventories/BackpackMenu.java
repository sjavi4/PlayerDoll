package me.autobot.playerdoll.GUI.Menus.Inventories;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.GUI.Menus.Inventories.Packets.SetDollInventory;
import me.autobot.playerdoll.GUI.Menus.InventoryMenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
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

public class BackpackMenu extends InventoryGUI {
    private Player doll;
    public BackpackMenu(Player doll) {
        this.doll = doll;
    }
    @Override
    protected org.bukkit.inventory.Inventory createInventory() {
        return Bukkit.createInventory(null, 27, YAMLManager.getConfig("lang").getString("title.backpack"));
    }
    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> {
            super.onClick(event);
            if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }
            if (event.getClickedInventory() == null) {
                Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), (Player) event.getWhoClicked()));
            }

            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                if (currentItem != doll.getInventory().getContents()[event.getSlot()+9]) {
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> PlayerDoll.getGuiManager().openGUI(new BackpackMenu(this.doll), (Player) event.getWhoClicked()));
                }
                event.setCancelled(false);
                int currentSlot = event.getSlot();
                SetDollInventory.UpdateDollInventory(doll, event.getCurrentItem(), currentSlot+9);
            }
        });

    }
    @Override
    public void decorate(Player player) {
        ItemStack[] backpack = new ItemStack[27];
        System.arraycopy(doll.getInventory().getContents(),9, backpack, 0,27);
        //for (int i = 0; i<list.size();i++) {
        //    list.set(i,hotbar[i]);
        //}
        for (int i = 0 ; i< backpack.length;i++) {
            this.addButton(i,this.createMainMenuButton(backpack[i]));
        }
        super.decorate(player);
    }
    /*
    @Override
    public void decorate(Player player) {
        List<ItemStack> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),null));

        ItemStack[] hotbar = Arrays.copyOfRange(doll.getInventory().getContents(),9,36);
        for (int i = 0; i<list.size();i++) {
            list.set(i,hotbar[i]);
        }
        for (int i = 0 ; i< list.size();i++) {
            this.addButton(i,this.createMainMenuButton(list.get(i)));
        }
        super.decorate(player);
    }

     */
    private InventoryButton createMainMenuButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }
}