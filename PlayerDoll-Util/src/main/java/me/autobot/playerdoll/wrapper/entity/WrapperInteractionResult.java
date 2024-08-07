package me.autobot.playerdoll.wrapper.entity;

import me.autobot.playerdoll.wrapper.Wrapper;
import net.minecraft.world.EnumInteractionResult;

public class WrapperInteractionResult extends Wrapper<EnumInteractionResult> {

    public static final EnumInteractionResult SUCCESS = EnumInteractionResult.a;
    public static final EnumInteractionResult CONSUME = EnumInteractionResult.b;
    public static final EnumInteractionResult CONSUME_PARTIAL = EnumInteractionResult.c;
    public static final EnumInteractionResult PASS = EnumInteractionResult.d;
    public static final EnumInteractionResult FAIL = EnumInteractionResult.e;


    public static WrapperInteractionResult wrap(EnumInteractionResult enumInteractionResult) {
        return new WrapperInteractionResult(enumInteractionResult);
    }
    public WrapperInteractionResult(Object enumInteractionResult) {
        super(enumInteractionResult);
    }

    public boolean consumesAction() {
        return source.a();
    }
    public boolean shouldSwing() {
        return source.b();
    }
}
