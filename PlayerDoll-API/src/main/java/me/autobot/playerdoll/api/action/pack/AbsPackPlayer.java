package me.autobot.playerdoll.api.action.pack;

import me.autobot.playerdoll.api.wrapper.builtin.*;
import org.bukkit.inventory.ItemStack;

public abstract class AbsPackPlayer {

    public abstract void setZZA(float v);

    public abstract void setXXA(float v);

    public abstract void setJumping(boolean b);

    public abstract void jumpFromGround();

    public abstract void resetLastActionTime();

    public abstract void releaseUsingItem();
    public abstract void look(float yaw, float pitch);
    public abstract void lookAt(WVec3<?> vec3);
    public abstract void lookAt(double x, double y, double z);
    public abstract Enum<?>[] getInteractionHandEnums();

    public abstract ItemStack getItemInHand(Enum<?> hand);
    public abstract void dropItem(int slot, boolean dropAll, int count);
    public abstract WServerLevel<?> serverLevel();

    public abstract void resetAttackStrengthTicker();

    public abstract WInteractionResult<?> interactOn(WEntity<?> entity, Enum<?> hand);

    public abstract WInteractionResult<?> useItem(Enum<?> hand);

    public abstract WInteractionResult<?> useItemOn(WServerLevel<?> world, ItemStack itemInHand, Enum<?> hand, WBlockHitResult<?> blockHit);

    public abstract boolean blockActionRestricted(WBlockPos<?> pos);

    public abstract void destroyBlockProgress(int i, WBlockPos<?> pos, int i1);

    public abstract void handleBlockBreakAction(WBlockPos<?> pos, WServerPlayerAction.Action action, WDirection.Direction side, int maxHeight, int i);
}
