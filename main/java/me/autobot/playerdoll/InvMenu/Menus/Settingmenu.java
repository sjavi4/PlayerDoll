package me.autobot.playerdoll.InvMenu.Menus;

import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
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

import java.util.*;
import java.util.function.Consumer;

public class Settingmenu extends InvInitializer {
    private final Map<ItemStack, Consumer<Boolean>> actionMap = new HashMap<>();
    public Settingmenu(Player player, Player doll) {
        super(player,doll);
    }

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return Bukkit.createInventory(null, 54, TranslateFormatter.stringConvert("menuTitle.setting",'&',"%a%",doll.getName()));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getSlot() < 18) {
            return;
        }
        boolean onoff = event.isLeftClick();
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

    }

    @Override
    public void decorate(Player player,Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        actionMap.put(defaultslot,(b)->{});

        List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),defaultslot));

        String desca = TranslateFormatter.stringConvert("controlButton.hinta",'&');
        String descb = TranslateFormatter.stringConvert("controlButton.hintb",'&');

        YamlConfiguration langFile = YAMLManager.getConfig("lang");
        Map<String, Object> toggleMap = PlayerDoll.dollManagerMap.get(doll.getName()).configManager.getData();

        //ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null, TranslateFormatter.stringConvert("control.back",'&'),null);
        ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null, TranslateFormatter.stringConvert("controlButton.back",'&'), null);
        buttons.set(0,slot0);
        actionMap.put(slot0,(b)-> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Mainmenu(player, doll), player);
        });
        //buttons.set(0,new Pair<>(slot0,()-> PlayerDoll.getGuiManager().openGUI(new MainMenu(this.doll), player)));
