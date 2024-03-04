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
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.BiPredicate;

public class PlayerSettingPage extends DollInvHolder {
    private final Player doll;
    //private PermissionManager perm;
    private final DollConfig dollConfig;
    private Map<String, Material> flags;
    private OfflinePlayer targetPlayer;
    private final Map<Material, BiPredicate<Player, Boolean>> settingMap = new HashMap<>();
    private YamlConfiguration langFile = null;
    public PlayerSettingPage(String doll, String target) {
        String fullDollName = CommandType.getDollName(doll, true);
        String shortDollName = CommandType.getDollName(doll, false);
        this.doll = Bukkit.getPlayer(fullDollName);
        if (this.doll == null) {
            this.dollConfig = DollConfig.getOfflineDollConfig(fullDollName);
        } else {
            this.dollConfig = DollConfig.getOnlineDollConfig(this.doll.getUniqueId());
        }
        //if (target == null) {
            // Gset
        //    inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("menuTitle.playerSetting","Everyone",shortDollName));
        //} else {
            // Pset
            this.targetPlayer = Bukkit.getOfflinePlayer(target);
            inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("menuTitle.playerSetting",target,shortDollName));
        //}
        //this.perm = PermissionManager.getPermissionGroup(dollConfig.getConfig().getString("Owner.Perm"));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();

        String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("controlButton.hint"));

        langFile = ConfigLoader.get().getConfig(ConfigLoader.ConfigType.CUSTOM_LANGUAGE);

        Map<String, Boolean> toggleMap = dollConfig.generalSetting;
        //Map<String, Boolean> playerToggleMap = dollConfig.playerSetting.get(targetPlayer.getUniqueId());

        if (dollConfig.playerSetting.containsKey(targetPlayer.getUniqueId())) {
            toggleMap = dollConfig.playerSetting.get(targetPlayer.getUniqueId());
        } else {
            LinkedHashMap<String, Boolean> playerToggleMap = new LinkedHashMap<>(toggleMap);
            dollConfig.playerSetting.put(targetPlayer.getUniqueId(), playerToggleMap);
            toggleMap = playerToggleMap;
        }
        final Map<String, Boolean> finalToggleMap = toggleMap;
        //toggleMap = config.getConfigurationSection("playerSetting."+targetPlayer.getUniqueId()).getValues(true);

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
            if (finalToggleMap.get(s) != null) {
                toggle = finalToggleMap.get(s);
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
                finalToggleMap.put(s,b);
                return true;
            });
            buttonMap.put(m,(p)->{});
        });
    }



    @Override
    public void onClose(InventoryCloseEvent event) {
        if (doll == null && event.getViewers().size() == 1) {
            dollConfig.saveConfig();
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
        if (clickItem == null) return;
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
