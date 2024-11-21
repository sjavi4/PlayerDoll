package me.autobot.playerdoll.api.wrapper.builtin;

import me.autobot.playerdoll.api.wrapper.IWrapper;

public abstract class WDirection<T> implements IWrapper<T> {

    public abstract WDirection.Direction parse();


    public enum Direction {
        NORTH, EAST, SOUTH, WEST, UP, DOWN;
    }

}
