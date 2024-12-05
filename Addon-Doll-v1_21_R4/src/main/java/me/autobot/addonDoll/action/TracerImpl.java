package me.autobot.addonDoll.action;

import me.autobot.playerdoll.api.action.pack.Tracer;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WHitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.Optional;
import java.util.function.Predicate;

public class TracerImpl extends Tracer {


    @Override
    public WHitResult<HitResult> rayTrace(BaseEntity source, float partialTicks, double reach, boolean fluids) {
        HitResult result = NMSRayTrace(source, partialTicks, reach, fluids);
        Class<? extends WHitResult<HitResult>> wrapper = (Class<? extends WHitResult<HitResult>>) WrapperRegistry.getWrapper(WHitResult.class, result);
        return WrapperRegistry.wrapFrom(wrapper, result);
    }

    private HitResult NMSRayTrace(BaseEntity source, float partialTicks, double reach, boolean fluids) {
        BlockHitResult blockHit = NMSRayTraceBlocks(source, partialTicks, reach, fluids);
        double maxSqDist = reach * reach;
        if (blockHit != null)
        {
            maxSqDist = blockHit.getLocation().distanceToSqr(((ServerPlayer)source).getEyePosition(partialTicks));
        }
        EntityHitResult entityHit = NMSRayTraceEntities(source, partialTicks, reach, maxSqDist);
        return entityHit == null ? blockHit : entityHit;
    }

    private BlockHitResult NMSRayTraceBlocks(BaseEntity source, float partialTicks, double reach, boolean fluids) {
        Vec3 pos = ((ServerPlayer)source).getEyePosition(partialTicks);
        Vec3 rotation = ((ServerPlayer)source).getViewVector(partialTicks);
        Vec3 reachEnd = pos.add(rotation.x * reach, rotation.y * reach, rotation.z * reach);
        return ((ServerPlayer)source).level().clip(new ClipContext(pos, reachEnd, ClipContext.Block.OUTLINE, fluids ?
                ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, ((ServerPlayer)source)));
    }

    private EntityHitResult NMSRayTraceEntities(BaseEntity source, float partialTicks, double reach, double maxSqDist) {
        Vec3 pos = ((ServerPlayer)source).getEyePosition(partialTicks);
        Vec3 reachVec = ((ServerPlayer)source).getViewVector(partialTicks).scale(reach);
        AABB box = ((ServerPlayer)source).getBoundingBox().expandTowards(reachVec).inflate(1);
        return NMSRayTraceEntities(source, pos, pos.add(reachVec), box, e -> !e.isSpectator() && e.isPickable(), maxSqDist);
    }

    private EntityHitResult NMSRayTraceEntities(BaseEntity source, Vec3 start, Vec3 end, AABB box, Predicate<Entity> predicate, double maxSqDistance) {
        Level world = ((ServerPlayer)source).level();
        double targetDistance = maxSqDistance;
        Entity target = null;
        Vec3 targetHitPos = null;
        for (Entity current : world.getEntities(((ServerPlayer)source), box, predicate))
        {
            AABB currentBox = current.getBoundingBox().inflate(current.getPickRadius());
            Optional<Vec3> currentHit = currentBox.clip(start, end);
            if (currentBox.contains(start))
            {
                if (targetDistance >= 0)
                {
                    target = current;
                    targetHitPos = currentHit.orElse(start);
                    targetDistance = 0;
                }
            }
            else if (currentHit.isPresent())
            {
                Vec3 currentHitPos = currentHit.get();
                double currentDistance = start.distanceToSqr(currentHitPos);
                if (currentDistance < targetDistance || targetDistance == 0)
                {
                    if (current.getRootVehicle() == ((ServerPlayer)source).getRootVehicle())
                    {
                        if (targetDistance == 0)
                        {
                            target = current;
                            targetHitPos = currentHitPos;
                        }
                    }
                    else
                    {
                        target = current;
                        targetHitPos = currentHitPos;
                        targetDistance = currentDistance;
                    }
                }
            }
        }
        return target == null ? null : new EntityHitResult(target, targetHitPos);
    }
}
