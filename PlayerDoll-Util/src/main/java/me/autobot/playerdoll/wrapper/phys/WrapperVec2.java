package me.autobot.playerdoll.wrapper.phys;

import me.autobot.playerdoll.wrapper.Wrapper;
import net.minecraft.world.phys.Vec2F;

public class WrapperVec2 extends Wrapper<Vec2F> {
    public final float x;
    public final float y;

    public static WrapperVec2 wrap(Vec2F vec2) {
        return new WrapperVec2(vec2);
    }
    public WrapperVec2(Object vec2f) {
        super(vec2f);
        x = source.i;
        y = source.j;
    }
}
