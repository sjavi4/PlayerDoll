package me.autobot.playerdoll.InvMenu;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class InvInitializer implements InvHandler {

    private final Inventory inventory;
    private final Map<Integer, ButtonInitializer> buttonMap = new HashMap<>();
    private final Player player;
    private final Player doll;

    protected InvInitializer(Player player, Player doll) {
        this.inventory = this.createInventory(player, doll);
        this.player = player;
        this.doll = doll;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
    public Player getPlayer() {
        return this.player;
    }
    public Player getDoll() {
        return this.doll;
    }

    public void addButton(int slot, ButtonInitializer button) {
        this.buttonMap.put(slot, button);
    }

    public void decorate(Player player, Player doll) {
        this.buttonMap.forEach((slot, button) -> {
            ItemStack icon = button.getButtonFunction().apply(player,null);
            this.inventory.setItem(slot, icon);
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (this.doll.isDead()) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), player::closeInventory,0);
            return;
        }
        event.setCancelled(event.getClickedInventory() == event.getView().getTopInventory());
        int slot = event.getSlot();
        ButtonInitializer button = this.buttonMap.get(slot);
        if (button != null && (event.getClickedInventory() == event.getView().getTopInventory() || event.getClickedInventory() == null)) {
            button.getClickEventConsumer().accept(event);
        }

    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
        if (this.doll.isDead()) {
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), player::closeInventory,0);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (this.doll.isDead()) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), player::closeInventory,0);
            return;
        }
        this.decorate(player,doll);
    }

    @Override
    public void onCreative(InventoryCreativeEvent event) {
        if (this.doll.isDead()) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), player::closeInventory,0);
            return;
        }
        event.setCancelled(event.getClickedInventory() == event.getView().getTopInventory());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}
    protected abstract Inventory createInventory(Player player, Player doll);

}
