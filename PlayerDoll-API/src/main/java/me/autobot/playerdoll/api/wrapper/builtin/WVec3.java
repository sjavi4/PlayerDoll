package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WVec3<T> implements IWrapper<T> {
    public abstract double x();
    public abstract double y();
    public abstract double z();
    public abstract WVec3<?> subtract(double x, double y, double z);
}
