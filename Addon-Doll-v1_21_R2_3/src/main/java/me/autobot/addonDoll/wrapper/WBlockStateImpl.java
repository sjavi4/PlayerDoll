package me.autobot.addonDoll.wrapper;

import me.autobot.addonDoll.action.PackPlayerImpl;
import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockPos;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Wrapper(wrapping = BlockState.class, method = "wrap")
public class WBlockStateImpl extends WBlockState<BlockState> {

    private final BlockState blockState;

    public static WBlockStateImpl wrap(BlockState blockState) {
        return new WBlockStateImpl(blockState);
    }

    private WBlockStateImpl(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public boolean isAir() {
        return blockState.isAir();
    }

    @Override
    public float getDestroyProgress(AbsPackPlayer player, WBlockPos<?> pos) {
        return blockState.getDestroyProgress(((PackPlayerImpl)player).getServerPlayer(), ((PackPlayerImpl)player).getServerPlayer().serverLevel(), (BlockPos) pos.getInstance());
    }

    @Override
    public void attack(WBlockPos<?> pos, AbsPackPlayer packPlayer) {
        blockState.attack(((PackPlayerImpl)packPlayer).getServerPlayer().serverLevel(), (BlockPos) pos.getInstance(), ((PackPlayerImpl)packPlayer).getServerPlayer());
    }

    @Override
    public BlockState getInstance() {
        return blockState;
    }
}
