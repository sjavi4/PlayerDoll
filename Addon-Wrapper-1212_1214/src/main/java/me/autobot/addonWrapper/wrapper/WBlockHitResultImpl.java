package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockHitResult;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockPos;
import me.autobot.playerdoll.api.wrapper.builtin.WDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;

@Wrapper(wrapping = BlockHitResult.class, method = "wrap")
public class WBlockHitResultImpl extends WBlockHitResult<BlockHitResult> {

    private final BlockHitResult blockHitResult;
    public static WBlockHitResultImpl wrap(BlockHitResult hitResult) {
        return new WBlockHitResultImpl(hitResult);
    }

    private WBlockHitResultImpl(BlockHitResult blockHitResult) {
        this.blockHitResult = blockHitResult;
    }
    @Override
    public WBlockPos<BlockPos> getBlockPos() {
        Class<? extends WBlockPos<BlockPos>> wrapper = (Class<? extends WBlockPos<BlockPos>>) WrapperRegistry.getWrapper(WBlockPos.class, blockHitResult.getBlockPos());
        return WrapperRegistry.wrapFrom(wrapper, blockHitResult.getBlockPos());
    }

    @Override
    public WDirection.Direction getDirection() {
        Class<? extends WDirection<Direction>> wrapper = (Class<? extends WDirection<Direction>>) WrapperRegistry.getWrapper(WDirection.class, blockHitResult.getDirection());
        return WrapperRegistry.wrapFrom(wrapper, blockHitResult.getDirection()).parse();
    }

    public BlockHitResult getInstance() {
        return blockHitResult;
    }
}
