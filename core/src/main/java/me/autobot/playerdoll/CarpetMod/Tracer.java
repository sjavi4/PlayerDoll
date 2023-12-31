package me.autobot.playerdoll.CarpetMod;

import java.util.List;

public abstract class Tracer {
    protected abstract double getDistanceToSqr(Object source, Object blockHit, float partialTicks);
    protected abstract double[] getEyePosition(Object source, float partialTicks);
    protected abstract double[] getViewVector(Object source, float partialTicks);
    private double[] vectorAdd(double[] old, double[] add) {
        return new double[]{old[0]+add[0],old[1]+add[1],old[2]+add[2]};
    }
    protected abstract Object clip(double[] pos, double[] reachEnd, boolean fluids, Object source);
    private double[] vectorScale(double[] rot, double reach) {
        return new double[]{rot[0]*reach,rot[1]*reach,rot[2]*reach};
    }
    protected abstract double[] getBoundingBox(Object source);
    private double[] AABBExpandTowards(double[] box, double[] vec) {
        double var6 = box[0];
        double var8 = box[1];
        double var10 = box[2];
        double var12 = box[3];
        double var14 = box[4];
        double var16 = box[5];
        if (vec[0] < 0.0) {
            var6 += vec[0];
        } else if (vec[0] > 0.0) {
            var12 += vec[0];
        }

        if (vec[1] < 0.0) {
            var8 += vec[1];
        } else if (vec[1] > 0.0) {
            var14 += vec[1];
        }

        if (vec[2] < 0.0) {
            var10 += vec[2];
        } else if (vec[2] > 0.0) {
            var16 += vec[2];
        }
        return new double[]{var6, var8, var10, var12, var14, var16};
    }
    private double[] AABBInflate(double[] box, double vec) {
        double var6 = box[0] - vec;
        double var8 = box[1] - vec;
        double var10 = box[2] - vec;
        double var12 = box[3] + vec;
        double var14 = box[4] + vec;
        double var16 = box[5] + vec;
        return new double[]{var6, var8, var10, var12, var14, var16};
    }
    protected abstract List<Object> world_getEntities(Object source, double[] box);
    protected abstract double getPickRadius(Object source);
    protected abstract double[] AABBClip(double[] box, double[] start, double[] end);
    private boolean AABBContains(double[] src, double var0, double var2, double var4) {
        return var0 >= src[0] && var0 < src[3] && var2 >= src[1] && var2 < src[4] && var4 >= src[2] && var4 < src[4];
    }
    protected abstract Object getRootVehicle(Object entity);
    protected abstract Object newEntityHitResult(Object entity, double[] hit);
    protected abstract Object castServerPlayer(Object source);
    private double distanceToSqr(double[] src, double[] var0) {
        double var1 = var0[0] - src[0];
        double var3 = var0[1] - src[1];
        double var5 = var0[2] - src[2];
        return var1 * var1 + var3 * var3 + var5 * var5;
    }
    public Object rayTrace(Object source, float partialTicks, double reach, boolean fluids)
    {
        Object nmsSource = castServerPlayer(source);
        Object blockHit = rayTraceBlocks(nmsSource, partialTicks, reach, fluids);
        double maxSqDist = reach * reach;
        if (blockHit != null)
        {
            maxSqDist = getDistanceToSqr(nmsSource,blockHit,partialTicks);
        }
        Object entityHit = rayTraceEntities(nmsSource, partialTicks, reach, maxSqDist);
        return entityHit == null ? blockHit : entityHit;
    }
    //BlockHitResult
    public Object rayTraceBlocks(Object source, float partialTicks, double reach, boolean fluids)
    {
        double[] pos = getEyePosition(source, partialTicks);
        double[] rotation = getViewVector(source, partialTicks);
        double[] v = new double[]{rotation[0]*reach,rotation[1]*reach,rotation[2]*reach};
        double[] reachEnd = vectorAdd(pos,v);
        return clip(pos, reachEnd, fluids, source);
    }
    //EntityHitResult
    public Object rayTraceEntities(Object source, float partialTicks, double reach, double maxSqDist)
    {
        double[] pos = getEyePosition(source, partialTicks);
        double[] rotation = getViewVector(source, partialTicks);
        double[] reachVec = vectorScale(rotation,reach);
        double[] box = getBoundingBox(source);
        double[] expandedBox = AABBExpandTowards(box,reachVec);
        double[] inflatedBox = AABBInflate(expandedBox,1);
        return rayTraceEntities(source, pos, vectorAdd(pos,reachVec), inflatedBox, maxSqDist);
    }
    //EntityHitResult
    public Object rayTraceEntities(Object source, double[] start, double[] end, double[] box, double maxSqDistance)
    {
        double targetDistance = maxSqDistance;
        Object target = null;
        double[] targetHitPos = null;
        for (Object current : world_getEntities(source,box)) {
            double[] currentBox = AABBInflate(getBoundingBox(current),getPickRadius(source));
            double[] currentHit = AABBClip(currentBox,start,end);
            if (AABBContains(currentBox,start[0],start[1],start[2])) {
                if (targetDistance >= 0) {
                    target = current;
                    targetHitPos = currentHit == null? currentHit : start;
                    targetDistance = 0;
                }
            }
            else if (currentHit != null) {
                double currentDistance = distanceToSqr(start,currentHit);
                if (currentDistance < targetDistance || targetDistance == 0) {
                    if (getRootVehicle(current) == getRootVehicle(source))
                    {
                        if (targetDistance == 0)
                        {
                            target = current;
                            targetHitPos = currentHit;
                        }
                    }
                    else
                    {
                        target = current;
                        targetHitPos = currentHit;
                        targetDistance = currentDistance;
                    }
                }
            }
        }
        return target == null ? null : newEntityHitResult(target, targetHitPos);
    }
}