/*
            Map<String,Object> slot18Lang = langFile.getConfigurationSection("settingmenu.Inventory").getValues(true);
            ItemStack slot18 = ButtonSetter.setItem(Material.CHEST_MINECART,null,getToggle(serverPlayerDoll.enableInventory) + slot18Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot18Lang.get("desc").toString(),'&'),desca,descb));
            buttons.set(18, new Pair<>(slot18,() -> {}));
            settings.put(slot18.getType(),()->serverPlayerDoll.enableInventory = onoff);

 */
        //ItemStack slot18 = ButtonSetter.setItem(Material.CHEST_MINECART,null,"slot18",null);
        //buttons.set(18,slot18);
        //actionMap.put(slot18, ()->{});

        ItemStack slot19 = ButtonSetter.setItem(Material.ENDER_EYE,null,getToggle((Boolean) toggleMap.get("setting.Ender Chest")) + langFile.getString("settingmenu.Ender Chest.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Ender Chest.desc",'&'),desca,descb));
        buttons.set(19,slot19);
        actionMap.put(slot19, (b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Ender Chest",b));

        //ItemStack slot20 = ButtonSetter.setItem(Material.RECOVERY_COMPASS,null,"slot20", null);
        //buttons.set(20, slot20);
        //actionMap.put(slot20,(b)-> {});

        ItemStack slot21 = ButtonSetter.setItem(Material.TOTEM_OF_UNDYING,null,getToggle((Boolean) toggleMap.get("setting.Invulnerable")) + langFile.getString("settingmenu.Invulnerable.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Invulnerable.desc",'&'),desca,descb));
        buttons.set(21, slot21);
        actionMap.put(slot21,(b)-> PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Invulnerable",b));//doll.setNoDamageTicks(onoff?Integer.MAX_VALUE:0));

        ItemStack slot22 = ButtonSetter.setItem(Material.TARGET,null,getToggle((Boolean) toggleMap.get("setting.Hostility")) + langFile.getString("settingmenu.Hostility.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Hostility.desc",'&'),desca,descb));
        buttons.set(22, slot22);
        actionMap.put(slot22,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Hostility",b));//serverPlayerDoll.enableHostility = onoff);

        ItemStack slot23 = ButtonSetter.setItem(Material.ARMOR_STAND,null,getToggle((Boolean) toggleMap.get("setting.Pushable")) + langFile.getString("settingmenu.Pushable.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Pushable.desc",'&'),desca,descb));
        buttons.set(23, slot23);
        actionMap.put(slot23,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Pushable",b));//doll.setCollidable(onoff));

        ItemStack slot24 = ButtonSetter.setItem(Material.GRAVEL,null,getToggle((Boolean) toggleMap.get("setting.Gravity")) + langFile.getString("settingmenu.Gravity.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Gravity.desc",'&'),desca,descb));
        buttons.set(24, slot24);
        actionMap.put(slot24,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Gravity",b));//{doll.setGravity(onoff);});

        ItemStack slot25 = ButtonSetter.setItem(Material.GLOW_INK_SAC,null,getToggle((Boolean) toggleMap.get("setting.Glow")) + langFile.getString("settingmenu.Glow.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Glow.desc",'&'),desca,descb));
        buttons.set(25, slot25);
        actionMap.put(slot25,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Glow",b));//{doll.setGlowing(onoff);});

        ItemStack slot26 = ButtonSetter.setItem(Material.STONE_STAIRS,null,getToggle((Boolean) toggleMap.get("setting.Large Step Size")) + langFile.getString("settingmenu.Large Step Size.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Large Step Size.desc",'&'),desca,descb));
        buttons.set(26, slot26);
        actionMap.put(slot26,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Large Step Size",b));//serverPlayerDoll.setMaxUpStep(onoff?1.0f:0.6f));

        ItemStack slot27 = ButtonSetter.setItem(Material.WOODEN_SWORD,null,getToggle((Boolean) toggleMap.get("setting.Attack")) + langFile.getString("settingmenu.Attack.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Attack.desc",'&'),desca,descb));
        buttons.set(27, slot27);
        actionMap.put(slot27,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Attack",b));

        ItemStack slot28 = ButtonSetter.setItem(Material.STONE_BUTTON,null,getToggle((Boolean) toggleMap.get("setting.Use")) + langFile.getString("settingmenu.Use.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Use.desc",'&'),desca,descb));
        buttons.set(28, slot28);
        actionMap.put(slot28,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Use",b));

        ItemStack slot29 = ButtonSetter.setItem(Material.PAPER,null,getToggle((Boolean) toggleMap.get("setting.Copy")) + langFile.getString("settingmenu.Copy.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Copy.desc",'&'),desca,descb));
        buttons.set(29, slot29);
        actionMap.put(slot29,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Copy",b));

        ItemStack slot30 = ButtonSetter.setItem(Material.MINECART,null,getToggle((Boolean) toggleMap.get("setting.Dismount")) + langFile.getString("settingmenu.Dismount.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Dismount.desc",'&'),desca,descb));
        buttons.set(30, slot30);
        actionMap.put(slot30,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Dismount",b));

        ItemStack slot31 = ButtonSetter.setItem(Material.ITEM_FRAME,null,getToggle((Boolean) toggleMap.get("setting.Mount")) + langFile.getString("settingmenu.Mount.name"),List.of(TranslateFormatter.stringConvert("settingmenu.Mount.desc",'&'),desca,descb));
        buttons.set(31, slot31);
        actionMap.put(slot31,(b)->PlayerDoll.dollManagerMap.get(doll.getName()).configManager.setData("setting.Mount",b));

        /*
        Map<String,Object> slot19Lang = langFile.getConfigurationSection("settingmenu.Ender Chest").getValues(true);
        ItemStack slot19 = ButtonSetter.setItem(Material.ENDER_EYE,null,getToggle(serverPlayerDoll.enableEnderChest) + slot19Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot19Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(19, new Pair<>(slot19,() -> {}));
        settings.put(slot19.getType(),()->serverPlayerDoll.enableEnderChest = onoff);


         */
            /*
            Map<String,Object> slot20Lang = langFile.getConfigurationSection("settingmenu.Chunk Load").getValues(true);
            ItemStack slot20 = ButtonSetter.setItem(Material.RECOVERY_COMPASS,null,ChatColor.WHITE + slot20Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot20Lang.get("desc").toString(), '&'), desca,descb));
            buttons.set(20, new Pair<>(slot20,() -> {}));
            settings.put(slot20.getType(),()-> {
            });

             */
        /*
        Map<String,Object> slot21Lang = langFile.getConfigurationSection("settingmenu.Invulnerable").getValues(true);
        ItemStack slot21 = ButtonSetter.setItem(Material.TOTEM_OF_UNDYING,null,getToggle(doll.getNoDamageTicks()>0) + slot21Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot21Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(21, new Pair<>(slot21,() -> {}));
        settings.put(slot21.getType(),()-> doll.setNoDamageTicks(onoff?Integer.MAX_VALUE:0));

        Map<String,Object> slot22Lang = langFile.getConfigurationSection("settingmenu.Hostility").getValues(true);
        ItemStack slot22 = ButtonSetter.setItem(Material.TARGET,null,getToggle(serverPlayerDoll.enableHostility) + slot22Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot22Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(22, new Pair<>(slot22,() -> {}));
        settings.put(slot22.getType(),()->serverPlayerDoll.enableHostility = onoff);

        Map<String,Object> slot23Lang = langFile.getConfigurationSection("settingmenu.Pushable").getValues(true);
        ItemStack slot23 = ButtonSetter.setItem(Material.ARMOR_STAND,null,getToggle(doll.isCollidable()) + slot23Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot23Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(23, new Pair<>(slot23,() -> {}));
        settings.put(slot23.getType(),()->doll.setCollidable(onoff));

        Map<String,Object> slot24Lang = langFile.getConfigurationSection("settingmenu.Gravity").getValues(true);
        ItemStack slot24 = ButtonSetter.setItem(Material.GRAVEL,null,getToggle(doll.hasGravity()) + slot24Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot24Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(24, new Pair<>(slot24,() -> {}));
        settings.put(slot24.getType(),()->{doll.setGravity(onoff);});

        Map<String,Object> slot25Lang = langFile.getConfigurationSection("settingmenu.Glow").getValues(true);
        ItemStack slot25 = ButtonSetter.setItem(Material.GLOW_INK_SAC,null,getToggle(doll.isGlowing()) + slot25Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot25Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(25, new Pair<>(slot25,() -> {}));
        settings.put(slot25.getType(),()->{doll.setGlowing(onoff);});

        Map<String,Object> slot26Lang = langFile.getConfigurationSection("settingmenu.Large Step Size").getValues(true);
        ItemStack slot26 = ButtonSetter.setItem(Material.STONE_STAIRS,null,getToggle(serverPlayerDoll.maxUpStep()==1.0f) + slot26Lang.get("name").toString(),List.of(TranslateFormatter.stringTranslate(slot26Lang.get("desc").toString(), '&'), desca,descb));
        buttons.set(26, new Pair<>(slot26,() -> {}));
        settings.put(slot26.getType(),()->serverPlayerDoll.setMaxUpStep(onoff?1.0f:0.6f));


         */


        for (int i = 0 ; i< buttons.size();i++) {
            ItemStack item = buttons.get(i);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.getDisplayName().startsWith("§a")) {
                itemMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
            }
            item.setItemMeta(itemMeta);
            this.addButton(i,this.createMainMenuButton(buttons.get(i)));
        }
        super.decorate(player,doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),()->actionMap.get(itemStack).accept(event.isLeftClick()),0);
                });
    }
    private ChatColor getToggle(boolean b) {
        return b?ChatColor.GREEN : ChatColor.RED;
    }

}