package me.autobot.playerdoll.GUI.Menus;

import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.server.level.TicketType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.util.*;

public class SettingMenu extends InventoryGUI {
    private Player doll;
    private boolean onoff;
    private final Map<Material,Runnable> settings = new HashMap<>();

    public SettingMenu(Player doll) {this.doll = doll;}
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, YAMLManager.getConfig("lang").getString("title.setting"));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
            if (event.getSlot() >= 18) {
                onoff = event.isLeftClick();
                ItemStack clickItem = event.getCurrentItem();
                ItemMeta clickMeta = clickItem.getItemMeta();
                if (clickItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {return;}
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
                settings.get(clickItem.getType()).run();
            }
        }
    }

    @Override
    public void decorate(Player player) {
        Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),()-> {
            DollManager serverPlayerDoll = (DollManager) ((CraftPlayer)doll).getHandle();
            ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
            List<Pair<ItemStack,Runnable>> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),new Pair<>(defaultslot,()->{})));

            String desca = TranslateFormatter.stringConvert("control.hinta",'&');
            String descb = TranslateFormatter.stringConvert("control.hintb",'&');

            YamlConfiguration langFile = YAMLManager.getConfig("lang");

            ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null, TranslateFormatter.stringConvert("control.back",'&'),null);
            list.set(0,new Pair<>(slot0,()-> PlayerDoll.getGuiManager().openGUI(new MainMenu(this.doll), player)));

            Map<String,Object> slot18Lang = langFile.getConfigurationSection("settingmenu.Inventory").getValues(true);
            ItemStack slot18 = ButtonSetter.setItem(Material.CHEST_MINECART,null,getToggle(serverPlayerDoll.enableInventory) + slot18Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot18Lang.get("desc").toString(),'&'),desca,descb));
            list.set(18, new Pair<>(slot18,() -> {}));
            settings.put(slot18.getType(),()->serverPlayerDoll.enableInventory = onoff);

            Map<String,Object> slot19Lang = langFile.getConfigurationSection("settingmenu.Ender Chest").getValues(true);
            ItemStack slot19 = ButtonSetter.setItem(Material.ENDER_EYE,null,getToggle(serverPlayerDoll.enableEnderChest) + slot19Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot19Lang.get("desc").toString(), '&'), desca,descb));
            list.set(19, new Pair<>(slot19,() -> {}));
            settings.put(slot19.getType(),()->serverPlayerDoll.enableEnderChest = onoff);

            Map<String,Object> slot20Lang = langFile.getConfigurationSection("settingmenu.Chunk Load").getValues(true);
            ItemStack slot20 = ButtonSetter.setItem(Material.RECOVERY_COMPASS,null,getToggle(serverPlayerDoll.enableChunkLoad) + slot20Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot20Lang.get("desc").toString(), '&'), desca,descb));
            list.set(20, new Pair<>(slot20,() -> {}));
            settings.put(slot20.getType(),()-> {
                serverPlayerDoll.enableChunkLoad = onoff;
                if (!onoff) {serverPlayerDoll.serverLevel().getChunkSource().removeRegionTicket(TicketType.PLAYER,serverPlayerDoll.dollChunkPos,Bukkit.getSimulationDistance()+1,serverPlayerDoll.dollChunkPos);}
            });

            Map<String,Object> slot21Lang = langFile.getConfigurationSection("settingmenu.Invulnerable").getValues(true);
            ItemStack slot21 = ButtonSetter.setItem(Material.TOTEM_OF_UNDYING,null,getToggle(doll.getNoDamageTicks()>0) + slot21Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot21Lang.get("desc").toString(), '&'), desca,descb));
            list.set(21, new Pair<>(slot21,() -> {}));
            settings.put(slot21.getType(),()-> doll.setNoDamageTicks(onoff?Integer.MAX_VALUE:0));

            Map<String,Object> slot22Lang = langFile.getConfigurationSection("settingmenu.Hostility").getValues(true);
            ItemStack slot22 = ButtonSetter.setItem(Material.TARGET,null,getToggle(serverPlayerDoll.enableHostility) + slot22Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot22Lang.get("desc").toString(), '&'), desca,descb));
            list.set(22, new Pair<>(slot22,() -> {}));
            settings.put(slot22.getType(),()->serverPlayerDoll.enableHostility = onoff);

            Map<String,Object> slot23Lang = langFile.getConfigurationSection("settingmenu.Pushable").getValues(true);
            ItemStack slot23 = ButtonSetter.setItem(Material.ARMOR_STAND,null,getToggle(doll.isCollidable()) + slot23Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot23Lang.get("desc").toString(), '&'), desca,descb));
            list.set(23, new Pair<>(slot23,() -> {}));
            settings.put(slot23.getType(),()->doll.setCollidable(onoff));

            Map<String,Object> slot24Lang = langFile.getConfigurationSection("settingmenu.Gravity").getValues(true);
            ItemStack slot24 = ButtonSetter.setItem(Material.GRAVEL,null,getToggle(doll.hasGravity()) + slot24Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot24Lang.get("desc").toString(), '&'), desca,descb));
            list.set(24, new Pair<>(slot24,() -> {}));
            settings.put(slot24.getType(),()->{doll.setGravity(onoff);});

            Map<String,Object> slot25Lang = langFile.getConfigurationSection("settingmenu.Glow").getValues(true);
            ItemStack slot25 = ButtonSetter.setItem(Material.GLOW_INK_SAC,null,getToggle(doll.isGlowing()) + slot25Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot25Lang.get("desc").toString(), '&'), desca,descb));
            list.set(25, new Pair<>(slot25,() -> {}));
            settings.put(slot25.getType(),()->{doll.setGlowing(onoff);});

            Map<String,Object> slot26Lang = langFile.getConfigurationSection("settingmenu.Large Step Size").getValues(true);
            ItemStack slot26 = ButtonSetter.setItem(Material.STONE_STAIRS,null,getToggle(serverPlayerDoll.maxUpStep()==1.0f) + slot26Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot26Lang.get("desc").toString(), '&'), desca,descb));
            list.set(26, new Pair<>(slot26,() -> {}));
            settings.put(slot26.getType(),()->serverPlayerDoll.setMaxUpStep(onoff?1.0f:0.6f));



            for (int i = 0 ; i< list.size();i++) {
                ItemStack item = list.get(i).getA();
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.getDisplayName().startsWith("§a")) {
                    itemMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
                }
                item.setItemMeta(itemMeta);
                this.addButton(i,this.createMainMenuButton(list.get(i)));
            }
            super.decorate(player);
        },0);
    }
    private InventoryButton createMainMenuButton(Pair<ItemStack, Runnable> pair) {
        return new InventoryButton()
                .creator(player -> pair.getA())
                .consumer(event -> {
                    if (!doll.isDead()) {
                        pair.getB().run();
                    } else {
                        event.getInventory().getViewers().forEach(p -> Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(), p::closeInventory));
                    }
                });
    }

    private ChatColor getToggle(boolean b) {
        return b?ChatColor.GREEN : ChatColor.RED;
    }
}
