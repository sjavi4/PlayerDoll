package me.autobot.playerdoll.InvMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

public class InvManager {
    public final Map<Player, Pair<Inventory, InvHandler>> invManager = new HashMap<>();


    public void openInv(InvInitializer inv, Player player) {
        registerInventory(player, inv.getInventory(), inv);
        player.openInventory(inv.getInventory());
    }

    public void registerInventory(Player player, Inventory inventory, InvHandler invHandler) {
        this.invManager.put(player, new Pair<>(inventory,invHandler));
    }

    public void unregisterInventory(Player player) {
        this.invManager.remove(player);
    }

    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.invManager.get(player) == null) {return;}
        InvHandler handler = this.invManager.get(player).getB();
        if (handler != null) {
            handler.onClick(event);
        }
        /*
        List<Pair<Inventory,InvHandler>> pair = this.invManager.values().stream().filter(p -> p.getA() == event.getInventory()).toList();
        if (!pair.isEmpty()) {
            InvHandler handler = pair.get(0).getB();
            if (handler != null && (event.getClickedInventory() == event.getView().getTopInventory() || event.getClickedInventory() == null)) {
                handler.onClick(event);
            }
        }

         */
    }

    public void handleCreative(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.invManager.get(player) == null) {return;}
        InvHandler handler = this.invManager.get(player).getB();
        if (handler != null) {
            handler.onCreative(event);
        }
        /*
        List<Pair<Inventory,InvHandler>> pair = this.invManager.values().stream().filter(p -> p.getA() == event.getInventory()).toList();
        if (!pair.isEmpty()) {
            InvHandler handler = pair.get(0).getB();
            if (handler != null) {
                handler.onCreative(event);
            }
        }

         */
    }

    public void handleDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.invManager.get(player) == null) {return;}
        InvHandler handler = this.invManager.get(player).getB();
        if (handler != null) {
            handler.onDrag(event);
        }
        /*
        List<Pair<Inventory,InvHandler>> pair = this.invManager.values().stream().filter(p -> p.getA() == event.getInventory()).toList();
        if (!pair.isEmpty()) {
            InvHandler handler = pair.get(0).getB();
            if (handler != null && event.getInventory() == event.getView().getTopInventory()) {
                handler.onDrag(event);
            }
        }

         */
    }

    public void handleOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (this.invManager.get(player) == null) {return;}
        InvHandler handler = this.invManager.get(player).getB();
        if (handler != null) {
            handler.onOpen(event);
        }
        //if (this.invManager.get(player) == null) {return;}
        
        /*
        List<Pair<Inventory,InvHandler>> pair = this.invManager.values().stream().filter(p -> p.getA() == event.getInventory()).toList();
        if (!pair.isEmpty()) {
            InvHandler handler = pair.get(0).getB();
            if (handler != null) {
                handler.onOpen(event);
            }
        }

         */
    }

    public void handleClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (this.invManager.get(player) == null) {return;}
        InvHandler handler = this.invManager.get(player).getB();
        if (handler != null) {
            handler.onClose(event);
            this.unregisterInventory(player);
        }
        /*
        this.invManager.forEach((p,i) -> {
            if (i.getA() == event.getInventory()) {
                InvHandler handler = i.getB();
                if (handler != null) {
                    handler.onClose(event);
                    Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),()->this.unregisterInventory(p));
                }
            }
        });

         */
        /*
        List<Pair<Inventory,InvHandler>> pair = this.invManager.values().stream().filter(p -> p.getA() == event.getInventory()).toList();
        if (!pair.isEmpty()) {

            Player player = (Player) event.getInventory().getHolder();
            InvHandler handler = this.invManager.get(player).getB();
            if (handler != null) {
                handler.onClose(event);
                this.unregisterInventory(player);
            }
        }

         */
    }
}
