package me.autobot.playerdoll.GUIs.Menus;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.GUIs.DollInvHolder;
import me.autobot.playerdoll.GUIs.ButtonSetter;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InventoryPage extends DollInvHolder {
    private final Player doll;

    public InventoryPage(Player doll) {
        this.doll = doll;
        String fullDollName = CommandType.getDollName(doll.getName(), true);
        String shortDollName = CommandType.getDollName(doll.getName(), false);

        inventory = Bukkit.createInventory(this,9, LangFormatter.YAMLReplace("menuTitle.inventory", new Pair<>("%a%", shortDollName)));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();

        ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null, LangFormatter.YAMLReplace("controlButton.back"),null);
        inventory.setItem(0,slot0);
        buttonMap.put(slot0.getType(),(player) -> {
            player.chat("/doll menu "+doll.getName());
            //new Menu(player,fullDollName);
        });

        ItemStack slot2 = ButtonSetter.setItem(Material.OAK_CHEST_BOAT,null, LangFormatter.YAMLReplace("inventorymenu.inventory"),null);
        inventory.setItem(2,slot2);
        buttonMap.put(slot2.getType(),(player) -> {
            player.chat("/doll inv "+doll.getName());
            //new Inv(player,doll.getName());
        });
        ItemStack slot3 = ButtonSetter.setItem(Material.SHULKER_BOX,null,LangFormatter.YAMLReplace("inventorymenu.enderchest"),null);
        ButtonSetter.setShulkerBoxPreview(slot3, doll.getEnderChest().getContents());
        inventory.setItem(3, slot3);
        buttonMap.put(slot3.getType(), (player) -> {
            player.chat("/doll echest "+doll.getName());
            //new Echest(player,doll.getName());
        });

        ItemStack slot6 = updateLevel();
        inventory.setItem(6,slot6);
        buttonMap.put(slot6.getType(), (player)-> {
            player.chat("/doll exp "+doll.getName());
            //new Exp(player,doll.getName(),null);
            inventory.setItem(6, updateLevel());
        });
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);
        inventory.setItem(6, updateLevel());
    }
    private ItemStack updateLevel() {
        return ButtonSetter.setItem(Material.EXPERIENCE_BOTTLE,
                null,
                LangFormatter.YAMLReplace("inventorymenu.levelUp"),
                Arrays.asList(LangFormatter.YAMLReplace("inventorymenu.level",
                        new Pair<>("%a%", Integer.toString(doll.getLevel()))),
                        LangFormatter.YAMLReplace("inventorymenu.levelGet")));
    }
}
