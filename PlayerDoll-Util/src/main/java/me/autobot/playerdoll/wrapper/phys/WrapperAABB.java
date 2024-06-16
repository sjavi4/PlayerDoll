package me.autobot.playerdoll.wrapper.phys;

import me.autobot.playerdoll.wrapper.Wrapper;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

import java.util.Optional;

public class WrapperAABB extends Wrapper<AxisAlignedBB> {

    public static WrapperAABB wrap(AxisAlignedBB AABB) {
        return new WrapperAABB(AABB);
    }
    public WrapperAABB(Object boundingBox) {
        super(boundingBox);
    }

    public AxisAlignedBB expandTowards(WrapperVec3 vec3) {
        return source.b(vec3.getSource());
    }

    public AxisAlignedBB inflate(double d) {
        return source.g(d);
    }

    public Optional<Vec3D> clip(WrapperVec3 vec3, WrapperVec3 vec3d) {
        return source.b(vec3.getSource(), vec3d.getSource());
    }
    public boolean contains(WrapperVec3 vec3) {
        return source.d(vec3.getSource());
    }
}
