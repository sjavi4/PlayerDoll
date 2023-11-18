package me.autobot.playerdoll.InvMenu;

import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public interface InvHandler {
    void onClick(InventoryClickEvent event);

    void onCreative(InventoryCreativeEvent event);

    void onDrag(InventoryDragEvent event);
    void onSwapHand(PlayerSwapHandItemsEvent event);

    void onOpen(InventoryOpenEvent event);

    void onClose(InventoryCloseEvent event);
}
