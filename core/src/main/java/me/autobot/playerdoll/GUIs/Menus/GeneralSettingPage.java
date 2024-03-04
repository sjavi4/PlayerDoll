package me.autobot.playerdoll.GUIs.Menus;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.GUIs.ButtonSetter;
import me.autobot.playerdoll.GUIs.DollInvHolder;
import me.autobot.playerdoll.Util.ConfigLoader;
import me.autobot.playerdoll.Util.Configs.FlagConfig;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class GeneralSettingPage extends DollInvHolder {
    private final Player doll;
    private final String fullDollName;
    private final String shortDollName;
    private DollConfig dollConfig;
    private Map<String, Material> flags;
    private final Map<Material, BiPredicate<Player, Boolean>> settingMap = new HashMap<>();
    private YamlConfiguration langFile = null;
    public GeneralSettingPage(String dollName) {
        this.doll = Bukkit.getPlayer(dollName);
        this.fullDollName = CommandType.getDollName(dollName, true);
        this.shortDollName = CommandType.getDollName(dollName, false);

        if (this.doll == null) {
            this.dollConfig = DollConfig.getOfflineDollConfig(fullDollName);
        } else {
            this.dollConfig = DollConfig.getOnlineDollConfig(this.doll.getUniqueId());
        }
        

        //this.perm = PermissionManager.getPermissionGroup(this.dollConfig.getConfig().getString("Owner.Perm"));
        inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("menuTitle.playerSetting","Everyone",shortDollName));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();

        String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("controlButton.hint"));

        langFile = ConfigLoader.get().getConfig(ConfigLoader.ConfigType.CUSTOM_LANGUAGE);

        Map<String, Boolean> toggleMap = dollConfig.generalSetting;

        //Page function wait until necessary
        /*
        ItemStack slot3 = ButtonSetter.setItem(Material.TORCH,null, LangFormatter.YAMLReplace("controlButton.prev"), null);
        inventory.setSlot(3,slot3);
        buttonMap.put(slot3.getType(),(b)-> {
        });
        ItemStack slot4 = ButtonSetter.setItem(Material.REDSTONE_TORCH,null, LangFormatter.YAMLReplace("controlButton.curr"), null);
        inventory.setSlot(4,slot4);
        buttonMap.put(slot4.getType(),(b)-> {
        });
        ItemStack slot5 = ButtonSetter.setItem(Material.SOUL_TORCH,null, LangFormatter.YAMLReplace("controlButton.next"), null);
        inventory.setSlot(5,slot5);
        buttonMap.put(slot5.getType(),(b)-> {
        });

         */

        flags = FlagConfig.PERSONAL_FLAG_MAP;

        int[] counter = {18};
        flags.forEach((s, m) -> {
            boolean toggle = false;
            if (toggleMap.get(s) != null) {
                toggle = toggleMap.get(s);
            }
            String buttonName = langFile.getString("settingmenu."+s+".name");
            List<String> lure = new ArrayList<>();
            lure.add(LangFormatter.YAMLReplace("settingmenu."+s+".desc"));
            lure.addAll(List.of(desc));
            ItemStack item = ButtonSetter.setItem(m, null, getToggle(toggle) + buttonName, lure);

            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.getDisplayName().startsWith("§a")) {
                itemMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
            }
            item.setItemMeta(itemMeta);

            inventory.setItem(counter[0],item);
            counter[0]++;
            settingMap.put(m, (p, b) -> {
                String perm = "playerdoll.personalflag." + s;
                if (!p.hasPermission(perm)) {
                    return false;
                }
                /*
                if (!perm.playerAvailableFlags.get(s) && !p.isOp()) {
                    return false;
                }

                 */
                dollConfig.generalSetting.put(s, b);
                return true;
            });
            buttonMap.put(m,(p)->{});
        });
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (doll == null && event.getViewers().size() == 1) {
            this.dollConfig.saveConfig();
        }
    }
    /*
    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (!langFile.equals(ConfigLoader.get().getConfig(ConfigLoader.ConfigType.CUSTOM_LANGUAGE)) || !flagFile.equals(ConfigManager.getFlag())) {
            setupInventoryItem();
        }
    }

     */
    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            return;
        }
        ItemStack clickItem = event.getCurrentItem();
        Material clickMaterial = clickItem.getType();
        ItemMeta clickMeta = clickItem.getItemMeta();
        flags.forEach((s, m) -> {
            if (clickMaterial != m) {
                return;
            }
            boolean isLeftClick = event.isLeftClick();
            boolean validAction = settingMap.get(clickMaterial).test((Player) event.getWhoClicked(),isLeftClick);
            if (!validAction) {
                return;
            }
            if (isLeftClick) {
                clickMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
                clickMeta.setDisplayName(ChatColor.GREEN + ChatColor.stripColor(clickMeta.getDisplayName()));
                clickItem.setItemMeta(clickMeta);
            } else {
                if (clickItem.getItemMeta().hasEnchants()) {
                    clickMeta.removeEnchant(Enchantment.MULTISHOT);
                }
                clickMeta.setDisplayName(ChatColor.RED + ChatColor.stripColor(clickMeta.getDisplayName()));
                clickItem.setItemMeta(clickMeta);
            }
        });
    }

    private ChatColor getToggle(boolean b) {
        return b?ChatColor.GREEN : ChatColor.RED;
    }
}
