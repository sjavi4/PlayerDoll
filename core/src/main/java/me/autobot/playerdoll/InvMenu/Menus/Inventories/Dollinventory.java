package me.autobot.playerdoll.InvMenu.Menus.Inventories;


import me.autobot.playerdoll.Command.SubCommands.Drop;
import me.autobot.playerdoll.Command.SubCommands.Slot;
import me.autobot.playerdoll.Command.SubCommands.Use;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.ButtonSetter;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.InvMenu.Menus.Inventorymenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import oshi.util.tuples.Pair;

import java.util.*;

public class Dollinventory extends InvInitializer {

    //private final Map<ItemStack,Runnable> actionMap = new HashMap<>();

    private final Map<Integer,Runnable> actions = new HashMap<>();

    public Dollinventory(Player player, Player doll) {
        super(player,doll);
    }
    private boolean once = false;

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return Bukkit.createInventory(null, 45, LangFormatter.YAMLReplace("menuTitle.backpack",'&',new Pair<>( "%a%",doll.getName())));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        event.setCancelled(true);
        if (event.getClickedInventory() == null) {
            event.getWhoClicked().closeInventory();
            PlayerDoll.getInvManager().openInv(new Inventorymenu(this.getPlayer(),this.getDoll()), this.getPlayer());
        }
        if (once) {return;}
        if (event.getSlot() < 5 || event.getSlot() > 8) {
            once = true;
            actions.put(event.getSlot(), () -> {
                switch (event.getClick()) {
                    // Set Current Slot in Hot-bar
                    case LEFT, RIGHT -> {
                        if (event.getSlot() >= 36) {
                            new Slot((Player) event.getWhoClicked(),this.getDoll().getName(),new String[]{Integer.toString(event.getSlot()-35)});
                            //new slot().perform(this.getPlayer(), this.getDoll().getName().substring(PlayerDoll.getDollPrefix().length()), new String[]{"", Integer.toString(event.getSlot()-35)});
                            // Try To Wear clicked Item
                            if (event.getClick() == ClickType.RIGHT && !event.getCurrentItem().getType().isBlock()) {
                                new Use((Player) event.getWhoClicked(),this.getDoll().getName(),null);
                                //new use().perform(this.getPlayer(), this.getDoll().getName().substring(PlayerDoll.getDollPrefix().length()), new String[]{});
                            }
                        }
                    }

                    // Shift Swap
                    case SHIFT_LEFT -> {
                        PlayerInventory inv = this.getDoll().getInventory();
                        if (event.getSlot() >= 36) {
                            for (int j = 9; j < 36; j++) {
                                if (inv.getItem(j) != null && inv.getItem(j).getType() != Material.GRAY_STAINED_GLASS_PANE) {
                                    continue;
                                }
                                //ItemStack item = inv.getItem(j);
                                inv.setItem(j, this.getDoll().getInventory().getItem(event.getSlot()-36));
                                inv.setItem(event.getSlot()-36, null);
                            }
                        } else if ((event.getSlot() >= 9 && event.getSlot() < 36)) {
                            for (int j = 0; j < 9; j++) {
                                if (inv.getItem(j) != null && inv.getItem(j).getType() != Material.GRAY_STAINED_GLASS_PANE) {
                                    continue;
                                }
                                //ItemStack item = inv.getItem(j);
                                inv.setItem(j, this.getDoll().getInventory().getItem(event.getSlot()));
                                inv.setItem(event.getSlot(), null);
                            }
                        } else if (event.getSlot() < 5) {
                            for (int j = 0; j < 9; j++) {
                                if (inv.getItem(j) != null && inv.getItem(j).getType() != Material.GRAY_STAINED_GLASS_PANE) {
                                    continue;
                                }
                                switch (event.getSlot()) {
                                    case 0 -> {
                                        inv.setItem(j,inv.getHelmet());
                                        inv.setHelmet(null);
                                    }
                                    case 1 -> {
                                        inv.setItem(j,inv.getChestplate());
                                        inv.setChestplate(null);
                                    }
                                    case 2 -> {
                                        inv.setItem(j,inv.getLeggings());
                                        inv.setLeggings(null);
                                    }
                                    case 3 -> {
                                        inv.setItem(j,inv.getBoots());
                                        inv.setBoots(null);
                                    }
                                    case 4 -> {
                                        inv.setItem(j,inv.getItemInOffHand());
                                        inv.setItemInOffHand(null);
                                    }
                                }
                            }
                        }
                    }
                    // Hotkey Swap
                    case NUMBER_KEY -> {
                        PlayerInventory inv = this.getDoll().getInventory();
                        int hotbar = event.getHotbarButton();
                        if (event.getSlot() == 4) {
                            ItemStack select = inv.getItem(event.getHotbarButton());
                            inv.setItem(event.getHotbarButton(), inv.getItemInOffHand());
                            inv.setItemInOffHand(select);
                        } else {
                            if (event.getSlot() >= 36) {
                                ItemStack select = inv.getItem(event.getSlot()-36);
                                inv.setItem(event.getSlot()-36, inv.getItem(hotbar));
                                inv.setItem(hotbar, select);
                            } else {
                                ItemStack select = inv.getItem(event.getSlot());
                                inv.setItem(event.getSlot(), inv.getItem(hotbar));
                                inv.setItem(hotbar, select);
                            }
                        }
                    }
                    case SWAP_OFFHAND -> {
                        if (event.getSlot() >= 9) {
                            PlayerInventory inv = this.getDoll().getInventory();
                            ItemStack offhand = inv.getItemInOffHand();
                            int slot = event.getSlot();
                            if (event.getSlot() >= 36) {
                                slot = event.getSlot() - 36;
                            }
                            inv.setItemInOffHand(inv.getItem(slot));
                            inv.setItem(slot, offhand);
                        }
                    }
                    case DROP , CONTROL_DROP -> {
                        String slot = Integer.toString(event.getSlot()+1);
                        switch (event.getSlot()) {
                            case 0 -> slot = "helmet";
                            case 1 -> slot = "chestplate";
                            case 2 -> slot = "leggings";
                            case 3 -> slot = "boots";
                            case 4 -> slot = "offhand";
                        }
                        if (event.getSlot() >= 36) {
                            slot = Integer.toString(event.getSlot() - 35);
                        }
                        String all = event.getClick() == ClickType.CONTROL_DROP? "stack" : "";
                        new Drop((Player) event.getWhoClicked(),this.getDoll().getName(),new String[]{slot,all});
                        //new drop().perform(this.getPlayer(), this.getDoll().getName().substring(PlayerDoll.getDollPrefix().length()), new String[]{"", slot, all});
                    }
                }
            });
        }
    }

    @Override
    public void decorate(Player player, Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),null));
        //5678
        for (int i = 0; i < 4; i++) {
            buttons.set(i+5,defaultslot);
            buttons.set(i, doll.getInventory().getArmorContents()[3-i]);
        }
        buttons.set(4, doll.getInventory().getItemInOffHand());
        for (int i = 0; i < 9; i++) {
            buttons.set(9+i,doll.getInventory().getContents()[i+9]); //inv row1
            buttons.set(18+i,doll.getInventory().getContents()[i+18]); //inv row2
            buttons.set(27+i,doll.getInventory().getContents()[i+27]);  //inv row3
            buttons.set(36+i,doll.getInventory().getContents()[i]); //hotbar
        }
        for (int i = 0 ; i< buttons.size();i++) {
            if (buttons.get(i) == null || buttons.get(i).getType().isAir()) {
                buttons.set(i, defaultslot);
            }
            this.addButton(i,this.createMainMenuButton(buttons.get(i)));
        }
        super.decorate(player, doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {
                    final boolean finalOnce = once;
                    if (!finalOnce) {
                        Runnable task = () -> {
                            actions.get(event.getSlot()).run();
                            this.decorate(this.getPlayer(), this.getDoll());
                            once = false;
                        };
                        if (PlayerDoll.isFolia) FoliaSupport.entityTask(getDoll(),task,1);
                        else Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), task, 1);
                    }
                });
    }

}
