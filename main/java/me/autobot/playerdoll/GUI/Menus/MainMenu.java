package me.autobot.playerdoll.GUI.Menus;

import me.autobot.playerdoll.Command.SubCommand.operations.despawn;
import me.autobot.playerdoll.Command.SubCommand.operations.remove;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.GUI.InventoryButton;
import me.autobot.playerdoll.GUI.InventoryGUI;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;

public class MainMenu extends InventoryGUI {
    private Player doll;

    public MainMenu(Player doll) {
        this.doll = doll;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, YAMLManager.getConfig("lang").getString("title.main"));
    }
    @Override
    public void decorate(Player player) {
        Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),() -> {
            ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
            List<Pair<ItemStack,Runnable>> list = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),new Pair<>(defaultslot,()->{})));

            ItemStack slot0 = ButtonSetter.setItem(Material.PLAYER_HEAD,null,ChatColor.GREEN+doll.getDisplayName(),Arrays.asList(
                    TranslateFormatter.stringConvert("mainmenu.uuid",'&', "%uuid%", doll.getUniqueId().toString()),
                    TranslateFormatter.stringConvert("mainmenu.owner", '&', "%player%", Bukkit.getPlayer(UUID.fromString(TranslateFormatter.getDollConfig(doll.getName().substring(PlayerDoll.getDollPrefix().length())).getString("Owner"))).getName()),
                    TranslateFormatter.stringConvert("mainmenu.hp", '&',"%current%",Double.toString(doll.getHealth()),"%full%",Double.toString(doll.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())),
                    TranslateFormatter.stringConvert("mainmenu.hunger", '&', "%current%", Integer.toString(doll.getFoodLevel()), "%full%", "20"),
                    TranslateFormatter.stringConvert("mainmenu.gamemode", '&', "%gamemode%", doll.getGameMode().toString()),
                    TranslateFormatter.stringConvert("mainmenu.copy", '&')
            ));
            SkullMeta dollMeta = ((SkullMeta)slot0.getItemMeta());
            dollMeta.setOwnerProfile(doll.getPlayerProfile());
            slot0.setItemMeta(dollMeta);
            list.set(0,new Pair<>(slot0,() -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(doll.getUniqueId().toString()), null);
                player.closeInventory();
                player.sendMessage(TranslateFormatter.stringConvert("CopiedUUID",'&',"%uuid%", doll.getUniqueId().toString()));
            }));

            ItemStack slot2 = ButtonSetter.setItem(Material.CRAFTING_TABLE,null, TranslateFormatter.stringConvert("mainmenu.setting",'&'),null);
            list.set(2,new Pair<>(slot2,()-> PlayerDoll.getGuiManager().openGUI(new SettingMenu(this.doll), player)));

            ItemStack slot3 = ButtonSetter.setItem(Material.CHEST,null, TranslateFormatter.stringConvert("mainmenu.inventory",'&'),null);
            list.set(3, new Pair<>(slot3, () -> PlayerDoll.getGuiManager().openGUI(new InventoryMenu(this.doll), player)));

            ItemStack slot7 = ButtonSetter.setItem(Material.RED_BED,null, TranslateFormatter.stringConvert("mainmenu.offline",'&'),null);
            list.set(7,new Pair<>(slot7,() -> {
                new despawn().perform(player, doll.getName().substring(PlayerDoll.getDollPrefix().length()), null);
                Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), player::closeInventory,0);
            }));
            if (PlayerDoll.dollManagerMap.get(doll.getName()).getOwner().getName().equals(player.getName())) {
                ItemStack slot8 = ButtonSetter.setItem(Material.BARRIER, null, TranslateFormatter.stringConvert("mainmenu.remove", '&'), null);
                list.set(8, new Pair<>(slot8, () -> {
                    new remove().perform(player, doll.getName().substring(PlayerDoll.getDollPrefix().length()), null);
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(), player::closeInventory, 0);
                }));
            }
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
