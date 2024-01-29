package me.autobot.playerdoll.GUIs;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;


public class GUIManager {

    public void open(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof DollInvHolder holder)) {
            return;
        }
        holder.onOpen(event);
    }
    public void close(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof DollInvHolder holder)) {
            return;
        }
        holder.onClose(event);
    }
    public void drag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof DollInvHolder holder)) {
            return;
        }
        holder.onDrag(event);
    }
    public void click(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof DollInvHolder holder)) {
            return;
        }
        holder.onClick(event);
    }
}
