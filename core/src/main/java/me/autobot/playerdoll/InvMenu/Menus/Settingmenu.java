package me.autobot.playerdoll.InvMenu.Menus;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.ButtonSetter;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Consumer;

public class Settingmenu extends InvInitializer {
    private final Map<Material, Consumer<Boolean>> actionMap = new HashMap<>();
    public Settingmenu(Player player, Player doll) {
        super(player,doll);
    }

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return Bukkit.createInventory(null, 54, LangFormatter.YAMLReplace("menuTitle.setting",'&',new Pair<>( "%a%",doll.getName())));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getSlot() < 18) {
            return;
        }
        boolean onoff = event.isLeftClick();
        ItemStack clickItem = event.getCurrentItem();
        if (clickItem == null) return;
        ItemMeta clickMeta = clickItem.getItemMeta();
        if (clickItem.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        if (onoff) {
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

    }

    @Override
    public void decorate(Player player,Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        actionMap.put(defaultslot.getType(),(b)->{});

        //List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),defaultslot));
        List<ItemStack> buttons = new ArrayList<>();

        String[] desc = LangFormatter.splitter(LangFormatter.YAMLReplace("controlButton.hint",'&'));

        YamlConfiguration langFile = ConfigManager.getLanguage();
        YamlConfiguration flagFile = ConfigManager.getFlag();

        String name = doll.getName();

        DollConfigManager configManager = PlayerDoll.dollManagerMap.get(name).getConfigManager();
        Map<String, Object> toggleMap = configManager.getDollSetting();

        for (int i = 0; i < 18; i++) buttons.add(defaultslot);

        ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null, LangFormatter.YAMLReplace("controlButton.back",'&'), null);
        buttons.set(0,slot0);
        actionMap.put(slot0.getType(),(b)-> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Mainmenu(player, doll), player);
        });
        //Page function wait until necessary
        /*
        ItemStack slot3 = ButtonSetter.setItem(Material.TORCH,null, LangFormatter.YAMLReplace("controlButton.prev",'&'), null);
        buttons.set(3,slot3);
        actionMap.put(slot3.getType(),(b)-> {
        });
        ItemStack slot4 = ButtonSetter.setItem(Material.REDSTONE_TORCH,null, LangFormatter.YAMLReplace("controlButton.curr",'&'), null);
        buttons.set(4,slot4);
        actionMap.put(slot4.getType(),(b)-> {
        });
        ItemStack slot5 = ButtonSetter.setItem(Material.SOUL_TORCH,null, LangFormatter.YAMLReplace("controlButton.next",'&'), null);
        buttons.set(5,slot5);
        actionMap.put(slot5.getType(),(b)-> {
        });

         */
        Map<String, Object> flag = flagFile.getConfigurationSection("GlobalFlag").getValues(false);

        PermissionManager perm = PermissionManager.getInstance(YAMLManager.loadConfig(doll.getName(), false).getConfig().getString("Owner.Perm"));
        if (!player.isOp()) {
            perm.flagGlobalDisplays.forEach((s, b) -> {
                if (!b) flag.remove(s);
            });
        }


        //List<String> flags = flagFile.getConfigurationSection("GlobalFlag").getValues(true).values().stream().sorted().toList();
        flag.forEach( (s,o) -> {
            Material m = Material.valueOf((String) o);
            boolean toggle = (boolean) toggleMap.get(s);
            String buttonName = langFile.getString("settingmenu."+s+".name");
            List<String> lure = new ArrayList<>();
            lure.add(LangFormatter.YAMLReplace("settingmenu."+s+".desc", '&'));
            lure.addAll(List.of(desc));
            ItemStack item = ButtonSetter.setItem(m, null, getToggle(toggle) + buttonName, lure);
            buttons.add(item);
            actionMap.put(item.getType(), (b) -> configManager.setDollSetting(s,b));
        });
        /*
        for (String s : flags) {
            Material m = Material.valueOf(flagFile.getString("GlobalFlags." + s + ".Item"));
            boolean toggle = (boolean) toggleMap.get(s);
            String buttonName = langFile.getString("settingmenu."+s+".name");
            List<String> lure = new ArrayList<>();
            lure.add(LangFormatter.YAMLReplace("settingmenu."+s+".desc", '&'));
            lure.addAll(List.of(desc));
            ItemStack item = ButtonSetter.setItem(m, null, getToggle(toggle) + buttonName, lure);
            buttons.add(item);
            actionMap.put(item.getType(), (b) -> configManager.setDollSetting(s,b));
        }

         */
        while (buttons.size() < this.getInventory().getSize()) buttons.add(defaultslot);

        for (int i = 0 ; i< buttons.size();i++) {
            ItemStack item = buttons.get(i);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.getDisplayName().startsWith("§a")) {
                itemMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
            }
            item.setItemMeta(itemMeta);
            this.addButton(i,this.createMainMenuButton(buttons.get(i),buttons.get(i).getType()));
        }
        super.decorate(player,doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack, Material material) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {
                    Runnable task = ()-> {
                        if (actionMap.get(material) != null) {
                            actionMap.get(material).accept(event.isLeftClick());
                        }
                    };
                    if (PlayerDoll.isFolia) FoliaSupport.globalTask(task);
                    else Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),task,0);
                });
    }
    private ChatColor getToggle(boolean b) {
        return b?ChatColor.GREEN : ChatColor.RED;
    }

}