package me.autobot.playerdoll.api.inv;

import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.gui.AbstractMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;
import java.util.function.Consumer;

public final class EventWatcher implements Listener {

    private static EventWatcher instance = null;

    public static void init() {
        if (instance == null) {
            instance = new EventWatcher();
        }
    }

    public static EventWatcher getInstance() {
        return instance;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Doll doll = getDoll(inventory.getHolder());
        if (doll != null) {
            findMenu(doll, inventory, menu -> menu.click(event));
        }
    }
    @EventHandler
    private void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        Doll doll = getDoll(inventory.getHolder());
        if (doll != null) {
            findMenu(doll, inventory, menu -> menu.drag(event));
        }
    }
    @EventHandler
    private void onOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        Doll doll = getDoll(inventory.getHolder());
        if (doll != null) {
            findMenu(doll, inventory, menu -> menu.open(event));
        }
    }
    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Doll doll = getDoll(inventory.getHolder());
        if (doll != null) {
            findMenu(doll, inventory, menu -> menu.close(event));
        }
    }

    private Doll getDoll(InventoryHolder holder) {
        for (UUID dollUUID : DollStorage.ONLINE_DOLLS.keySet()) {
            if (Bukkit.getPlayer(dollUUID) == holder) {
                return DollStorage.ONLINE_DOLLS.get(dollUUID);
            }
        }
        return null;
    }

    private void findMenu(Doll doll, Inventory inventory, Consumer<MenuBase> action) {
        DollMenuHolder holder = DollMenuHolder.HOLDERS.get(doll.getBukkitPlayer().getUniqueId());
        if (holder != null) {
            AbstractMenu menu = holder.searchMenu(inventory);
            if (menu != null) {
                action.accept(menu);
            }
        }
    }
}
