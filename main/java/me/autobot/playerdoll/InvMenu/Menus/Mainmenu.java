package me.autobot.playerdoll.InvMenu.Menus;

import me.autobot.playerdoll.Command.SubCommand.operations.despawn;
import me.autobot.playerdoll.Command.SubCommand.operations.remove;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.ButtonSetter;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.*;

public class Mainmenu extends InvInitializer {

    private final Map<ItemStack,Runnable> actionMap = new HashMap<>();
    public Mainmenu(Player player, Player doll) {
        super(player,doll);
    }

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return Bukkit.createInventory(null, 9, TranslateFormatter.stringConvert("menuTitle.main",'&',"%a%",doll.getName()));
    }

    @Override
    public void decorate(Player player,Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        actionMap.put(defaultslot,()->{});

        List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),defaultslot));

        ItemStack slot0 = ButtonSetter.setItem(Material.PLAYER_HEAD,null, ChatColor.GREEN+doll.getDisplayName(), Arrays.asList(
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

        actionMap.put(slot0, () -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(doll.getUniqueId().toString()), null);
            player.closeInventory();
            player.sendMessage(TranslateFormatter.stringConvert("CopiedUUID",'&',"%uuid%", doll.getUniqueId().toString()));
        });
        buttons.set(0,slot0);

        ItemStack slot2 = ButtonSetter.setItem(Material.CRAFTING_TABLE,null, TranslateFormatter.stringConvert("mainmenu.setting",'&'),null);
        buttons.set(2,slot2);
        actionMap.put(slot2,()-> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Settingmenu(player, doll), player);
        });

        ItemStack slot3 = ButtonSetter.setItem(Material.CHEST,null, TranslateFormatter.stringConvert("mainmenu.inventory",'&'),null);
        buttons.set(3,slot3);
        actionMap.put(slot3,()-> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Inventorymenu(player, doll), player);
        });

        ItemStack slot7 = ButtonSetter.setItem(Material.RED_BED,null, TranslateFormatter.stringConvert("mainmenu.offline",'&'),null);
        buttons.set(7,slot7);
        actionMap.put(slot7,()->{
            new despawn().perform(player, doll.getName().substring(PlayerDoll.getDollPrefix().length()), null);
            player.closeInventory();
        });

        if (PlayerDoll.dollManagerMap.get(doll.getName()).getOwner().getName().equalsIgnoreCase(player.getName())) {
            ItemStack slot8 = ButtonSetter.setItem(Material.BARRIER, null, TranslateFormatter.stringConvert("mainmenu.remove", '&'), null);
            buttons.set(8, slot8);
            actionMap.put(slot8,()->{
                new remove().perform(player, doll.getName().substring(PlayerDoll.getDollPrefix().length()), null);
                player.closeInventory();
            });
        }

        for (int i = 0 ; i< buttons.size();i++) {
            this.addButton(i,this.createMainMenuButton(buttons.get(i)));
        }
        super.decorate(player,doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {
                    Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),actionMap.get(itemStack),0);
                });
    }

}
