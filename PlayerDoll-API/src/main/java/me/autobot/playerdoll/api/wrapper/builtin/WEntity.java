package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.IWrapper;
import org.bukkit.entity.Entity;

public abstract class WEntity<T> implements IWrapper<T> {
    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();

    public abstract WInteractionResult<?> interactAt(AbsPackPlayer player, WVec3<?> relativeHitPos, Enum<?> hand);

    public abstract Entity getBukkitEntity();
}
