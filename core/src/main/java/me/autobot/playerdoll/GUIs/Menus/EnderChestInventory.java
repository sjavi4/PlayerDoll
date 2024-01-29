package me.autobot.playerdoll.GUIs.Menus;

import me.autobot.playerdoll.GUIs.DollInvHolder;
import org.bukkit.entity.Player;

public class EnderChestInventory extends DollInvHolder {
    public EnderChestInventory(Player doll) {
        inventory = doll.getEnderChest();
    }
}
