package me.autobot.playerdoll.gui.menu;

import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.gui.ItemSetter;
import me.autobot.playerdoll.persistantdatatype.Button;
import me.autobot.playerdoll.persistantdatatype.ButtonAction;
import me.autobot.playerdoll.persistantdatatype.ButtonType;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractMenu implements Menu {

    protected Inventory inventory;
    protected final DollGUIHolder.MenuType type;
    protected final DollGUIHolder dollGUIHolder;

    protected static final String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("control-button.hint"));
    public static final ItemStack EMPTY_ITEM = ItemSetter.setItem(Material.GRAY_STAINED_GLASS_PANE, ButtonAction.NONE, 1," ",null);
    public final Map<Button, Consumer<Player>> buttonMap = new HashMap<>();
    public AbstractMenu(Doll doll, DollGUIHolder.MenuType type) {
        this.type = type;
        buttonMap.put(ButtonAction.NONE, player -> {});
        this.dollGUIHolder = DollGUIHolder.DOLL_GUI_HOLDERS.get(doll.getBukkitPlayer().getUniqueId());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public DollGUIHolder.MenuType getMenuType() {
        return type;
    }

    public void initialGUIContent() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, EMPTY_ITEM);
        }
    }
    public abstract void updateGUIContent();
    public abstract void onClickOutside(Player player);

    public boolean onToggle(Player whoClicked, boolean leftClick, ItemStack item, ItemMeta itemMeta , Button button) {
        if (button instanceof FlagConfig.PersonalFlagType personalFlagType) {
            if (!whoClicked.hasPermission(personalFlagType.getPermission())) {
                return false;
            }
        } else if (button instanceof FlagConfig.GlobalFlagType globalFlagType) {
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
    public void onClose(InventoryCloseEvent event) {
//        Inventory inventory = event.getInventory();
//        if (inventory.getViewers().size() == 1) {
//            // to become 0
//            dollGUIHolder.menus.put(type, null);
//        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
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
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            onClickOutside(player);
            return;
        }
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
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
        if (!pdc.has(ItemSetter.ITEM_KEY, new ButtonType())) {
            return;
        }
        Button action = pdc.get(ItemSetter.ITEM_KEY, new ButtonType());
        if (action == null) {
            return;
        }
        buttonMap.get(action).accept(player);
        if (action.isToggleable()) {
            onToggle(player, event.isLeftClick(), item, itemMeta, action);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
    protected ChatColor getToggle(boolean b) {
        return b ? ChatColor.GREEN : ChatColor.RED;
    }

    protected Button getPDC(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (!pdc.has(ItemSetter.ITEM_KEY, new ButtonType())) {
            return null;
        }
        return pdc.get(ItemSetter.ITEM_KEY, new ButtonType());
    }
}
