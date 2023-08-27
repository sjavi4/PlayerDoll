package me.autobot.playerdoll.GUI.Menus;

import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.PlayerDoll;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InventoryMenu extends InventoryGUI {

    private Player doll;
    public InventoryMenu(Player doll) {
        this.doll = doll;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9,  YAMLManager.getConfig("lang").getString("title.inventory"));
    }



    @Override
    public void decorate(Player player) {
        Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),() -> {
            ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);

            List<Pair<ItemStack,Runnable>> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),new Pair<>(defaultslot,()->{})));

            ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null,TranslateFormatter.stringConvert("control.back",'&'),null);
            list.set(0,new Pair<>(slot0,()-> PlayerDoll.getGuiManager().openGUI(new MainMenu(this.doll), player)));


            ItemStack slot2 = ButtonSetter.setItem(Material.LIME_SHULKER_BOX,null,TranslateFormatter.stringConvert("inventorymenu.armor_offhand",'%'),null);
            ItemStack[] slot2Preview = new ItemStack[27];
            slot2Preview[20] = new ItemStack(Material.LEATHER_HELMET);
            slot2Preview[21] = new ItemStack(Material.LEATHER_CHESTPLATE);
            slot2Preview[22] = new ItemStack(Material.LEATHER_LEGGINGS);
            slot2Preview[23] = new ItemStack(Material.LEATHER_BOOTS);
            slot2Preview[24] = new ItemStack(Material.SHIELD);
            ItemStack[] armor = doll.getInventory().getArmorContents();
            ArrayUtils.reverse(armor);
            for (int i = 0; i<armor.length;i++) {
                slot2Preview[2+i] = armor[i];
            }
            slot2Preview[6] = doll.getInventory().getItemInOffHand();
            ButtonSetter.setShulkerBoxPreview(slot2,slot2Preview);
            list.set(2,new Pair<>(slot2,()-> PlayerDoll.getGuiManager().openGUI((InventoryGUI) doll.getMetadata("DollArmorMenu").get(0).value(), player)));

            ItemStack slot3 = ButtonSetter.setItem(Material.RED_SHULKER_BOX,null,TranslateFormatter.stringConvert("inventorymenu.hotbar",'&'),null);
            ButtonSetter.setShulkerBoxPreview(slot3,Arrays.copyOfRange(doll.getInventory().getContents(),0,9));
            list.set(3,new Pair<>(slot3,()-> PlayerDoll.getGuiManager().openGUI((InventoryGUI) doll.getMetadata("DollHotbarMenu").get(0).value(), player)));

            if (PlayerDoll.dollManagerMap.get(doll.getName()).enableInventory) {
                ItemStack slot4 = ButtonSetter.setItem(Material.GRAY_SHULKER_BOX,null,TranslateFormatter.stringConvert("inventorymenu.inventory", '&'),null);
                ButtonSetter.setShulkerBoxPreview(slot4, Arrays.copyOfRange(doll.getInventory().getContents(), 9, 36));
                list.set(4, new Pair<>(slot4, () -> PlayerDoll.getGuiManager().openGUI((InventoryGUI) doll.getMetadata("DollInvenMenu").get(0).value(), player)));
            }

            if (PlayerDoll.dollManagerMap.get(doll.getName()).enableEnderChest) {
                ItemStack slot5 = ButtonSetter.setItem(Material.SHULKER_BOX,null,TranslateFormatter.stringConvert("inventorymenu.enderchest", '&'),null);
                ButtonSetter.setShulkerBoxPreview(slot5, doll.getEnderChest().getContents());
                list.set(5, new Pair<>(slot5, () -> PlayerDoll.getGuiManager().openGUI((InventoryGUI) doll.getMetadata("DollEnderChestMenu").get(0).value(), player)));
            }

            ItemStack slot6 = ButtonSetter.setItem(Material.EXPERIENCE_BOTTLE,null,TranslateFormatter.stringConvert("inventorymenu.levelUp",'&')
                    , Arrays.asList(TranslateFormatter.stringConvert("inventorymenu.level",'&',"%level%"
                            , Integer.toString(doll.getLevel())), TranslateFormatter.stringConvert("inventorymenu.levelGet",'&')));
            list.set(6,new Pair<>(slot6,()-> {
                if (doll.getLevel() <= 0) {return;}
                float sumPoints = doll.getExp() * doll.getExpToLevel() + player.getExp() * player.getExpToLevel();
                doll.setExp(0);
                player.setExp(0);

                doll.setLevel(doll.getLevel() - 1);
                sumPoints += doll.getExpToLevel();

                while (sumPoints >= player.getExpToLevel()) {
                    sumPoints -= player.getExpToLevel();
                    player.setLevel(player.getLevel()+1);
                }
                if (sumPoints > 0) {
                    player.setExp(sumPoints/player.getExpToLevel());
                }
                PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), player);
            }));

            for (int i = 0 ; i< list.size();i++) {
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
}