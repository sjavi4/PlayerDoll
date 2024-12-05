package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WVec2;
import net.minecraft.world.phys.Vec2;

@Wrapper(wrapping = Vec2.class, method = "wrap")
public class WVec2Impl extends WVec2<Vec2> {

    private float x;
    private float y;
    public static WVec2Impl wrap(Vec2 vec2) {
        return new WVec2Impl(vec2.x, vec2.y);
    }

    private WVec2Impl(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public Vec2 getInstance() {
        return null;
    }
}
