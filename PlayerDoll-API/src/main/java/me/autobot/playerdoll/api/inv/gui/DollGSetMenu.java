package me.autobot.playerdoll.api.inv.gui;

import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.ItemSetter;
import me.autobot.playerdoll.api.inv.button.ActionButton;
import me.autobot.playerdoll.api.inv.button.InvButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DollGSetMenu extends AbstractMenu {
    private final Player dollPlayer;
    private final DollConfig dollConfig;
    private final int currentPage;
    public DollGSetMenu(Doll doll, DollMenuHolder holder, int currentPage) {
        super(doll, holder);
        this.currentPage = currentPage;
        this.dollPlayer = doll.getBukkitPlayer();
        dollConfig = DollConfig.getOnlineConfig(dollPlayer.getUniqueId());
        inventory = Bukkit.createInventory(dollPlayer, 54, LangFormatter.YAMLReplace("inv-name.gset",dollPlayer.getName()));
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void updateGUIContent() {

    }

    @Override
    public void onClickOutside(Player player) {
        player.openInventory(dollMenuHolder.inventoryStorage.get(DollInfoMenu.class).get(0).inventory);
    }

    @Override
    public boolean onToggle(Player whoClicked, boolean leftClick, ItemStack item, ItemMeta itemMeta, InvButton button) {
        if (!super.onToggle(whoClicked, leftClick, item, itemMeta, button)) {
            return false;
        }

        PersonalFlagButton flagType = (PersonalFlagButton) getPDC(item);

        String commandSet = String.format("/playerdoll:doll gset %s %s %b", DollNameUtil.dollShortName(dollPlayer.getName()), flagType.registerName().toLowerCase(), leftClick);
        whoClicked.chat(commandSet);
        return true;
    }

    @Override
    public void initialGUIContent() {
        super.initialGUIContent();

        var list = dollMenuHolder.inventoryStorage.get(DollGSetMenu.class);

        ItemStack slot45 = ItemSetter.setItem(Material.ECHO_SHARD, ActionButton.PREVIOUS_PAGE, 1, LangFormatter.YAMLReplace("control-button.prev"),null);
        inventory.setItem(45,slot45);
        buttonMap.put(getPDC(slot45), (player) -> {
            player.openInventory(list.get((currentPage - 1 + list.size()) % list.size()).inventory);
        });

        ItemStack slot49 = ItemSetter.setItem(Material.PAPER, ActionButton.PAGE_DISPLAY, currentPage+1, LangFormatter.YAMLReplace("control-button.curr", currentPage+1, list.size()),null);
        inventory.setItem(49,slot49);
        buttonMap.put(getPDC(slot49), (player) -> {});

        ItemStack slot53 = ItemSetter.setItem(Material.AMETHYST_SHARD, ActionButton.NEXT_PAGE, 1, LangFormatter.YAMLReplace("control-button.next"),null);
        inventory.setItem(53,slot53);
        buttonMap.put(getPDC(slot53), (player) -> {
            player.openInventory(list.get((currentPage + 1) % list.size()).inventory);
        });

        var personalFlagButtons = PersonalFlagButton.getButtons().values().stream()
                .filter(b -> b instanceof PersonalFlagButton)
                .map(b -> (PersonalFlagButton)b)
                .toList();

        int start = 36 * currentPage;
        int end = Math.min(start + 36, personalFlagButtons.size());
        for (int i = 0; i < end - start; i++) {

            PersonalFlagButton flagType = personalFlagButtons.get(start + i);
            boolean toggleState = dollConfig.generalSetting.get(flagType);

            String name = getToggle(toggleState) + LangFormatter.YAMLReplace("set-menu." + flagType.registerName().toLowerCase() + ".name");

            List<String> lure = new ArrayList<>(Arrays.asList(desc));
            lure.add(LangFormatter.YAMLReplace("set-menu." + flagType.registerName().toLowerCase() + ".desc"));

            ItemStack itemStack = ItemSetter.setItem(flagType.getMaterial(), flagType, 1, name, lure);

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (toggleState) {
                itemMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
                itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.stripColor(itemMeta.getDisplayName()));
                itemStack.setItemMeta(itemMeta);
            } else {
                if (itemStack.getItemMeta().hasEnchants()) {
                    itemMeta.removeEnchant(Enchantment.MULTISHOT);
                }
                itemMeta.setDisplayName(ChatColor.RED + ChatColor.stripColor(itemMeta.getDisplayName()));
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(i, itemStack);
            // process further in onToggle()
            buttonMap.put(flagType, player -> {});
        }
    }
}
