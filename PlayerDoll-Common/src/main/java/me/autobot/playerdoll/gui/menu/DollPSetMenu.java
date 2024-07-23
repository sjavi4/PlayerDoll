package me.autobot.playerdoll.gui.menu;

import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.gui.ItemSetter;
import me.autobot.playerdoll.persistantdatatype.Button;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class DollPSetMenu extends AbstractMenu {
    private final Player dollPlayer;
    private final DollConfig dollConfig;
    private final OfflinePlayer targetPlayer;
    public DollPSetMenu(Doll doll, OfflinePlayer targetPlayer) {
        super(doll, DollGUIHolder.MenuType.PSETTING);
        this.dollPlayer = doll.getBukkitPlayer();
        this.targetPlayer = targetPlayer;
        dollConfig = DollConfig.getOnlineDollConfig(dollPlayer.getUniqueId());
        inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("inv-name.pset", targetPlayer.getName(), dollPlayer.getName()));
    }

    @Override
    public void updateGUIContent() {

    }

    @Override
    public void onClickOutside(Player player) {

    }

    @Override
    public boolean onToggle(Player whoClicked, boolean leftClick, ItemStack item, ItemMeta itemMeta, Button button) {
        if (!super.onToggle(whoClicked, leftClick, item, itemMeta, button)) {
            return false;
        }

        FlagConfig.PersonalFlagType flagType = (FlagConfig.PersonalFlagType) getPDC(item);

        String commandSet = String.format("/playerdoll:doll pset %s %s %s %b", DollManager.dollShortName(dollPlayer.getName()), targetPlayer.getName(), flagType.getCommand().toLowerCase(), leftClick);
        whoClicked.chat(commandSet);
        return true;
    }

    @Override
    public void initialGUIContent() {
        super.initialGUIContent();
        EnumMap<FlagConfig.PersonalFlagType, Boolean> flagEnums = dollConfig.playerSetting.get(targetPlayer.getUniqueId());
        FlagConfig.PersonalFlagType[] personalFlagTypes = FlagConfig.PersonalFlagType.values();
        for (int i = 0; i < Math.min(inventory.getSize(), personalFlagTypes.length); i++) {

            FlagConfig.PersonalFlagType flagType = personalFlagTypes[i];
            boolean toggleState = flagEnums.get(personalFlagTypes[i]);

            String name = getToggle(toggleState) + LangFormatter.YAMLReplace("set-menu." + flagType.getCommand().toLowerCase() + ".name");

            List<String> lure = new ArrayList<>(Arrays.asList(desc));
            lure.add(LangFormatter.YAMLReplace("set-menu." + flagType.getCommand().toLowerCase() + ".desc"));

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

    @Override
    public void onClose(InventoryCloseEvent event) {
        //super.onClose(event);
    }
}
