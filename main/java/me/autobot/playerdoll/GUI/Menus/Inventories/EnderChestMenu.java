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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnderChestMenu extends InventoryGUI {
    private Player doll;
    public EnderChestMenu(Player doll) {this.doll = doll;}
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, YAMLManager.getConfig("lang").getString("title.enderchest"));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), () -> {
            super.onClick(event);
            if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }
            if (event.getClickedInventory() == null) {
                PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), (Player) event.getWhoClicked());
            }
            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                event.setCancelled(false);
                int currentSlot = event.getSlot();
                SetDollInventory.UpdateDollEnderChest(doll, event.getCurrentItem(), currentSlot);
            }
        });
    }
    /*
    @Override
    public void onOpen(InventoryOpenEvent event) {
        System.arraycopy(doll.getEnderChest().getContents(),0,this.getInventory().getContents(),0,27);
    }

     */

    @Override
    public void decorate(Player player) {
        //List<ItemStack> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),null));

        ItemStack[] enderChest = doll.getEnderChest().getContents();
        for (int i = 0 ; i< enderChest.length;i++) {
            this.addButton(i,this.createMainMenuButton(enderChest[i]));
        }
        super.decorate(player);
    }
    private InventoryButton createMainMenuButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }

}