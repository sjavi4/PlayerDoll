package me.autobot.playerdoll.InvMenu.Menus.Inventories;

import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.InvMenu.Menus.Inventorymenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Dollenderchest extends InvInitializer {

    private final Map<ItemStack,Runnable> actionMap = new HashMap<>();
    public Dollenderchest(Player player, Player doll) {
        super(player,doll);
    }

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return doll.getEnderChest();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        event.setCancelled(false);
        if (event.getClickedInventory() == null) {
            this.getPlayer().closeInventory();
            PlayerDoll.getInvManager().openInv(new Inventorymenu(this.getPlayer(),this.getDoll()), this.getPlayer());
        }
    }

    @Override
    public void decorate(Player player,Player doll) {

        for (int i = 0 ; i< doll.getEnderChest().getSize();i++) {
            this.addButton(i,this.createMainMenuButton(doll.getEnderChest().getItem(i)));
            actionMap.put(doll.getEnderChest().getItem(i),()->{});
        }
        super.decorate(player,doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {});
    }

}
