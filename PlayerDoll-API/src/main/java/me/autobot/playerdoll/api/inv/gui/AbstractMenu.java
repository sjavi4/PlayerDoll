package me.autobot.playerdoll.api.inv.gui;

import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.ItemSetter;
import me.autobot.playerdoll.api.inv.MenuBase;
import me.autobot.playerdoll.api.inv.button.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractMenu extends MenuBase {

    protected Inventory inventory;
    protected final DollMenuHolder dollMenuHolder;

    protected static final String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("control-button.hint"));
    public static final ItemStack EMPTY_ITEM = ItemSetter.setItem(Material.GRAY_STAINED_GLASS_PANE, ActionButton.NONE, 1," ",null);
    public final Map<InvButton, Consumer<Player>> buttonMap = new HashMap<>();
    public AbstractMenu(Doll doll, DollMenuHolder holder) {
        dollMenuHolder = holder;
        buttonMap.put(ActionButton.NONE, player -> {});
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void initialGUIContent() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, EMPTY_ITEM);
        }
    }
    public abstract void updateGUIContent();
    public abstract void onClickOutside(Player player);

    public boolean onToggle(Player whoClicked, boolean leftClick, ItemStack item, ItemMeta itemMeta , InvButton button) {
        if (button instanceof PersonalFlagButton personalFlagType) {
            if (!whoClicked.hasPermission(personalFlagType.getPermission())) {
                return false;
            }
        } else if (button instanceof GlobalFlagButton globalFlagType) {
            if (!whoClicked.hasPermission(globalFlagType.getPermission())) {
                return false;
            }
        }

        if (leftClick) {
            itemMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
            itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.stripColor(itemMeta.getDisplayName()));
            item.setItemMeta(itemMeta);
        } else {
            if (item.getItemMeta().hasEnchants()) {
                itemMeta.removeEnchant(Enchantment.MULTISHOT);
            }
            itemMeta.setDisplayName(ChatColor.RED + ChatColor.stripColor(itemMeta.getDisplayName()));
            item.setItemMeta(itemMeta);
        }
        return true;
    }

    @Override
    public void open(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        // 1 for first opening
        if (inventory.getViewers().size() == 1) {
            // to become 1
//            dollGUIHolder.menus.put(type, this);
            initialGUIContent();
            return;
        }
        updateGUIContent();
    }

    @Override
    public void click(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            onClickOutside(player);
            return;
        }
        if (disableBottomInventory() && event.getClickedInventory() == event.getView().getBottomInventory()) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (!pdc.has(ItemSetter.ITEM_KEY, new PDCButtonType())) {
            return;
        }
        InvButton action = pdc.get(ItemSetter.ITEM_KEY, new PDCButtonType());
        if (action == null) {
            return;
        }
        buttonMap.get(action).accept(player);
        if (action.isToggleable()) {
            onToggle(player, event.isLeftClick(), item, itemMeta, action);
        }
    }

    public boolean disableBottomInventory() {
        return true;
    }

    @Override
    public void drag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
    protected ChatColor getToggle(boolean b) {
        return b ? ChatColor.GREEN : ChatColor.RED;
    }

    protected InvButton getPDC(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (!pdc.has(ItemSetter.ITEM_KEY, new PDCButtonType())) {
            return null;
        }
        return pdc.get(ItemSetter.ITEM_KEY, new PDCButtonType());
    }
}
