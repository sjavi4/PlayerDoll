package me.autobot.playerdoll.wrapper.phys;

import me.autobot.playerdoll.wrapper.Wrapper;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;

public class WrapperEntityHitResult extends Wrapper<MovingObjectPositionEntity> implements WrapperHitResult {


    public static WrapperEntityHitResult wrap(MovingObjectPositionEntity entityHitResult) {
        return new WrapperEntityHitResult(entityHitResult);
    }
    public WrapperEntityHitResult(Object entityHitResult) {
        super(entityHitResult);
    }

    @Override
    public MovingObjectPosition.EnumMovingObjectType getType() {
        return WrapperHitResult.ENTITY;
    }

    @Override
    public WrapperVec3 getLocation() {
        return WrapperVec3.wrap(source.e());
    }

    public Object getEntity() {
        return source.a();
    }
}
