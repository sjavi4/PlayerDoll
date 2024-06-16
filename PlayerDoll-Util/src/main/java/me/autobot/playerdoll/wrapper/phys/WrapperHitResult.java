package me.autobot.playerdoll.wrapper.phys;

import net.minecraft.world.phys.MovingObjectPosition;

public interface WrapperHitResult {
    MovingObjectPosition.EnumMovingObjectType MISS = MovingObjectPosition.EnumMovingObjectType.a;
    MovingObjectPosition.EnumMovingObjectType BLOCK = MovingObjectPosition.EnumMovingObjectType.b;
    MovingObjectPosition.EnumMovingObjectType ENTITY = MovingObjectPosition.EnumMovingObjectType.c;

    MovingObjectPosition.EnumMovingObjectType getType();
    WrapperVec3 getLocation();
}
