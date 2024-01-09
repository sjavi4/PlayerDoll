package me.autobot.playerdoll.Events;


//import me.autobot.playerdoll.Command.SubCommand.operations.set;
import me.autobot.playerdoll.Command.SubCommands.Menu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractDollEvent implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            return;
        }
        Entity doll = event.getRightClicked();
        if (!PlayerDoll.dollManagerMap.containsKey(doll.getName())) {
            return;
        }
        /*
        File dollFile = new File(PlayerDoll.getPlugin().getDataFolder() + File.separator + "doll", PlayerDoll.dollManagerMap.get(doll.getName()).getDollName() + ".yml");
        YamlConfiguration dollData = YamlConfiguration.loadConfiguration(dollFile);
        if (!dollData.getStringList("Share").contains(event.getPlayer().getUniqueId().toString())) {
            if (!PlayerDoll.dollManagerMap.get(doll.getName()).getOwner().getName().equals(event.getPlayer().getName()) && !event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(TranslateFormatter.stringConvert("NoPermission"));
                return;
            }
        }
        PlayerDoll.dollManagerMap.get(doll.getName()).getActionPack().stopAll();

         */
        Player player = event.getPlayer();
        if (!player.isSneaking() || !player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {return;}
        //if (!doll.getMetadata("Owner").get(0).asString().equals((((CraftPlayer)player).getHandle().getStringUUID())) && !player.isOp()) {return;}

        new Menu(player,doll.getName());
        //new set().perform(player, doll.getName().substring(PlayerDoll.getDollPrefix().length()),null);
        //PlayerDoll.getGuiManager().openGUI(new MainMenu((Player)doll),player);
        //PlayerDoll.playerOpenInvMap.put(player,(Player)doll);

    }
}
