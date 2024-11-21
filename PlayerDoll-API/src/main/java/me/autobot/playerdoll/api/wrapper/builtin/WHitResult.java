package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.wrapper.IWrapper;

import java.util.function.Function;

public abstract class WHitResult<T> implements IWrapper<T> {

    public abstract Boolean parseResultForUse(Function<WBlockHitResult<?>, Boolean> blockHit, Function<WEntityHitResult<?>, Boolean> entityHit);
    public abstract boolean parseResultForAttack(Function<WBlockHitResult<?>, Boolean> blockHit, Function<WEntityHitResult<?>, Boolean> entityHit);
}
