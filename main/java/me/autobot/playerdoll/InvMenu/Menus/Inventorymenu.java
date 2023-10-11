package me.autobot.playerdoll.InvMenu.Menus;

import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.ButtonSetter;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.InvMenu.Menus.Inventories.Dollenderchest;
import me.autobot.playerdoll.InvMenu.Menus.Inventories.Dollinventory;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Inventorymenu extends InvInitializer {

    private final Map<ItemStack,Runnable> actionMap = new HashMap<>();
    public Inventorymenu(Player player, Player doll) {
        super(player,doll);
    }

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return Bukkit.createInventory(null, 9, TranslateFormatter.stringConvert("menuTitle.inventory",'&',"%a%",doll.getName()));
    }

    @Override
    public void decorate(Player player,Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        actionMap.put(defaultslot,()->{});

        List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),defaultslot));

        ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null,TranslateFormatter.stringConvert("controlButton.back",'&'),null);
        buttons.set(0,slot0);
        actionMap.put(slot0,() -> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Mainmenu(player, doll), player);
        });

        ItemStack slot2 = ButtonSetter.setItem(Material.OAK_CHEST_BOAT,null,TranslateFormatter.stringConvert("inventorymenu.inventory",'&'),null);
        buttons.set(2,slot2);
        actionMap.put(slot2,() -> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Dollinventory(player, doll), player);
        });

        if ((Boolean) PlayerDoll.dollManagerMap.get(doll.getName()).configManager.getData().get("setting.Ender Chest")) {
            ItemStack slot3 = ButtonSetter.setItem(Material.SHULKER_BOX,null,TranslateFormatter.stringConvert("inventorymenu.enderchest", '&'),null);
            ButtonSetter.setShulkerBoxPreview(slot3, doll.getEnderChest().getContents());
            buttons.set(3, slot3);
            actionMap.put(slot3, () -> {
                player.closeInventory();
                PlayerDoll.getInvManager().openInv(new Dollenderchest(player, doll), player);
            });
        }

        ItemStack slot6 = ButtonSetter.setItem(Material.EXPERIENCE_BOTTLE,null,TranslateFormatter.stringConvert("inventorymenu.levelUp",'&')
                , Arrays.asList(TranslateFormatter.stringConvert("inventorymenu.level",'&',"%a%"
                        , Integer.toString(doll.getLevel())), TranslateFormatter.stringConvert("inventorymenu.levelGet",'&')));
        buttons.set(6,slot6);
        actionMap.put(slot6, ()-> {
            if (doll.getLevel() <= 0) {return;}
            float sumPoints = doll.getExp() * doll.getExpToLevel() + player.getExp() * player.getExpToLevel();
            doll.setExp(0);
            player.setExp(0);

            doll.setLevel(doll.getLevel() - 1);
            sumPoints += doll.getExpToLevel();

            while (sumPoints >= player.getExpToLevel()) {
                sumPoints -= player.getExpToLevel();
                player.setLevel(player.getLevel()+1);
            }
            if (sumPoints > 0) {
                player.setExp(sumPoints/player.getExpToLevel());
            }
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Inventorymenu(player, doll), player);
        });

        for (int i = 0 ; i< buttons.size();i++) {
            this.addButton(i,this.createMainMenuButton(buttons.get(i)));
        }
        super.decorate(player,doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),actionMap.get(itemStack),0);
                });
    }

}
