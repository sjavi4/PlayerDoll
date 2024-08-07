package me.autobot.playerdoll.wrapper.phys;

import me.autobot.playerdoll.wrapper.Wrapper;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class WrapperBlockHitResult extends Wrapper<MovingObjectPositionBlock> implements WrapperHitResult {

    public static WrapperBlockHitResult wrap(MovingObjectPositionBlock blockHitResult) {
        return new WrapperBlockHitResult(blockHitResult);
    }

//    public static WrapperBlockHitResult construct(WrapperVec3 vec3, EnumDirection direction, WrapperBlockPos pos, boolean inside) {
//        return WrapperBlockHitResult.wrap(new MovingObjectPositionBlock(vec3.getSource(), direction, pos.getSource(), inside));
//    }
    public WrapperBlockHitResult(Object blockHitResult) {
        super(blockHitResult);
    }
    @Override
    public MovingObjectPosition.EnumMovingObjectType getType() {
        return WrapperHitResult.BLOCK;
    }

    @Override
    public WrapperVec3 getLocation() {
        return WrapperVec3.wrap(source.e());
    }

    public WrapperBlockPos getBlockPos() {
        return WrapperBlockPos.wrap(source.a());
    }

    public EnumDirection getDirection() {
        return source.b();
    }
}
