package me.autobot.playerdoll.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryGUI implements InventoryHandler {
    private final Inventory inventory;
    private final Map<Integer, InventoryButton> buttonMap = new HashMap<>();

    public InventoryGUI() {
        this.inventory = this.createInventory();
    }
    public Inventory getInventory() {
        return this.inventory;
    }

    public void addButton(int slot, InventoryButton button) {
        this.buttonMap.put(slot, button);
    }

    public void decorate(Player player) {
        this.buttonMap.forEach((slot, button) -> {
            ItemStack icon = button.getIconCreator().apply(player);
            this.inventory.setItem(slot, icon);
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(event.getClickedInventory() == event.getView().getTopInventory());
        int slot = event.getSlot();
        InventoryButton button = this.buttonMap.get(slot);
        if (button != null) {
            button.getEventConsumer().accept(event);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.decorate((Player) event.getPlayer());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
    protected abstract Inventory createInventory();


}