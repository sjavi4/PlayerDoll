package me.autobot.playerdoll.api.wrapper.builtin;


import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WBlockHitResult<T> implements IWrapper<T> {
    public abstract WBlockPos<?> getBlockPos();

    public abstract WDirection.Direction getDirection();
}
