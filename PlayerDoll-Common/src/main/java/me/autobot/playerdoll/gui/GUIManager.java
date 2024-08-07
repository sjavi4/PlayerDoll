package me.autobot.playerdoll.gui;

import me.autobot.playerdoll.gui.menu.AbstractMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GUIManager {
    public void open(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractMenu menu)) {
            return;
        }
        menu.onOpen(event);
    }
    public void close(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractMenu menu)) {
            return;
        }
        menu.onClose(event);
    }
    public void drag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractMenu menu)) {
            return;
        }
        menu.onDrag(event);
    }
    public void click(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractMenu menu)) {
            return;
        }
        menu.onClick(event);
    }
}
