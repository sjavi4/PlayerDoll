package me.autobot.playerdoll.gui.menu;

import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.gui.ItemSetter;
import me.autobot.playerdoll.persistantdatatype.ButtonAction;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class DollDataMenu extends AbstractMenu {
    private final Player dollPlayer;
    public DollDataMenu(Doll doll) {
        super(doll, DollGUIHolder.MenuType.DATA);
        this.dollPlayer = doll.getBukkitPlayer();
        inventory = Bukkit.createInventory(this, 9, LangFormatter.YAMLReplace("inv-name.data",dollPlayer.getName()));
    }

    @Override
    public void updateGUIContent() {
        inventory.setItem(6, updateLevel());
    }

    @Override
    public void onClickOutside(Player player) {

    }

    @Override
    public void initialGUIContent() {
        super.initialGUIContent();

        ItemStack slot0 = ItemSetter.setItem(Material.RESPAWN_ANCHOR, ButtonAction.RETURN, 1, LangFormatter.YAMLReplace("control-button.back"),null);
        inventory.setItem(0,slot0);
        buttonMap.put(getPDC(slot0), (player) -> {
            String commandMenu = String.format("/playerdoll:doll menu %s", dollPlayer.getName());
            player.chat(commandMenu);
        });

        ItemStack slot2 = ItemSetter.setItem(Material.OAK_CHEST_BOAT, ButtonAction.OPEN_BACKPACK, 1, LangFormatter.YAMLReplace("inv-menu.inv"),null);
        inventory.setItem(2,slot2);
        buttonMap.put(getPDC(slot2), (player) -> {
            String commandBackpack = String.format("/playerdoll:doll inv %s", dollPlayer.getName());
            player.chat(commandBackpack);
        });
        ItemStack slot3 = ItemSetter.setItem(Material.SHULKER_BOX,ButtonAction.OPEN_ENDER_CHEST, 1, LangFormatter.YAMLReplace("inv-menu.echest"),null);
        ItemSetter.setShulkerBoxPreview(slot3, dollPlayer.getEnderChest().getContents());
        inventory.setItem(3, slot3);
        buttonMap.put(getPDC(slot3), (player) -> {
            String commandEnderChest = String.format("/playerdoll:doll echest %s", dollPlayer.getName());
            player.chat(commandEnderChest);
        });

        ItemStack slot6 = updateLevel();
        inventory.setItem(6,slot6);
        buttonMap.put(getPDC(slot6), (player)-> {
            String commandEXP = String.format("/playerdoll:doll exp %s", dollPlayer.getName());
            player.chat(commandEXP);
            inventory.setItem(6, updateLevel());
        });
    }
    private ItemStack updateLevel() {
        return ItemSetter.setItem(Material.EXPERIENCE_BOTTLE,
                ButtonAction.GET_EXP,
                1,
                LangFormatter.YAMLReplace("inv-menu.level"),
                Arrays.asList(
                        LangFormatter.YAMLReplace("inv-menu.level-display", dollPlayer.getLevel()),
                        LangFormatter.YAMLReplace("inv-menu.level-get")
                )
        );
    }
}
