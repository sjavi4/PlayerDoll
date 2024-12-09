package me.autobot.playerdoll.api.inv.gui;

import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.ItemSetter;
import me.autobot.playerdoll.api.inv.button.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DollDataMenu extends AbstractMenu {
    private final Player dollPlayer;
    public DollDataMenu(Doll doll, DollMenuHolder holder) {
        super(doll, holder);
        this.dollPlayer = doll.getBukkitPlayer();
        inventory = Bukkit.createInventory(dollPlayer, 9, LangFormatter.YAMLReplace("inv-name.data",dollPlayer.getName()));
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

        ItemStack slot0 = ItemSetter.setItem(Material.RESPAWN_ANCHOR, ActionButton.RETURN, 1, LangFormatter.YAMLReplace("control-button.back"),null);
        inventory.setItem(0,slot0);
        buttonMap.put(getPDC(slot0), (player) -> {
            String commandMenu = String.format("/playerdoll:doll menu %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandMenu);
        });

        ItemStack slot2 = ItemSetter.setItem(Material.OAK_CHEST_BOAT, ActionButton.OPEN_BACKPACK, 1, LangFormatter.YAMLReplace("inv-menu.inv"),null);
        inventory.setItem(2,slot2);
        buttonMap.put(getPDC(slot2), (player) -> {
            String commandBackpack = String.format("/playerdoll:doll inv %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandBackpack);
        });
        ItemStack slot3 = ItemSetter.setItem(Material.SHULKER_BOX,ActionButton.OPEN_ENDER_CHEST, 1, LangFormatter.YAMLReplace("inv-menu.echest"),null);
        ItemSetter.setShulkerBoxPreview(slot3, dollPlayer.getEnderChest().getContents());
        inventory.setItem(3, slot3);
        buttonMap.put(getPDC(slot3), (player) -> {
            String commandEnderChest = String.format("/playerdoll:doll echest %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandEnderChest);
        });

        ItemStack slot6 = updateLevel();
        inventory.setItem(6,slot6);
        // Further in click event
        buttonMap.put(getPDC(slot6), (player -> {}));
    }

    @Override
    public void click(InventoryClickEvent event) {
        super.click(event);
        if (getPDC(event.getCurrentItem()) instanceof ActionButton actionButton) {
            if (actionButton != ActionButton.GET_EXP) {
                return;
            }
            String commandEXP = String.format("/playerdoll:doll exp %s %s", DollNameUtil.dollShortName(dollPlayer.getName()), event.isShiftClick() ? "all" : 1);
            if (event.isRightClick()) {
                commandEXP = commandEXP.concat(" asOrb");
            }
            ((Player) event.getWhoClicked()).chat(commandEXP);
            updateGUIContent();
        }
    }

    private ItemStack updateLevel() {
        List<String> s = new ArrayList<>();
        s.add(LangFormatter.YAMLReplace("inv-menu.level-display", dollPlayer.getLevel()));
        s.addAll(Arrays.asList(LangFormatter.splitter(LangFormatter.YAMLReplace("inv-menu.level-get"))));
        return ItemSetter.setItem(Material.EXPERIENCE_BOTTLE,
                ActionButton.GET_EXP,
                1,
                LangFormatter.YAMLReplace("inv-menu.level"),
                s
        );
    }
}
