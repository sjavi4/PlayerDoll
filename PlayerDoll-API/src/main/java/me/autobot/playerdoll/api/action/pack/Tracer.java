package me.autobot.playerdoll.api.action.pack;

import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.wrapper.builtin.WHitResult;

public abstract class Tracer {

    private static Tracer impl = null;

    public static void setTracer(Tracer tracer) {
        impl = tracer;
    }

    public static Tracer getTracer() {
        return impl;
    }

    public abstract WHitResult<?> rayTrace(BaseEntity source, float partialTicks, double reach, boolean fluids);
//    public abstract WBlockHitResult rayTraceBlocks(BaseEntity source, float partialTicks, double reach, boolean fluids);
//    public abstract WEntityHitResult rayTraceEntities(BaseEntity source, float partialTicks, double reach, double maxSqDist);
//    public abstract WEntityHitResult rayTraceEntities(BaseEntity source, WVec3 start, WVec3 end, WAABB box, Predicate<WEntity> predicate, double maxSqDistance);

}
