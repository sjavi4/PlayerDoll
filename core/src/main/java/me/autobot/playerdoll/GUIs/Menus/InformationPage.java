package me.autobot.playerdoll.GUIs.Menus;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.GUIs.ButtonSetter;
import me.autobot.playerdoll.GUIs.DollInvHolder;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

public class InformationPage extends DollInvHolder {
    private final Player doll;
    private final String fullDollName;

    public InformationPage(Player doll) {
        this.doll = doll;
        this.fullDollName = CommandType.getDollName(doll.getName(),true);
        String shortDollName = CommandType.getDollName(doll.getName(), false);

        inventory = Bukkit.createInventory(this,9, LangFormatter.YAMLReplace("menuTitle.main",shortDollName));
        setupInventoryItem();
    }

    @Override
    public void setupInventoryItem() {
        super.setupInventoryItem();

        ItemStack slot0 = getDollStatus();

        buttonMap.put(slot0.getType(), (player) -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(doll.getUniqueId().toString()), null);
            player.closeInventory();
            player.sendMessage(LangFormatter.YAMLReplaceMessage("CopiedDollUUID",doll.getUniqueId().toString()));
        });
        inventory.setItem(0,slot0);

        ItemStack slot2 = ButtonSetter.setItem(Material.CRAFTING_TABLE,null, LangFormatter.YAMLReplace("mainmenu.setting"),null);
        inventory.setItem(2,slot2);
        buttonMap.put(slot2.getType(),(player)-> {
            //player.closeInventory();
            player.chat("/doll set "+doll.getName());
            //new Set(player,doll.getName());
            //PlayerDoll.getInvManager().openInv(new Settingmenu(player, doll), player);
        });

        ItemStack slot3 = ButtonSetter.setItem(Material.CHEST,null, LangFormatter.YAMLReplace("mainmenu.inventory"),null);
        inventory.setItem(3,slot3);
        buttonMap.put(slot3.getType(),(player)-> {
            player.openInventory(PlayerDoll.dollInvStorage.get(fullDollName).getInventoryPage());
        });

        ItemStack slot7 = ButtonSetter.setItem(Material.RED_BED,null, LangFormatter.YAMLReplace("mainmenu.offline"),null);
        inventory.setItem(7,slot7);
        buttonMap.put(slot7.getType(),(player)->{
            player.closeInventory();
            player.chat("/doll despawn "+doll.getName());
            //new Despawn(player, doll.getName());
        });

        ItemStack slot8 = ButtonSetter.setItem(Material.BARRIER, null, LangFormatter.YAMLReplace("mainmenu.remove"), null);
        inventory.setItem(8, slot8);
        buttonMap.put(slot8.getType(),(player)->{
            player.closeInventory();
            player.chat("/doll remove "+doll.getName());
            //new Remove(player, doll.getName());
        });
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        inventory.setItem(0, getDollStatus());
    }

    private ItemStack getDollStatus() {
        YamlConfiguration dollConfig = DollConfigManager.getConfigManager(doll).config;

        ItemStack slot0 = ButtonSetter.setItem(Material.PLAYER_HEAD,null, ChatColor.GREEN+ fullDollName, Arrays.asList(
                LangFormatter.YAMLReplace("mainmenu.uuid", doll.getUniqueId().toString()),
                LangFormatter.YAMLReplace("mainmenu.owner", dollConfig.getString("Owner.Name"), dollConfig.getString("Owner.UUID")),
                LangFormatter.YAMLReplace("mainmenu.hp",doll.getHealth(), doll.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()),
                LangFormatter.YAMLReplace("mainmenu.hunger", doll.getFoodLevel(),"20"),
                LangFormatter.YAMLReplace("mainmenu.gamemode",doll.getGameMode().toString()),
                LangFormatter.YAMLReplace("mainmenu.copy")
        ));
        SkullMeta dollMeta = ((SkullMeta)slot0.getItemMeta());
        dollMeta.setOwnerProfile(doll.getPlayerProfile());
        slot0.setItemMeta(dollMeta);

        return slot0;
    }
}
