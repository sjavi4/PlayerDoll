package me.autobot.playerdoll.api.inv.gui;

import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.ItemSetter;
import me.autobot.playerdoll.api.inv.button.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DollInfoMenu extends AbstractMenu {
    private final Player dollPlayer;
    private final UUID dollUUID;
    public DollInfoMenu(Doll doll, DollMenuHolder holder) {
        super(doll, holder);
        this.dollPlayer = doll.getBukkitPlayer();
        this.dollUUID = dollPlayer.getUniqueId();
        inventory = Bukkit.createInventory(dollPlayer, 9, LangFormatter.YAMLReplace("inv-name.info", dollPlayer.getName()));

    }

    @Override
    public void updateGUIContent() {
        inventory.setItem(0, getDollStatus());
    }

    @Override
    public void onClickOutside(Player player) {

    }

    @Override
    public void initialGUIContent() {
        super.initialGUIContent();

        ItemStack slot0 = getDollStatus();
        buttonMap.put(getPDC(slot0), (player -> {}));
        inventory.setItem(0,slot0);

        ItemStack slot2 = ItemSetter.setItem(Material.CRAFTING_TABLE, ActionButton.OPEN_DOLL_SETTING, 1, LangFormatter.YAMLReplace("info.set"),null);
        inventory.setItem(2,slot2);
        buttonMap.put(getPDC(slot2), (player)-> {
            String commandSet = String.format("/playerdoll:doll set %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandSet);
        });

        ItemStack slot3 = ItemSetter.setItem(Material.FLETCHING_TABLE, ActionButton.OPEN_GSETTING, 1, LangFormatter.YAMLReplace("info.gset"),null);
        inventory.setItem(3,slot3);
        buttonMap.put(getPDC(slot3), (player)-> {
            String commandGSet = String.format("/playerdoll:doll gset %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandGSet);
        });


        ItemStack slot4 = ItemSetter.setItem(Material.CHEST,ActionButton.OPEN_DATA, 1, LangFormatter.YAMLReplace("info.data"),null);
        inventory.setItem(4,slot4);
        buttonMap.put(getPDC(slot4),(player)-> {
            player.openInventory(dollMenuHolder.inventoryStorage.get(DollDataMenu.class).get(0).getInventory());
        });

        ItemStack slot7 = ItemSetter.setItem(Material.RED_BED,ActionButton.DOLL_OFFLINE, 1, LangFormatter.YAMLReplace("info.offline"),null);
        inventory.setItem(7,slot7);
        buttonMap.put(getPDC(slot7),(player)-> {
            player.closeInventory();
            String commandDespawn = String.format("/playerdoll:doll despawn %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandDespawn);
        });
        List<String> removeHint = Arrays.asList(LangFormatter.splitter(LangFormatter.YAMLReplace("info.remove-hint")));
        ItemStack slot8 = ItemSetter.setItem(Material.BARRIER, ActionButton.DOLL_REMOVE, 1, LangFormatter.YAMLReplace("info.remove"), removeHint);
        inventory.setItem(8, slot8);
        buttonMap.put(getPDC(slot8),(player)->{
            player.closeInventory();
            String commandRemove = String.format("/playerdoll:doll remove %s", DollNameUtil.dollShortName(dollPlayer.getName()));
            player.chat(commandRemove);
        });
    }
    private ItemStack getDollStatus() {
        DollConfig dollConfig = DollConfig.getOnlineConfig(dollUUID);

        ItemStack slot0 = ItemSetter.setItem(Material.PLAYER_HEAD,
                ActionButton.NONE,
                1,
                ChatColor.GREEN + dollPlayer.getName(),
                Arrays.asList(
                    LangFormatter.YAMLReplace("info.uuid", dollUUID),
                    LangFormatter.YAMLReplace("info.owner", dollConfig.ownerName.getValue(), dollConfig.ownerUUID.getValue()),
                    LangFormatter.YAMLReplace("info.hp", dollPlayer.getHealth(), dollPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()),
                    LangFormatter.YAMLReplace("info.hunger", dollPlayer.getFoodLevel(), "20"),
                    LangFormatter.YAMLReplace("info.gamemode", dollPlayer.getGameMode().toString())
                )
        );
        SkullMeta dollMeta = ((SkullMeta)slot0.getItemMeta());
        dollMeta.setOwnerProfile(dollPlayer.getPlayerProfile());
        slot0.setItemMeta(dollMeta);

        return slot0;
    }
}
