package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockPos;
import net.minecraft.core.BlockPos;

@Wrapper(wrapping = BlockPos.class, method = "wrap")
public class WBlockPosImpl extends WBlockPos<BlockPos> {

    private final BlockPos blockPos;

    public static WBlockPosImpl wrap(BlockPos blockPos) {
        return new WBlockPosImpl(blockPos);
    }

    private WBlockPosImpl(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public int getY() {
        return blockPos.getY();
    }

    public BlockPos getInstance() {
        return blockPos;
    }

    @Override
    public boolean equals(Object pos) {
        if (this == pos) {
            return true;
        }
        if (pos instanceof WBlockPosImpl pos1) {
            return blockPos.equals(pos1.blockPos);
        }
        return false;
    }
}
