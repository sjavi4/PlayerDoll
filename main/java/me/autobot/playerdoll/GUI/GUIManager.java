package me.autobot.playerdoll.GUI;

import jline.internal.Nullable;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {

    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();


    public void openGUI(InventoryGUI gui, Player player) {
        this.registerHandledInventory(gui.getInventory(), gui);
        player.openInventory(gui.getInventory());
    }

    public void registerHandledInventory(Inventory inventory, InventoryHandler handler) {
        this.activeInventories.put(inventory, handler);
    }

    public void unregisterInventory(Inventory inventory) {
        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> {
            if (inventory.getViewers().size() == 0) {
                this.activeInventories.remove(inventory);
            }
        });

    }

    public void handleClick(InventoryClickEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onClick(event);
        }
    }

    public void handleDrag(InventoryDragEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null && event.getInventory() == event.getView().getTopInventory()) {
            handler.onDrag(event);
        }
    }

    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onOpen(event);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHandler handler = this.activeInventories.get(inventory);
        if (handler != null) {
            handler.onClose(event);
            this.unregisterInventory(inventory);
        }
    }
}