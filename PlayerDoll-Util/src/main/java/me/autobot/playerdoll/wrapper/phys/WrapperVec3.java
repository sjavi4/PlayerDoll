package me.autobot.playerdoll.wrapper.phys;

import me.autobot.playerdoll.wrapper.Wrapper;
import net.minecraft.world.phys.Vec3D;

public class WrapperVec3 extends Wrapper<Vec3D> {

    public final double x;
    public final double y;
    public final double z;

    public static WrapperVec3 construct(double x, double y, double z) {
        return WrapperVec3.wrap(new Vec3D(x,y,z));
    }

    public static WrapperVec3 wrap(Vec3D vec3) {
        return new WrapperVec3(vec3);
    }


    public WrapperVec3(Object vec3d) {
        super(vec3d);
        x = source.c;
        y = source.d;
        z = source.e;
    }

    public double distanceToSqr(WrapperVec3 vec3) {
        return source.g(vec3.source);
    }
    public Vec3D scale(double d) {
        return source.a(d);
    }
    public Vec3D add(WrapperVec3 vec3) {
        return source.e(vec3.source);
    }
    public Vec3D add(double x, double y ,double z) {
        return source.b(x, y, z);
    }

    public Vec3D subtract(double x, double y, double z) {
        return source.a(x, y, z);
    }

}
