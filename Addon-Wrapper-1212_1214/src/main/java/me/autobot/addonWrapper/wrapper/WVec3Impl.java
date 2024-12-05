package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WVec3;
import net.minecraft.world.phys.Vec3;

@Wrapper(wrapping = Vec3.class, method = "wrap")
public class WVec3Impl extends WVec3<Vec3> {
    private final Vec3 vec3;
    public static WVec3Impl wrap(Vec3 vec3) {
        return new WVec3Impl(vec3);
    }

    private WVec3Impl(Vec3 vec3) {
        this.vec3 = vec3;
    }

    @Override
    public double x() {
        return vec3.x();
    }

    @Override
    public double y() {
        return vec3.y();
    }

    @Override
    public double z() {
        return vec3.z();
    }

    @Override
    public WVec3<Vec3> subtract(double x, double y, double z) {
        return new WVec3Impl(vec3.subtract(x, y, z));
    }

    @Override
    public Vec3 getInstance() {
        return vec3;
    }
}
