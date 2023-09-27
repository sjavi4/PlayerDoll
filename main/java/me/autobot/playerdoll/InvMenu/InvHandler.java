package me.autobot.playerdoll.InvMenu;

import org.bukkit.event.inventory.*;

public interface InvHandler {
    void onClick(InventoryClickEvent event);

    void onCreative(InventoryCreativeEvent event);

    void onDrag(InventoryDragEvent event);

    void onOpen(InventoryOpenEvent event);

    void onClose(InventoryCloseEvent event);
}
