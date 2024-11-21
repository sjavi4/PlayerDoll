package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WServerPlayerAction<T> implements IWrapper<T> {


    public enum Action {
        ABORT_DESTROY_BLOCK, START_DESTROY_BLOCK, STOP_DESTROY_BLOCK
    }
}
