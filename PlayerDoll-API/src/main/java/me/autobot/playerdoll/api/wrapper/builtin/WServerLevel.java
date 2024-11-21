package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WServerLevel<T> implements IWrapper<T> {
    public abstract boolean mayInteract(AbsPackPlayer player, WBlockPos<?> pos);

    public abstract WBlockState<?> getBlockState(WBlockPos<?> pos);
}
