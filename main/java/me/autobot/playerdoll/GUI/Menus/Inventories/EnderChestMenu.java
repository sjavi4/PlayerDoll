package me.autobot.playerdoll.GUI.Menus.Inventories;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.GUI.Menus.Inventories.Packets.SetDollInventory;
import me.autobot.playerdoll.GUI.Menus.InventoryMenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnderChestMenu extends InventoryGUI {
    private Player doll;
    public EnderChestMenu(Player doll) {this.doll = doll;}
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, YAMLManager.getConfig("lang").getString("title.armor"));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), () -> {
            super.onClick(event);
            if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }
            if (event.getClickedInventory() == null) {
                PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), (Player) event.getWhoClicked());
            }

            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                event.setCancelled(false);
                int currentSlot = event.getSlot();
                SetDollInventory.UpdateDollEnderChest(doll, event.getCurrentItem(), currentSlot);
            }
        });
    }

/*
    @Override
    public void decorate(Player player) {
        List<Pair<ItemStack,Runnable>> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),new Pair<>(null,()->{})));

        ItemStack[] enderChest = doll.getEnderChest().getContents();
        for (int i = 0; i<list.size();i++) {
            list.set(i,new Pair<>(enderChest[i],()->{}));
        }
        for (int i = 0 ; i< list.size();i++) {
            this.addButton(i,this.createMainMenuButton(list.get(i)));
        }
        super.decorate(player);
    }
    private InventoryButton createMainMenuButton(Pair<ItemStack, Runnable> pair) {
        return new InventoryButton()
                .creator(player -> pair.getA())
                .consumer(event -> {
                    if (doll.isDead()) {
                        event.getInventory().getViewers().forEach(p -> Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), p::closeInventory));
                    }});
    }

 */
}