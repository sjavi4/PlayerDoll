package me.autobot.playerdoll.GUIs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DollInvHolder implements InventoryHolder {

    public Inventory inventory;
    public final ItemStack EMPTY_ITEM = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,1," ",null);
    public final Map<Material, Consumer<Player>> buttonMap = new HashMap<>();
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            return;
        }
        if (event.getCurrentItem() == null) return;
        buttonMap.get(event.getCurrentItem().getType()).accept((Player) event.getWhoClicked());
    }
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
    public void onOpen(InventoryOpenEvent event) {
        /*
        if (event.getInventory() == event.getView().getBottomInventory()) {
            return;
        }

         */
    }
    public void onClose(InventoryCloseEvent event){
        /*
        if (event.getInventory() == event.getView().getBottomInventory()) {
            return;
        }

         */
    }
    public void setupInventoryItem() {
        ItemStack[] defaultContent = new ItemStack[inventory.getSize()];
        Arrays.fill(defaultContent,EMPTY_ITEM);
        inventory.setContents(defaultContent);
        buttonMap.put(EMPTY_ITEM.getType(),(p)->{});
    }

}
