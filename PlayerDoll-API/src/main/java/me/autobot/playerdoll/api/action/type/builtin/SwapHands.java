package me.autobot.playerdoll.api.action.type.builtin;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SwapHands extends AbsActionType {
    public SwapHands(boolean preventSpectator) {
        super(preventSpectator);
    }

    @Override
    public String registerName() {
        return "swap_hands";
    }

    @Override
    public boolean execute(BaseEntity player, Action action) {
        ActionPack ap = player.getActionPack();
        ap.packPlayer.resetLastActionTime();
        PlayerInventory inv = player.getBukkitPlayer().getInventory();
        ItemStack itemStack_1 = inv.getItemInOffHand();
        inv.setItemInOffHand(inv.getItemInMainHand());
        inv.setItemInMainHand(itemStack_1);
        return false;
    }
}
