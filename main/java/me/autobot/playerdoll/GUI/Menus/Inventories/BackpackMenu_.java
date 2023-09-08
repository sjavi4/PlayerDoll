package me.autobot.playerdoll.GUI.Menus.Inventories;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.GUI.Menus.ButtonSetter;
import me.autobot.playerdoll.GUI.Menus.Inventories.Packets.SetDollInventory;
import me.autobot.playerdoll.GUI.Menus.InventoryMenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackpackMenu_ extends InventoryGUI {
    private Player doll;
    public BackpackMenu_(Player doll) {this.doll = doll;}
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 45 , YAMLManager.getConfig("lang").getString("title.backpack"));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null) {
            Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),() -> PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), (Player) event.getWhoClicked()));
        }
    }

    @Override
    public void decorate(Player player) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);

        List<ItemStack> backpack = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),null));
        //5678
        for (int i = 0; i < 4; i++) {
            backpack.set(i+5,defaultslot);
            backpack.set(i, doll.getInventory().getArmorContents()[3-i]);
        }
        backpack.set(4, doll.getInventory().getItemInOffHand());
        for (int i = 0; i < 9; i++) {
            backpack.set(9+i,doll.getInventory().getContents()[i+9]);
            backpack.set(18+i,doll.getInventory().getContents()[i+18]);
            backpack.set(27+i,doll.getInventory().getContents()[i+27]);
            backpack.set(36+i,doll.getInventory().getContents()[i]);
        }
        for (int i = 0 ; i< backpack.size();i++) {
            this.addButton(i,this.createMainMenuButton(backpack.get(i)));
        }
        super.decorate(player);
    }
    private InventoryButton createMainMenuButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }
}
