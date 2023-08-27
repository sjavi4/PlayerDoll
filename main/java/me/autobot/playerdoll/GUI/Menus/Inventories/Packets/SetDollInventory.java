package me.autobot.playerdoll.GUI.Menus.Inventories.Packets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SetDollInventory {
    public static void UpdateDollEquiptment(Player doll, ItemStack item, EquipmentSlot slot) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        ((CraftPlayer)doll).getHandle().setItemSlot(slot, nmsItem);
        ((CraftPlayer)doll).getHandle().containerMenu.broadcastChanges();
        //Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().connection.send(new ClientboundSetEquipmentPacket(doll.getEntityId(), Arrays.asList(new Pair<>(slot, nmsItem)))));
    }

    public static void UpdateDollInventory(Player doll, ItemStack item, int slot) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        //((CraftPlayer)doll).getHandle().setItemSlot(slot, nmsItem);
        ((CraftPlayer)doll).getHandle().getInventory().setItem(slot,nmsItem);
        //Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().connection.send(new ClientboundContainerSetSlotPacket(0,0,slot,nmsItem)));
        if (doll.getInventory().getHeldItemSlot() == slot) {
            ((CraftPlayer)doll).getHandle().containerMenu.broadcastCarriedItem();
            //Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().connection.send(new ClientboundSetEquipmentPacket(doll.getEntityId(), Arrays.asList(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(doll.getInventory().getItemInMainHand()))))));
        }
    }

    public static void UpdateDollEnderChest(Player doll, ItemStack item, int slot) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        //((CraftPlayer)doll).getHandle().setItemSlot(slot, nmsItem);
        ((CraftPlayer)doll).getHandle().getEnderChestInventory().setItem(slot,nmsItem);
    }

    public static boolean CheckSyncedItems(Player doll, ItemStack clickedSlot, int slot) {
        if (clickedSlot == null) {clickedSlot = new ItemStack(Material.AIR, 0);}
        return CraftItemStack.asBukkitCopy(((CraftPlayer)doll).getHandle().getInventory().getItem(slot)).getType().equals(clickedSlot.getType());
    }
}
