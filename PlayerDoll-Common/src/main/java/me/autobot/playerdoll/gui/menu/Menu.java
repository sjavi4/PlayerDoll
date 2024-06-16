package me.autobot.playerdoll.gui.menu;

import me.autobot.playerdoll.gui.DollGUIHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public interface Menu extends InventoryHolder {
    DollGUIHolder.MenuType getMenuType();

    void onOpen(InventoryOpenEvent event);
    void onClose(InventoryCloseEvent event);
    void onClick(InventoryClickEvent event);
    void onDrag(InventoryDragEvent event);
}
