package me.autobot.playerdoll.GUIs;

import jline.internal.Nullable;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface ButtonSetter {
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

    static ItemStack setItem(Material item, @Nullable Integer count, @Nullable String name, @Nullable List<String> lore) {
        if (item == null) {return new ItemStack(Material.AIR);}
        ItemStack itemStack = new ItemStack(item);
        if (count != null) {itemStack.setAmount(count);}
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if (name != null) {itemMeta.setDisplayName(name);}
        if (lore != null) {itemMeta.setLore(lore);}
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
