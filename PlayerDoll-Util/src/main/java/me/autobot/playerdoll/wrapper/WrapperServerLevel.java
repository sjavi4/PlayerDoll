package me.autobot.playerdoll.wrapper;

import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.block.WrapperBlockState;

public interface WrapperServerLevel {
    Object toObj();
    void destroyBlockProgress(int i, WrapperBlockPos blockPos, int j);
    WrapperBlockState getBlockState(WrapperBlockPos blockPos);
    boolean mayInteract(Object serverPlayer, WrapperBlockPos blockPos);
}
