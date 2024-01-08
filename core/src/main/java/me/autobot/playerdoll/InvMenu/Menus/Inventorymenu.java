package me.autobot.playerdoll.InvMenu.Menus;

import me.autobot.playerdoll.Command.SubCommands.Echest;
import me.autobot.playerdoll.Command.SubCommands.Exp;
import me.autobot.playerdoll.Command.SubCommands.Inv;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.InvMenu.ButtonInitializer;
import me.autobot.playerdoll.InvMenu.ButtonSetter;
import me.autobot.playerdoll.InvMenu.InvInitializer;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.autobot.playerdoll.Util.Pair;

import java.util.*;

public class Inventorymenu extends InvInitializer {
    private final Map<ItemStack,Runnable> actionMap = new HashMap<>();
    private final Player player;
    private final Player doll;
    public Inventorymenu(Player player, Player doll) {
        super(player,doll);
        this.player = player;
        this.doll = doll;
    }

    @Override
    protected Inventory createInventory(Player player, Player doll) {
        return Bukkit.createInventory(null, 9, LangFormatter.YAMLReplace("menuTitle.inventory", new Pair<>("%a%",doll.getName())));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getSlot() == 6) {
            ItemStack slot6 = ButtonSetter.setItem(Material.EXPERIENCE_BOTTLE,null, LangFormatter.YAMLReplace("inventorymenu.levelUp")
                    , Arrays.asList(LangFormatter.YAMLReplace("inventorymenu.level", new Pair<>("%a%"
                            , Integer.toString(Math.max(0,doll.getLevel()-1)))), LangFormatter.YAMLReplace("inventorymenu.levelGet")));

            event.getClickedInventory().setItem(6,slot6);
        }
    }

    @Override
    public void decorate(Player player,Player doll) {
        ItemStack defaultslot = ButtonSetter.setItem(Material.GRAY_STAINED_GLASS_PANE,null," ", null);
        actionMap.put(defaultslot,()->{});

        List<ItemStack> buttons = new ArrayList<>(Collections.nCopies(this.getInventory().getSize(),defaultslot));

        ItemStack slot0 = ButtonSetter.setItem(Material.RESPAWN_ANCHOR,null, LangFormatter.YAMLReplace("controlButton.back"),null);
        buttons.set(0,slot0);
        actionMap.put(slot0,() -> {
            player.closeInventory();
            PlayerDoll.getInvManager().openInv(new Mainmenu(player, doll), player);
        });

        ItemStack slot2 = ButtonSetter.setItem(Material.OAK_CHEST_BOAT,null, LangFormatter.YAMLReplace("inventorymenu.inventory"),null);
        buttons.set(2,slot2);
        actionMap.put(slot2,() -> {
            player.closeInventory();
            new Inv(player,doll.getName());
            //PlayerDoll.getInvManager().openInv(new Dollinventory(player, doll), player);
        });
        ItemStack slot3 = ButtonSetter.setItem(Material.SHULKER_BOX,null,LangFormatter.YAMLReplace("inventorymenu.enderchest"),null);
        ButtonSetter.setShulkerBoxPreview(slot3, doll.getEnderChest().getContents());
        buttons.set(3, slot3);
        actionMap.put(slot3, () -> {
            player.closeInventory();
            new Echest(player,doll.getName());
            //PlayerDoll.getInvManager().openInv(new Dollenderchest(player, doll), player);
        });

        ItemStack slot6 = ButtonSetter.setItem(Material.EXPERIENCE_BOTTLE,null, LangFormatter.YAMLReplace("inventorymenu.levelUp")
                , Arrays.asList(LangFormatter.YAMLReplace("inventorymenu.level", new Pair<>("%a%"
                        , Integer.toString(doll.getLevel()))), LangFormatter.YAMLReplace("inventorymenu.levelGet")));
        buttons.set(6,slot6);
        actionMap.put(slot6, ()-> {
            new Exp(player,doll.getName(),null);
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
