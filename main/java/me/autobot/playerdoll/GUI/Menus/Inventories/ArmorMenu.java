package me.autobot.playerdoll.GUI.Menus.Inventories;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.GUI.Menus.Inventories.Packets.SetDollInventory;
import me.autobot.playerdoll.GUI.Menus.InventoryMenu;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.util.*;

public class ArmorMenu extends InventoryGUI {
    private Player doll;

    public ArmorMenu(Player doll) {
        this.doll = doll;
    }
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, InventoryType.HOPPER, YAMLManager.getConfig("lang").getString("title.armor"));
    }
    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        ItemStack[] armors = new ItemStack[4];
        System.arraycopy(event.getInventory().getContents(),0,armors,0,3);
        ArrayUtils.reverse(armors);

        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> {
            super.onClick(event);
            Map<Integer, EquipmentSlot> slotTypes = new HashMap<>();
            slotTypes.put(0,EquipmentSlot.HEAD);
            slotTypes.put(1,EquipmentSlot.CHEST);
            slotTypes.put(2,EquipmentSlot.LEGS);
            slotTypes.put(3,EquipmentSlot.FEET);
            slotTypes.put(4,EquipmentSlot.OFFHAND);
            if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }

            if (event.getClickedInventory() == null) {
                PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), (Player) event.getWhoClicked());
            }


            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                if (event.getSlot() == 4) {
                    if (currentItem != doll.getInventory().getItemInOffHand()) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> PlayerDoll.getGuiManager().openGUI(new ArmorMenu(this.doll), (Player) event.getWhoClicked()));
                    }
                } else {
                    if (armors[event.getSlot()] != doll.getInventory().getArmorContents()[3 - event.getSlot()]) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> PlayerDoll.getGuiManager().openGUI(new ArmorMenu(this.doll), (Player) event.getWhoClicked()));
                    }
                }
                /*
                event.setCancelled(true);

                EquipmentSlot eSlot = slotTypes.get(event.getSlot());
                EnchantmentTarget target = null;
                if (event.getSlot() != 4) {
                    target = EnchantmentTarget.values()[eSlot.ordinal()];
                }
                if (event.getCursor().getType() == Material.AIR || target == null || target.includes(event.getCursor())) {
                    event.setCancelled(false);
                    int currentSlot = event.getSlot();
                    SetDollInventory.UpdateDollEquiptment(doll, event.getCurrentItem(), slotTypes.get(currentSlot));
                }

                 */
                event.setCancelled(false);
                int currentSlot = event.getSlot();
                SetDollInventory.UpdateDollEquiptment(doll, event.getCurrentItem(), slotTypes.get(currentSlot));
            }
        });

    }

    @Override
    public void decorate(Player player) {
        //List<ItemStack> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),null));
        ItemStack[] armor = new ItemStack[5];
        System.arraycopy(doll.getInventory().getArmorContents(),0,armor,1,4);
        ArrayUtils.reverse(armor);
        armor[4] = doll.getInventory().getItemInOffHand();
        //list.set(0, armor[0]);
        //list.set(1, armor[1]);
        //list.set(2, armor[2]);
        //list.set(3, armor[3]);
        //list.set(4, doll.getInventory().getItemInOffHand());

        for (int i = 0 ; i< armor.length;i++) {
            this.addButton(i,this.createMainMenuButton(armor[i]));
        }
        super.decorate(player);
    }
    private InventoryButton createMainMenuButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }

}