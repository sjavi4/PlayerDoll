package me.autobot.playerdoll.api.inv.gui;

import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.ItemSetter;
import me.autobot.playerdoll.api.inv.button.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class DollBackpackMenu extends AbstractMenu {
    private final Player dollPlayer;
    private final PlayerInventory dollInventory;
    private long lastInteractTick = -1;

    public DollBackpackMenu(Doll doll, DollMenuHolder holder) {
        super(doll, holder);
        this.dollPlayer = doll.getBukkitPlayer();
        this.dollInventory = dollPlayer.getInventory();
        inventory = Bukkit.createInventory(dollPlayer, 45, LangFormatter.YAMLReplace("inv-name.backpack",dollPlayer.getName()));
    }

    @Override
    public void initialGUIContent() {
        super.initialGUIContent();
        updateGUIContent();
    }

    @Override
    public void updateGUIContent() {
        inventory.setContents(updateInventory());
    }

    @Override
    public void onClickOutside(Player player) {
        player.openInventory(dollMenuHolder.inventoryStorage.get(DollDataMenu.class).get(0).inventory);
    }

//    @Override
//    public void onOpen(InventoryOpenEvent event) {
//        super.onOpen(event);
//        //inventory.setContents(updateInventory());
//    }

    @Override
    public void click(InventoryClickEvent event) {
        super.click(event);
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        if (event.getCurrentItem() == EMPTY_ITEM) {
            return;
        }
        Player whoClicked = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            onClickOutside(whoClicked);
            return;
        }
        if (event.getSlot() == 8) {
            // Real backpack
            whoClicked.openInventory(dollInventory);
        }

        if (event.getSlot() < 5 || event.getSlot() > 8) {
            long currentTickCount = whoClicked.getWorld().getGameTime();
            if (currentTickCount - lastInteractTick >= 0 && currentTickCount - lastInteractTick <= 5) {
                return;
            }
            lastInteractTick = currentTickCount;
            // TODO: OverStacked Items
            switch (event.getClick()) {
                case LEFT,RIGHT -> mouseClick(event);
                case SHIFT_LEFT -> shiftSwap(event);
                case NUMBER_KEY -> numKeySwap(event);
                case SWAP_OFFHAND -> handSwap(event);
                case DROP,CONTROL_DROP -> dropSlot(event);
            }
        }
        PlayerDollAPI.getScheduler().entityTaskDelayed(this::updateGUIContent, dollPlayer, 4);
    }

    private ItemStack[] updateInventory() {
        ItemStack[] items = inventory.getContents();
        ItemStack[] dollInvItems = dollInventory.getContents();
        items[0] = dollInventory.getArmorContents()[3]; // Helmet
        items[1] = dollInventory.getArmorContents()[2]; // Chest plate
        items[2] = dollInventory.getArmorContents()[1]; // Leggings
        items[3] = dollInventory.getArmorContents()[0]; // Boots

        items[4] = dollInventory.getItemInOffHand();
        for (int i = 0; i < 9; i++) {
            items[9+i] = dollInvItems[i+9]; // inv row1
            items[18+i] = dollInvItems[i+18]; // inv row2
            items[27+i] = dollInvItems[i+27]; // inv row3
            items[36+i] = dollInvItems[i]; // hot bar
        }
        items[8] = ItemSetter.setItem(Material.CHEST, ActionButton.NONE, 1, LangFormatter.YAMLReplace("inv-menu.real-inv"), null);
        return items;
    }

    private void mouseClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.isLeftClick() && event.getSlot() >= 36) {
            String commandSlot = String.format("/playerdoll:doll slot %s %d", DollNameUtil.dollShortName(dollPlayer.getName()), event.getSlot() - 35);
            player.chat(commandSlot);
        }

        ItemStack currItem = event.getCurrentItem();
        if (currItem == null || event.getSlot() < 36) {
            return;
        }
        if (event.isRightClick() && !currItem.getType().isBlock()) {
            String commandSlot = String.format("/playerdoll:doll slot %s %d", DollNameUtil.dollShortName(dollPlayer.getName()), event.getSlot() - 35);
            player.chat(commandSlot);
            String commandUse = String.format("/playerdoll:doll use %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandUse);
            //new Use(player, fullDollName, null);
            ItemStack item = inventory.getItem(event.getSlot());
            if (item != null) {
                if (item.getAmount() == 1) {
                    inventory.setItem(event.getSlot(),null);
                } else {
                    item.setAmount(item.getAmount()-1);
                    inventory.setItem(event.getSlot(), item);
                }
                inventory.setItem(0, dollInventory.getHelmet());
                inventory.setItem(1, dollInventory.getChestplate());
                inventory.setItem(2, dollInventory.getLeggings());
                inventory.setItem(3, dollInventory.getBoots());
            }
        }
    }

    private void shiftSwap(InventoryClickEvent event) {
        int clickedSlot = event.getSlot();

        if (clickedSlot >= 36) { // Hot-bar to backpack
            hotbarToBackpackSwap(clickedSlot);
            return;
        }
        if (clickedSlot >= 9) { // Backpack to hot-bar
            backpackToHotbarSwap(clickedSlot);
            return;
        }
        if (event.getSlot() < 5) { // Equipment and offhand to hot-bar
            for (int i = 0; i < 9; i++) {
                if (dollInventory.getItem(i) != null) {
                    continue;
                }
                ItemStack position = switch (event.getSlot()) {
                    case 0 -> {
                        var item = dollInventory.getHelmet();
                        dollInventory.setHelmet(null);
                        yield item;
                    }
                    case 1 -> {
                        var item = dollInventory.getChestplate();
                        dollInventory.setChestplate(null);
                        yield item;
                    }
                    case 2 -> {
                        var item = dollInventory.getLeggings();
                        dollInventory.setLeggings(null);
                        yield item;
                    }
                    case 3 -> {
                        var item = dollInventory.getBoots();
                        dollInventory.setBoots(null);
                        yield item;
                    }
                    case 4 -> {
                        var item = dollInventory.getItemInOffHand();
                        dollInventory.setItemInOffHand(null);
                        yield item;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + event.getSlot());
                };
                dollInventory.setItem(i, position);
                break;
            }
        }

    }

    private void numKeySwap(InventoryClickEvent event) {
        int hotbar = event.getHotbarButton();
        // Swap offHand
        if (event.getSlot() == 4) {
            ItemStack select = dollInventory.getItem(event.getHotbarButton());
            dollInventory.setItem(event.getHotbarButton(), dollInventory.getItemInOffHand());
            dollInventory.setItemInOffHand(select);
        } else {
            if (event.getSlot() >= 36) { // hot-bar
                ItemStack select = dollInventory.getItem(event.getSlot()-36);
                dollInventory.setItem(event.getSlot()-36, dollInventory.getItem(hotbar));
                dollInventory.setItem(hotbar, select);
            } else { // backpack
                ItemStack select = dollInventory.getItem(event.getSlot());
                dollInventory.setItem(event.getSlot(), dollInventory.getItem(hotbar));
                dollInventory.setItem(hotbar, select);
            }
        }
    }

    private void handSwap(InventoryClickEvent event) {
        if (event.getSlot() >= 9) {
            ItemStack offhand = dollInventory.getItemInOffHand();
            int slot = event.getSlot();
            if (event.getSlot() >= 36) {
                slot = event.getSlot() - 36;
            }
            dollInventory.setItemInOffHand(dollInventory.getItem(slot));
            dollInventory.setItem(slot, offhand);
        }
    }

    private void dropSlot(InventoryClickEvent event) {
        int pos = event.getSlot();
        String slot = Integer.toString(pos+1);
        switch (event.getSlot()) {
            case 0 -> slot = "helmet";
            case 1 -> slot = "chestplate";
            case 2 -> slot = "leggings";
            case 3 -> slot = "boots";
            case 4 -> slot = "offhand";
        }
        if (pos >= 36) {
            slot = Integer.toString(pos - 36);
        } else if (pos >= 9) {
            slot = Integer.toString(pos + 1);
        }
        boolean dropStack = event.getClick() == ClickType.CONTROL_DROP;
        String arg = dropStack ? "dropStack" : "drop";
        String commandDrop = String.format("/playerdoll:doll %s %s %s", arg, DollNameUtil.dollShortName(dollPlayer.getName()), slot);
        ((Player) event.getWhoClicked()).chat(commandDrop);
        //new Drop((Player) event.getWhoClicked(),fullDollName,new String[]{all,slot});
        ItemStack item = inventory.getItem(event.getSlot());
        if (item != null) {
            if (item.getAmount() == 1 || dropStack) {
                inventory.setItem(pos,null);
            } else {
                item.setAmount(item.getAmount()-1);
                inventory.setItem(pos, item);
            }
        }
    }

    private void hotbarToBackpackSwap(int clickedSlot) {
        int actualClickedSlot = clickedSlot - 36; // 0 - 9 Hotbar slots
        ItemStack targetItem = dollInventory.getItem(actualClickedSlot);
        if (targetItem == null) {
            return;
        }
        int targetItemAmount = targetItem.getAmount();
        // 9 - 36 Backpack slots
        for (int i = 9; i < 36; i++) {
            ItemStack indexedItem = dollInventory.getItem(i);
            // Empty Backpack Slot -> Immediately swap
            if (indexedItem == null) {
                dollInventory.setItem(i, targetItem);
                dollInventory.setItem(actualClickedSlot, null);
                return;
            }
            // Partial Backpack Slot Empty -> Iterate all similar item and add amount
            if (indexedItem.getAmount() == indexedItem.getMaxStackSize()) {
                continue;
            }
            if (indexedItem.isSimilar(targetItem)) {
                int sum = indexedItem.getAmount() + targetItemAmount;
                int min = Math.min(sum, indexedItem.getMaxStackSize());
                targetItem.setAmount(min);
                dollInventory.setItem(i, targetItem);
                targetItemAmount = sum - min;
                if (targetItemAmount > 0) {
                    indexedItem.setAmount(targetItemAmount);
                    dollInventory.setItem(actualClickedSlot, indexedItem);
                    continue;
                }
                dollInventory.setItem(actualClickedSlot, null);
                return;
            }
        }
    }
    private void backpackToHotbarSwap(int clickedSlot) {
        ItemStack targetItem = dollInventory.getItem(clickedSlot);
        if (targetItem == null) {
            return;
        }
        int targetItemAmount = targetItem.getAmount();
        // 0 - 9 Hotbar slots
        for (int i = 0; i < 9; i++) {
            ItemStack indexedItem = dollInventory.getItem(i);
            // Empty Horbar Slot -> Immediately swap
            if (indexedItem == null) {
                dollInventory.setItem(i, targetItem);
                dollInventory.setItem(clickedSlot, null);
                return;
            }
            // Partial Hotbar Slot Empty -> Iterate all similar item and add amount
            if (indexedItem.getAmount() == indexedItem.getMaxStackSize()) {
                continue;
            }
            if (indexedItem.isSimilar(targetItem)) {
                int sum = indexedItem.getAmount() + targetItemAmount;
                int min = Math.min(sum, indexedItem.getMaxStackSize());
                targetItem.setAmount(min);
                dollInventory.setItem(i, targetItem);
                targetItemAmount = sum - min;
                if (targetItemAmount > 0) {
                    indexedItem.setAmount(targetItemAmount);
                    dollInventory.setItem(clickedSlot, indexedItem);
                    continue;
                }
                dollInventory.setItem(clickedSlot, null);
                return;
            }
        }
    }
}
