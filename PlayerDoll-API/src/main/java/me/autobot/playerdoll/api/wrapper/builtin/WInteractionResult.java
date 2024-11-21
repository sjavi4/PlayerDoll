package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WInteractionResult<T> implements IWrapper<T> {

    public abstract boolean consumesAction();
    public abstract boolean shouldSwing();
}
