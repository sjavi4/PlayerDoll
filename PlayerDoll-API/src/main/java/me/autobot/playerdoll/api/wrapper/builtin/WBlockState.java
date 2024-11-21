package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WBlockState<T> implements IWrapper<T> {
    public abstract boolean isAir();

    public abstract float getDestroyProgress(AbsPackPlayer player, WBlockPos<?> pos);

    public abstract void attack(WBlockPos<?> pos, AbsPackPlayer packPlayer);
}
