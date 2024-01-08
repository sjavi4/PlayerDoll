package me.autobot.playerdoll.InvMenu.Menus;

import me.autobot.playerdoll.Command.SubCommands.Despawn;
import me.autobot.playerdoll.Command.SubCommands.Remove;
import me.autobot.playerdoll.Command.SubCommands.Set;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.ButtonSetter;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import me.autobot.playerdoll.Util.Pair;

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
        return Bukkit.createInventory(null, 9, LangFormatter.YAMLReplace("menuTitle.main", new Pair<>("%a%",doll.getName())));
    }

    @Override
    public void decorate(Player player,Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        actionMap.put(defaultslot,()->{});

        List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),defaultslot));

        String name = doll.getName();
        YamlConfiguration dollConfig = DollConfigManager.dollConfigManagerMap.get(doll).config;
        //var map = LangFormatter.getDollConfig(name).getConfigurationSection("Owner");

        ItemStack slot0 = ButtonSetter.setItem(Material.PLAYER_HEAD,null, ChatColor.GREEN+doll.getDisplayName(), Arrays.asList(
                LangFormatter.YAMLReplace("mainmenu.uuid", new Pair<>( "%a%", doll.getUniqueId().toString())),
                LangFormatter.YAMLReplace("mainmenu.owner", new Pair<>( "%a%", dollConfig.getString("Owner.Name") ), new Pair<>("%b%", dollConfig.getString("Owner.UUID"))),
                LangFormatter.YAMLReplace("mainmenu.hp",new Pair<>( "%a%",Double.toString(doll.getHealth())), new Pair<>( "%b%",Double.toString(doll.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))),
                LangFormatter.YAMLReplace("mainmenu.hunger", new Pair<>( "%a%", Integer.toString(doll.getFoodLevel())), new Pair<>( "%b%", "20")),
                LangFormatter.YAMLReplace("mainmenu.gamemode", new Pair<>( "%a%", doll.getGameMode().toString())),
                LangFormatter.YAMLReplace("mainmenu.copy")
        ));
        SkullMeta dollMeta = ((SkullMeta)slot0.getItemMeta());
        dollMeta.setOwnerProfile(doll.getPlayerProfile());
        slot0.setItemMeta(dollMeta);

        actionMap.put(slot0, () -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(doll.getUniqueId().toString()), null);
            player.closeInventory();
            player.sendMessage(LangFormatter.YAMLReplaceMessage("CopiedDollUUID",new Pair<>( "%a%", doll.getUniqueId().toString())));
        });
        buttons.set(0,slot0);

        ItemStack slot2 = ButtonSetter.setItem(Material.CRAFTING_TABLE,null, LangFormatter.YAMLReplace("mainmenu.setting"),null);
        buttons.set(2,slot2);
        actionMap.put(slot2,()-> {
            player.closeInventory();
            new Set(player,doll.getName());
            //PlayerDoll.getInvManager().openInv(new Settingmenu(player, doll), player);
        });

        ItemStack slot3 = ButtonSetter.setItem(Material.CHEST,null, LangFormatter.YAMLReplace("mainmenu.inventory"),null);
        buttons.set(3,slot3);
        actionMap.put(slot3,()-> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Inventorymenu(player, doll), player);
        });

        ItemStack slot7 = ButtonSetter.setItem(Material.RED_BED,null, LangFormatter.YAMLReplace("mainmenu.offline"),null);
        buttons.set(7,slot7);
        actionMap.put(slot7,()->{
            //new despawn().perform(player, doll.getName().substring(PlayerDoll.getDollPrefix().length()), null);
            player.closeInventory();
            new Despawn(player, doll.getName());
        });

        ItemStack slot8 = ButtonSetter.setItem(Material.BARRIER, null, LangFormatter.YAMLReplace("mainmenu.remove"), null);
        buttons.set(8, slot8);
        actionMap.put(slot8,()->{
            player.closeInventory();
            new Remove(player, doll.getName());
        });
        for (int i = 0 ; i< buttons.size();i++) {
            this.addButton(i,this.createMainMenuButton(buttons.get(i)));
        }
        super.decorate(player,doll);
    }
    private ButtonInitializer createMainMenuButton(ItemStack itemStack) {
        return new ButtonInitializer()
                .creator((player1, player2) -> itemStack)
                .consumer(event -> {
                    Runnable task = actionMap.get(itemStack);
                    if (PlayerDoll.isFolia) FoliaSupport.globalTask(task);
                    else Bukkit.getScheduler().runTaskLater(PlayerDoll.getPlugin(),task,0);
                });
    }

}
