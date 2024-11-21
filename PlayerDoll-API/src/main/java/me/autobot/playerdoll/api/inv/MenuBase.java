package me.autobot.playerdoll.api.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public abstract class MenuBase {
    public abstract void open(InventoryOpenEvent event);
    public void close(InventoryCloseEvent event) {};
    public abstract void drag(InventoryDragEvent event);
    public abstract void click(InventoryClickEvent event);

}
