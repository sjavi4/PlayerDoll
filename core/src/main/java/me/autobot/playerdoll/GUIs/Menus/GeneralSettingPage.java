package me.autobot.playerdoll.GUIs.Menus;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.GUIs.ButtonSetter;
import me.autobot.playerdoll.GUIs.DollInvHolder;
import me.autobot.playerdoll.Util.ConfigManager;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermissionManager;
import me.autobot.playerdoll.YAMLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
    private PermissionManager perm;
    private YAMLManager dollConfig;
    private Map<String, Object> flag;
    private final Map<Material, BiPredicate<Player, Boolean>> settingMap = new HashMap<>();
    private YamlConfiguration langFile = null;
    private YamlConfiguration flagFile = null;
    public GeneralSettingPage(String doll) {
        this.doll = Bukkit.getPlayer(doll);
        this.fullDollName = CommandType.getDollName(doll, true);
        this.shortDollName = CommandType.getDollName(doll, false);

        this.dollConfig = YAMLManager.loadConfig(fullDollName,false,false);
        if (this.dollConfig == null) {
            return;
        }
        

        this.perm = PermissionManager.getPermissionGroup(this.dollConfig.getConfig().getString("Owner.Perm"));
        inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("menuTitle.playerSetting","Everyone",shortDollName));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();

        String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("controlButton.hint"));

        langFile = ConfigManager.getLanguage();
        flagFile = ConfigManager.getFlag();

        DollConfigManager configManager = DollConfigManager.getConfigManager(doll);
        Map<String, Object> toggleMap = configManager.getDollSetting();

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

        flag = flagFile.getConfigurationSection("GlobalFlag").getValues(false);

        int[] counter = {18};
        flag.forEach((s,o) -> {
            Material m = Material.valueOf((String) o);
            boolean toggle = (boolean) toggleMap.get(s);
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
                if (!perm.playerAvailableFlags.get(s) && !p.isOp()) {
                    return false;
                }
                configManager.setGeneralSetting(s, b);
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
    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (!langFile.equals(ConfigManager.getLanguage()) || !flagFile.equals(ConfigManager.getFlag())) {
            setupInventoryItem();
        }
    }
    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            return;
        }
        ItemStack clickItem = event.getCurrentItem();
        Material clickMaterial = clickItem.getType();
        ItemMeta clickMeta = clickItem.getItemMeta();
        flag.forEach((s,m) -> {
            if (!clickMaterial.toString().equalsIgnoreCase((String)m)) {
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
