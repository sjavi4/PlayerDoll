package me.autobot.playerdoll.v1_21_R1.wrapper;

import me.autobot.playerdoll.wrapper.Wrapper;
import me.autobot.playerdoll.wrapper.WrapperServerLevel;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.block.WrapperBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class NMSServerLevel extends Wrapper<ServerLevel> implements WrapperServerLevel {
    public NMSServerLevel(Object level) {
        super(level);
    }

    @Override
    public void destroyBlockProgress(int i, WrapperBlockPos blockPos, int j) {
        source.destroyBlockProgress(i, (BlockPos) blockPos.toObj(), j);
    }

    @Override
    public WrapperBlockState getBlockState(WrapperBlockPos blockPos) {
        return new WrapperBlockState(source.getBlockState((BlockPos) blockPos.toObj()));
    }
    @Override
    public boolean mayInteract(Object serverPlayer, WrapperBlockPos blockPos) {
        return source.mayInteract((Player) serverPlayer, (BlockPos) blockPos.toObj());
    }
}
