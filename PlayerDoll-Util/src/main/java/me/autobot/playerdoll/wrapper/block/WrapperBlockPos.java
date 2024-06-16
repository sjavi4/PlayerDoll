package me.autobot.playerdoll.wrapper.block;

import me.autobot.playerdoll.wrapper.Wrapper;
import net.minecraft.core.BlockPosition;

public class WrapperBlockPos extends Wrapper<BlockPosition> {

    public static WrapperBlockPos wrap(BlockPosition blockPos) {
        return new WrapperBlockPos(blockPos);
    }

    public WrapperBlockPos(Object blockPosition) {
        super(blockPosition);
    }

    public int getY() {
        return source.v();
    }
}
