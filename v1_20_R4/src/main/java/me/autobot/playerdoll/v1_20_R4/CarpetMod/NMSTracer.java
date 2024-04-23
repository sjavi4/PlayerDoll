package me.autobot.playerdoll.v1_20_R4.CarpetMod;

import me.autobot.playerdoll.CarpetMod.Tracer;
import me.autobot.playerdoll.Dolls.IServerPlayerExt;
import me.autobot.playerdoll.v1_20_R4.player.ServerDoll;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NMSTracer extends Tracer {
    @Override
    protected double getDistanceToSqr(Object source, Object blockHit, float partialTicks) {
        double[] pos = getEyePosition(source,partialTicks);
        return ((BlockHitResult)blockHit).getLocation().distanceToSqr(new Vec3(pos[0],pos[1],pos[2]));
    }

    @Override
    protected double[] getEyePosition(Object source, float partialTicks) {
        Vec3 pos = ((Entity)source).getEyePosition(partialTicks);
        return new double[]{pos.x,pos.y,pos.z};
    }

    @Override
    protected double[] getViewVector(Object source, float partialTicks) {
        Vec3 rotation = ((Entity)source).getViewVector(partialTicks);
        return new double[]{rotation.x,rotation.y,rotation.z};
    }
    @Override
    protected Object clip(double[] pos, double[] reachEnd, boolean fluids, Object source) {
        return ((Entity)source).level().clip(new ClipContext(new Vec3(pos[0],pos[1],pos[2]), new Vec3(reachEnd[0],reachEnd[1],reachEnd[2]), ClipContext.Block.OUTLINE, fluids ?
                ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, (Entity)source));
    }

    @Override
    protected double[] getBoundingBox(Object source) {
        AABB box = ((Entity)source).getBoundingBox();
        return new double[]{box.minX,box.minY,box.minZ,box.maxX,box.maxY,box.maxZ};
    }

    @Override
    protected List<Object> world_getEntities(Object source, double[] box) {
        Entity entity = (Entity) source;
        return new ArrayList<>(entity.level().getEntities(entity, new AABB(box[0], box[1], box[2], box[3], box[4], box[5]), e -> !e.isSpectator() && e.isPickable()));
    }

    @Override
    protected double getPickRadius(Object source) {
        return ((Entity)source).getPickRadius();
    }

    @Override
    protected double[] AABBClip(double[] box, double[] start, double[] end) {
        AABB b = new AABB(box[0], box[1], box[2], box[3], box[4], box[5]);
        Optional<Vec3> o = b.clip(new Vec3(start[0],start[1],start[2]),new Vec3(end[0],end[1],end[2]));
        return o.map(vec3 -> new double[]{vec3.x, vec3.y, vec3.z}).orElse(null);
    }

    @Override
    protected Object getRootVehicle(Object entity) {
        return ((Entity)entity).getRootVehicle();
    }

    @Override
    protected Object newEntityHitResult(Object entity, double[] hit) {
        return new EntityHitResult((Entity) entity,new Vec3(hit[0],hit[1],hit[2]));
    }
    @Override
    protected Object castServerPlayer(Object source) {
        return (IServerPlayerExt)source;
    }
}
