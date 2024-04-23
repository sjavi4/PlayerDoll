package me.autobot.playerdoll.GUIs.Menus;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.Command.SubCommands.Drop;
import me.autobot.playerdoll.Command.SubCommands.Slot;
import me.autobot.playerdoll.Command.SubCommands.Use;
import me.autobot.playerdoll.GUIs.ButtonSetter;
import me.autobot.playerdoll.GUIs.DollInvHolder;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BackpackInventory extends DollInvHolder {
    private final Player doll;
    private final String fullDollName;
    private final String shortDollName;
    private final PlayerInventory dollInv;
    public BackpackInventory(Player doll) {
        this.doll = doll;
        this.fullDollName = CommandType.getDollName(doll.getName(),true);
        this.shortDollName = CommandType.getDollName(doll.getName(),false);
        this.dollInv = doll.getInventory();
        inventory = Bukkit.createInventory(this,45, LangFormatter.YAMLReplace("menuTitle.backpack",shortDollName));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();
        inventory.setContents(updateInventory());
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);
        inventory.setContents(updateInventory());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        //if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem() == EMPTY_ITEM) {
            return;
        }
        Player whoClicked = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) {
            whoClicked.openInventory(PlayerDoll.dollInvStorage.get(fullDollName).getInventoryPage());
        }
        if (event.getSlot() == 8) {
            // Real backpack
            whoClicked.openInventory(dollInv);
        }

        if (event.getSlot() < 5 || event.getSlot() > 8) {
            switch (event.getClick()) {
                case LEFT,RIGHT -> mouseClick(event);
                case SHIFT_LEFT -> shiftSwap(event);
                case NUMBER_KEY -> numKeySwap(event);
                case SWAP_OFFHAND -> handSwap(event);
                case DROP,CONTROL_DROP -> dropSlot(event);
            }
        }
        Runnable task = () -> inventory.setContents(updateInventory());
        PlayerDoll.getScheduler().entityTask(doll, task, 4);
        /*
        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().entityTask(doll, task, 4);
            //FoliaSupport.entityTask(doll,task,4);
        } else {
            Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),task,4);
        }

         */

    }

    private ItemStack[] updateInventory() {
        ItemStack[] items = inventory.getContents();
        ItemStack[] dollInvItems = dollInv.getContents();
        for (int i = 0; i < 4; i++) {
            //items[i+5] = super.EMPTY_ITEM;
            //buttons.set(i+5,defaultslot);
            items[i] = dollInv.getArmorContents()[3-i];
            //buttons.set(i, doll.getInventory().getArmorContents()[3-i]);
        }
        items[4] = dollInv.getItemInOffHand();
        //buttons.set(4, doll.getInventory().getItemInOffHand());
        for (int i = 0; i < 9; i++) {
            items[9+i] = dollInvItems[i+9];
            items[18+i] = dollInvItems[i+18];
            items[27+i] = dollInvItems[i+27];
            items[36+i] = dollInvItems[i];
            //buttons.set(9+i,doll.getInventory().getContents()[i+9]); //inv row1
            //buttons.set(18+i,doll.getInventory().getContents()[i+18]); //inv row2
            //buttons.set(27+i,doll.getInventory().getContents()[i+27]);  //inv row3
            //buttons.set(36+i,doll.getInventory().getContents()[i]); //hotbar
        }
        items[8] = ButtonSetter.setItem(Material.CHEST,null,LangFormatter.YAMLReplace("inventorymenu.actualinv"), null);
        return items;
    }

    private void mouseClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        new Slot(player,fullDollName, new String[]{Integer.toString(event.getSlot()-35)});
        ItemStack currItem = event.getCurrentItem();
        if (currItem == null) {
            return;
        }
        if (event.getSlot() < 36) return;
        if (event.isRightClick() && !currItem.getType().isBlock()) {
            new Use(player, fullDollName, null);
            ItemStack item = inventory.getItem(event.getSlot());
            if (item != null) {
                if (item.getAmount() == 1) {
                    inventory.setItem(event.getSlot(),null);
                } else {
                    item.setAmount(item.getAmount()-1);
                    inventory.setItem(event.getSlot(),item);
                }
                inventory.setItem(0,dollInv.getHelmet());
                inventory.setItem(1,dollInv.getChestplate());
                inventory.setItem(2,dollInv.getLeggings());
                inventory.setItem(3,dollInv.getBoots());
            }
        }
    }

    private void shiftSwap(InventoryClickEvent event) {
        if (event.getSlot() >= 36) { // Hot-bar to backpack
            for (int i = 9; i < 36; i++) {
                if (dollInv.getItem(i) != null) {
                    continue;
                }
                dollInv.setItem(i, dollInv.getItem(event.getSlot()-36));
                dollInv.setItem(event.getSlot()-36, null);
                break;
            }
        } else if ((event.getSlot() >= 9 && event.getSlot() < 36)) { // Backpack to hot-bar
            for (int j = 0; j < 9; j++) {
                if (dollInv.getItem(j) != null && dollInv.getItem(j).getType() != Material.GRAY_STAINED_GLASS_PANE) {
                    continue;
                }
                //ItemStack item = inv.getItem(j);
                dollInv.setItem(j, dollInv.getItem(event.getSlot()));
                dollInv.setItem(event.getSlot(), null);
                break;
            }
        } else if (event.getSlot() < 5) { // Equipment and offhand to hot-bar
            for (int i = 0; i < 9; i++) {
                if (dollInv.getItem(i) != null) {
                    continue;
                }
                ItemStack position = switch (event.getSlot()) {
                    case 0 -> {
                        var item = dollInv.getHelmet();
                        dollInv.setHelmet(null);
                        yield item;
                    }
                    case 1 -> {
                        var item = dollInv.getChestplate();
                        dollInv.setChestplate(null);
                        yield item;
                    }
                    case 2 -> {
                        var item = dollInv.getLeggings();
                        dollInv.setLeggings(null);
                        yield item;
                    }
                    case 3 -> {
                        var item = dollInv.getBoots();
                        dollInv.setBoots(null);
                        yield item;
                    }
                    case 4 -> {
                        var item = dollInv.getItemInOffHand();
                        dollInv.setItemInOffHand(null);
                        yield item;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + event.getSlot());
                };
                dollInv.setItem(i, position);
                break;
            }
        }

    }

    private void numKeySwap(InventoryClickEvent event) {
        int hotbar = event.getHotbarButton();
        // Swap offHand
        if (event.getSlot() == 4) {
            ItemStack select = dollInv.getItem(event.getHotbarButton());
            dollInv.setItem(event.getHotbarButton(), dollInv.getItemInOffHand());
            dollInv.setItemInOffHand(select);
        } else {
            if (event.getSlot() >= 36) { // hot-bar
                ItemStack select = dollInv.getItem(event.getSlot()-36);
                dollInv.setItem(event.getSlot()-36, dollInv.getItem(hotbar));
                dollInv.setItem(hotbar, select);
            } else { // backpack
                ItemStack select = dollInv.getItem(event.getSlot());
                dollInv.setItem(event.getSlot(), dollInv.getItem(hotbar));
                dollInv.setItem(hotbar, select);
            }
        }
    }

    private void handSwap(InventoryClickEvent event) {
        if (event.getSlot() >= 9) {
            ItemStack offhand = dollInv.getItemInOffHand();
            int slot = event.getSlot();
            if (event.getSlot() >= 36) {
                slot = event.getSlot() - 36;
            }
            dollInv.setItemInOffHand(dollInv.getItem(slot));
            dollInv.setItem(slot, offhand);
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
            slot = Integer.toString(pos - 35);
        } else if (pos >= 9) {
            slot = Integer.toString(pos+1);
        }
        boolean dropStack = event.getClick() == ClickType.CONTROL_DROP;
        String all = dropStack? "stack" : "single";
        new Drop((Player) event.getWhoClicked(),fullDollName,new String[]{all,slot});
        ItemStack item = inventory.getItem(event.getSlot());
        if (item != null) {
            if (item.getAmount() == 1 || dropStack) {
                inventory.setItem(event.getSlot(),null);
            } else {
                item.setAmount(item.getAmount()-1);
                inventory.setItem(event.getSlot(),item);
            }
        }
    }
}
