package me.autobot.playerdoll.gui.menu;

import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.gui.ItemSetter;
import me.autobot.playerdoll.persistantdatatype.ButtonAction;
import me.autobot.playerdoll.util.LangFormatter;
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
    public DollInfoMenu(Doll doll) {
        super(doll, DollGUIHolder.MenuType.INFO);
        this.dollPlayer = doll.getBukkitPlayer();
        this.dollUUID = dollPlayer.getUniqueId();
        inventory = Bukkit.createInventory(this, 9, LangFormatter.YAMLReplace("inv-name.info", dollPlayer.getName()));

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

        ItemStack slot2 = ItemSetter.setItem(Material.CRAFTING_TABLE, ButtonAction.OPEN_DOLL_SETTING, 1, LangFormatter.YAMLReplace("info.set"),null);
        inventory.setItem(2,slot2);
        buttonMap.put(getPDC(slot2), (player)-> {
            String commandSet = String.format("/playerdoll:doll set %s", DollManager.dollShortName(dollPlayer.getName()));
            player.chat(commandSet);
        });

        ItemStack slot3 = ItemSetter.setItem(Material.FLETCHING_TABLE, ButtonAction.OPEN_GSETTING, 1, LangFormatter.YAMLReplace("info.gset"),null);
        inventory.setItem(3,slot3);
        buttonMap.put(getPDC(slot3), (player)-> {
            String commandGSet = String.format("/playerdoll:doll gset %s", DollManager.dollShortName(dollPlayer.getName()));
            player.chat(commandGSet);
        });


        ItemStack slot4 = ItemSetter.setItem(Material.CHEST,ButtonAction.OPEN_DATA, 1, LangFormatter.YAMLReplace("info.data"),null);
        inventory.setItem(4,slot4);
        buttonMap.put(getPDC(slot4),(player)-> {
            player.openInventory(dollGUIHolder.menus.get(DollGUIHolder.MenuType.DATA).getInventory());
        });

        ItemStack slot7 = ItemSetter.setItem(Material.RED_BED,ButtonAction.DOLL_OFFLINE, 1, LangFormatter.YAMLReplace("info.offline"),null);
        inventory.setItem(7,slot7);
        buttonMap.put(getPDC(slot7),(player)-> {
            player.closeInventory();
            String commandDespawn = String.format("/playerdoll:doll despawn %s", DollManager.dollShortName(dollPlayer.getName()));
            player.chat(commandDespawn);
        });
        List<String> removeHint = Arrays.asList(LangFormatter.splitter(LangFormatter.YAMLReplace("info.remove-hint")));
        ItemStack slot8 = ItemSetter.setItem(Material.BARRIER, ButtonAction.DOLL_REMOVE, 1, LangFormatter.YAMLReplace("info.remove"), removeHint);
        inventory.setItem(8, slot8);
        buttonMap.put(getPDC(slot8),(player)->{
            player.closeInventory();
            String commandRemove = String.format("/playerdoll:doll remove %s", DollManager.dollShortName(dollPlayer.getName()));
            player.chat(commandRemove);
        });
    }
    private ItemStack getDollStatus() {
        DollConfig dollConfig = DollConfig.getOnlineDollConfig(dollUUID);

        ItemStack slot0 = ItemSetter.setItem(Material.PLAYER_HEAD,
                ButtonAction.NONE,
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
