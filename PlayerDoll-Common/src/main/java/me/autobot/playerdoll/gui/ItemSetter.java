package me.autobot.playerdoll.gui;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.persistantdatatype.Button;
import me.autobot.playerdoll.persistantdatatype.ButtonType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface ItemSetter {
    NamespacedKey ITEM_KEY = new NamespacedKey(PlayerDoll.PLUGIN, "item_action");
    static ItemStack setItem(Material item, Button action, Integer count, String name, List<String> lore) {
        if (item == null) {
            return new ItemStack(Material.AIR);
        }
        ItemStack itemStack = new ItemStack(item);

        if (count != null) {
            itemStack.setAmount(count);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemMeta.getPersistentDataContainer().set(ITEM_KEY, new ButtonType(), action);


        if (name != null) {
            itemMeta.setDisplayName(name);
        }
        if (lore != null) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static ItemStack setShulkerBoxPreview(ItemStack shulkerbox, ItemStack[] previewItems) {
        BlockStateMeta bsm = (BlockStateMeta) shulkerbox.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
        Inventory shulkerInv = shulker.getInventory();
        shulkerInv.setContents(previewItems);
        shulker.update();
        bsm.setBlockState(shulker);
        shulkerbox.setItemMeta(bsm);

        return shulkerbox;
    }
}
