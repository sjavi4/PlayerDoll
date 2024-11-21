package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WEntityHitResult<T> implements IWrapper<T> {
    public abstract WEntity<?> getEntity();

    public abstract WVec3<?> getLocation();
}
