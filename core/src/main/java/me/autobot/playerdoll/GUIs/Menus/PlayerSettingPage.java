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
import org.bukkit.OfflinePlayer;
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

public class PlayerSettingPage extends DollInvHolder {
    private final Player doll;
    private final String target;
    private PermissionManager perm;
    private final YAMLManager dollConfig;
    private YamlConfiguration config;
    private DollConfigManager configManager;
    private Map<String, Object> flag;
    private OfflinePlayer targetPlayer;
    private final Map<Material, BiPredicate<Player, Boolean>> settingMap = new HashMap<>();
    private YamlConfiguration langFile = null;
    private YamlConfiguration flagFile = null;
    public PlayerSettingPage(String doll, String target) {
        String fullDollName = CommandType.getDollName(doll, true);
        String shortDollName = CommandType.getDollName(doll, false);
        this.doll = Bukkit.getPlayer(fullDollName);
        this.target = target;
        dollConfig = YAMLManager.loadConfig(fullDollName, false, false);
        if (this.doll == null) {
            if (dollConfig == null) {
                return;
            }
            config = dollConfig.getConfig();
            // Offline setting
        } else {
            configManager = DollConfigManager.getConfigManager(fullDollName);
            // Online setting
        }
        if (target == null) {
            // Gset
            inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("menuTitle.playerSetting","Everyone",shortDollName));
        } else {
            // Pset
            this.targetPlayer = Bukkit.getOfflinePlayer(target);
            inventory = Bukkit.createInventory(this, 54, LangFormatter.YAMLReplace("menuTitle.playerSetting",target,shortDollName));
        }
        this.perm = PermissionManager.getPermissionGroup(dollConfig.getConfig().getString("Owner.Perm"));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();

        String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("controlButton.hint"));

        langFile = ConfigManager.getLanguage();
        flagFile = ConfigManager.getFlag();

        Map<String, Boolean> toggleMap = perm.playerDefaultSettings;
        if (doll == null) {
            if (target == null) {
                if (config.contains("generalSetting")) {
                    config.getConfigurationSection("generalSetting").getValues(true).forEach((s, o) -> toggleMap.put(s, (boolean) o));
                }
                //toggleMap = config.getConfigurationSection("generalSetting").getValues(true);
            } else {
                if (config.contains("playerSetting."+targetPlayer.getUniqueId())) {
                    config.getConfigurationSection("playerSetting." + targetPlayer.getUniqueId()).getValues(true).forEach((s, o) -> toggleMap.put(s, (boolean) o));
                }
                //toggleMap = config.getConfigurationSection("playerSetting."+targetPlayer.getUniqueId()).getValues(true);
            }
        } else {
            if (target == null) {
                configManager.getGeneralSetting().forEach((s,o) -> toggleMap.put(s,(boolean)o));
            } else {
                configManager.getPlayerSetting(targetPlayer).forEach((s,o) -> toggleMap.put(s,(boolean)o));;
            }
        }

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

        flag = flagFile.getConfigurationSection("PersonalFlag").getValues(false);

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
                if (doll == null) {
                    if (target == null) {
                        config.set("generalSetting."+s,b);
                    } else {
                        config.set("playerSetting."+targetPlayer.getUniqueId()+"."+s,b);
                    }
                } else {
                    if (target == null) {
                        configManager.setGeneralSetting(s,b);
                    } else {
                        configManager.setPlayerSetting(targetPlayer.getUniqueId().toString(),s,b);
                    }
                }
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
        if (clickItem == null) return;
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
