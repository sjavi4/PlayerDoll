package me.autobot.playerdoll.wrapper.block;

import me.autobot.playerdoll.wrapper.Wrapper;
import me.autobot.playerdoll.wrapper.WrapperServerLevel;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public class WrapperBlockState extends Wrapper<IBlockData> {

    public static WrapperBlockState wrap(IBlockData blockData) {
        return new WrapperBlockState(blockData);
    }
    public WrapperBlockState(Object blockData) {
        super(blockData);
    }

    public boolean isAir() {
        return source.i();
    }

    public float getDestroyProgress(Object serverPlayer, WrapperServerLevel level, WrapperBlockPos blockPos) {
        return source.a((EntityPlayer) serverPlayer, (World) level.toObj(), blockPos.getSource());
    }

    public void attack(WrapperServerLevel level, WrapperBlockPos blockPos, Object serverPlayer) {
        source.a((World) level.toObj(), blockPos.getSource(), (EntityPlayer) serverPlayer);
    }
}
