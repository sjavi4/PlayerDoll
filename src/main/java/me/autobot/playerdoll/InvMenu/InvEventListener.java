package me.autobot.playerdoll.InvMenu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class InvEventListener implements Listener {
    private final InvManager invManager;

    public InvEventListener(InvManager invManager) {
        this.invManager = invManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        this.invManager.handleClick(event);
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent event) {
        this.invManager.handleCreative(event);
    }
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        this.invManager.handleDrag(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        this.invManager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.invManager.handleClose(event);
    }

}