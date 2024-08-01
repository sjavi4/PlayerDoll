package me.autobot.playerdoll.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MenuWatcher implements Listener {
    private final GUIManager manager;
    public MenuWatcher(GUIManager manager) {
        this.manager = manager;
    }
    @EventHandler
    private void onClick(InventoryClickEvent event) {
        manager.click(event);
    }
    @EventHandler
    private void onDrag(InventoryDragEvent event) {
        manager.drag(event);
    }
    @EventHandler
    private void onOpen(InventoryOpenEvent event) {
        manager.open(event);
    }
    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        manager.close(event);
    }
}