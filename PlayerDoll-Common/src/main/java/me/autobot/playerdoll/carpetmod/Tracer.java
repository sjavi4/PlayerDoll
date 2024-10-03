package me.autobot.playerdoll.carpetmod;

import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.wrapper.entity.WrapperEntity;
import me.autobot.playerdoll.wrapper.phys.WrapperBlockHitResult;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;


public class Tracer {
    private static final Field NMS_ENTITY_LEVEL_FIELD;
    private static final Constructor<?> clipContextConstructor;
    private static final Class<?> levelClass;
    private static final Method levelClipMethod;

    private static final Enum<?> clipBlockEnumOutline;
    private static final Enum<?> clipFluidEnumNone;
    private static final Enum<?> clipFluidEnumAny;


    static {
        Field ENTITY_FIELD = Arrays.stream(ReflectionUtil.getCBClass("entity.CraftEntity").getDeclaredFields())
                .filter(field -> field.getName().equals("entity"))
                .findFirst()
                .orElseThrow();
        ENTITY_FIELD.setAccessible(true);

        // NMS ENTITY CLASS
        Class<?> NMS_ENTITY_CLASS = ENTITY_FIELD.getType();


        levelClass = ReflectionUtil.getClass("net.minecraft.world.level.World");
        NMS_ENTITY_LEVEL_FIELD = Arrays.stream(NMS_ENTITY_CLASS.getDeclaredFields())
                // Level (mojang) / World
                .filter(field -> field.getType() == levelClass)
                .findFirst()
                .orElseThrow();
        NMS_ENTITY_LEVEL_FIELD.setAccessible(true);

        // BlockHitResult (mojang) / MovingObjectPositionBlock
        Class<?> blockHitResultClass = ReflectionUtil.getClass("net.minecraft.world.phys.MovingObjectPositionBlock");
        // BlockGetter (mojang) / IBlockAccess
        Class<?> blockGetterClass = ReflectionUtil.getClass("net.minecraft.world.level.IBlockAccess");
        Objects.requireNonNull(blockGetterClass, "blockGetterClass");
        // ClipContext (mojang) / RayTrace
        final String clipContextClassName = "net.minecraft.world.level.RayTrace";
        Class<?> clipContextClass = ReflectionUtil.getClass(clipContextClassName);
        Class<?> clipContextFluidEnumClass = ReflectionUtil.getClass(clipContextClassName + "$FluidCollisionOption");
        Class<?> clipContextBlockEnumClass = ReflectionUtil.getClass(clipContextClassName + "$BlockCollisionOption");

        Objects.requireNonNull(clipContextClass, "clipContextClass");
        Objects.requireNonNull(clipContextFluidEnumClass, "clipContextFluidEnumClass");
        Objects.requireNonNull(clipContextBlockEnumClass, "clipContextBlockEnumClass");

        clipContextConstructor = Arrays.stream(clipContextClass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 5 && constructor.getParameterTypes()[4] == NMS_ENTITY_CLASS)
                .findFirst()
                .orElseThrow();

        levelClipMethod = Arrays.stream(blockGetterClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == blockHitResultClass && method.getParameterCount() == 1 && method.getParameterTypes()[0] == clipContextClass)
                .findFirst()
                .orElseThrow();
        levelClipMethod.setAccessible(true);


        try {
            Enum<?>[] blockEnum = (Enum<?>[]) clipContextBlockEnumClass.getMethod("values").invoke(null);
            clipBlockEnumOutline = blockEnum[1];
            Enum<?>[] fluidEnum = (Enum<?>[]) clipContextFluidEnumClass.getMethod("values").invoke(null);
            clipFluidEnumNone = fluidEnum[0];
            clipFluidEnumAny = fluidEnum[2];
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static RayTraceResult rayTrace(LivingEntity source, double reach, boolean fluids) {
        RayTraceResult blockHit = rayTraceBlocks(source, reach, fluids);
        double maxSqDist = reach * reach;
        if (blockHit != null) {
            maxSqDist = blockHit.getHitBlock().getLocation().distanceSquared(source.getEyeLocation());
        }
        RayTraceResult entityHit = rayTraceEntities(source, Math.sqrt(maxSqDist));
        return entityHit == null ? blockHit : entityHit;
    }

    public static RayTraceResult rayTraceBlocks(LivingEntity source, double reach, boolean fluids) {
        Vector rotation = source.getEyeLocation().getDirection();
        return source.getWorld().rayTraceBlocks(source.getEyeLocation(), rotation, reach, fluids ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
    }

    // For Use Action
    public static WrapperBlockHitResult rayTraceBlocks(ActionPackPlayer packPlayer, Object source, double reach, boolean fluids, LivingEntity livingEntity)
    {
        Location eyeLoc = livingEntity.getEyeLocation();
        WrapperEntity entity = packPlayer.wrapEntity(ReflectionUtil.getNMSEntity(livingEntity));
        Object viewVec = entity.getViewVector(1.0f);
        //Vector viewVec = livingEntity.getEyeLocation().getDirection();
        WrapperVec3 pos = WrapperVec3.construct(eyeLoc.getX(), eyeLoc.getY(),eyeLoc.getZ());
        WrapperVec3 rotation = new WrapperVec3(viewVec);
        WrapperVec3 reachEnd = new WrapperVec3(pos.add(rotation.x * reach, rotation.y * reach, rotation.z * reach));
        return new WrapperBlockHitResult(getLevelClip(source, newClipContext(pos.getSource(), reachEnd.getSource(), clipBlockEnumOutline, fluids ?
                clipFluidEnumAny : clipFluidEnumNone, source)));
    }

    public static RayTraceResult rayTraceEntities(LivingEntity source, double reach) {
        Predicate<? super Entity> predicate = entity -> entity != source && source.getVehicle() != entity && entity.getVehicle() != source;
        return source.getWorld().rayTraceEntities(source.getEyeLocation(), source.getEyeLocation().getDirection(), reach, predicate);
    }


//    public static Object rayTraceEntities(LivingEntity source, Vector start, Vector end, BoundingBox box, double maxSqDistance) {
//        double targetDistance = maxSqDistance;
//        Entity target = null;
//        Vector targetHitPos = null;
//        for (Entity current : source.getNearbyEntities(box.getCenterX(), box.getCenterY(), box.getCenterZ())) {
//            BoundingBox currentBox = current.getBoundingBox();
//            Optional<Vector> currentHit = clip(currentBox, start, end);
//            if (currentBox.contains(start)) {
//                if (targetDistance >= 0) {
//                    target = current;
//                    targetHitPos = currentHit.orElse(start);
//                    targetDistance = 0;
//                }
//            } else if (currentHit.isPresent()) {
//                Vector currentHitPos = currentHit.get();
//                double currentDistance = start.distanceSquared(currentHitPos);
//                if (currentDistance < targetDistance || targetDistance == 0) {
//                    if (current.getVehicle() == source.getVehicle()) {
//                        if (targetDistance == 0) {
//                            target = current;
//                            targetHitPos = currentHitPos;
//                        }
//                    } else {
//                        target = current;
//                        targetHitPos = currentHitPos;
//                        targetDistance = currentDistance;
//                    }
//                }
//            }
//        }
//        return null;
//        //return target == null ? null : new WrapperEntityHitResult(entityHitResultNewInstance(target, targetHitPos.getSource()));
//    }

    
//    private static Optional<Vector> clip(BoundingBox box, Vector start, Vector end) {
//        double[] var2 = new double[]{1.0};
//        double var3 = end.getX() - start.getX();
//        double var5 = end.getY() - start.getY();
//        double var7 = end.getZ() - start.getZ();
//        boolean var9 = hasDirection(box, start, var2, var3, var5, var7);
//        if (!var9) {
//            return Optional.empty();
//        } else {
//            double var10 = var2[0];
//            Vector v = new Vector(var10 * var3, var10 * var5, var10 * var7);
//            return Optional.of(start.add(v));
//        }
//    }
//
//    private static boolean hasDirection(BoundingBox var0, Vector var1, double[] var2, double var4, double var6, double var8) {
//        boolean var3 = false;
//        if (var4 > 1.0E-7) {
//            var3 = clipPoint(var2, var4, var6, var8, var0.getMinX(), var0.getMinY(), var0.getMaxY(), var0.getMinZ(), var0.getMaxZ(), var1.getX(), var1.getY(), var1.getZ());
//        } else if (var4 < -1.0E-7) {
//            var3 = clipPoint(var2, var4, var6, var8, var0.getMaxX(), var0.getMinY(), var0.getMaxY(), var0.getMinZ(), var0.getMaxZ(), var1.getX(), var1.getY(), var1.getZ());
//        }
//
//        if (var6 > 1.0E-7) {
//            var3 = clipPoint(var2, var6, var8, var4, var0.getMinY(), var0.getMinZ(), var0.getMaxZ(), var0.getMinX(), var0.getMaxX(), var1.getY(), var1.getZ(), var1.getX());
//        } else if (var6 < -1.0E-7) {
//            var3 = clipPoint(var2, var6, var8, var4, var0.getMaxY(), var0.getMinZ(), var0.getMaxZ(), var0.getMinX(), var0.getMaxX(), var1.getY(), var1.getZ(), var1.getX());
//        }
//
//        if (var8 > 1.0E-7) {
//            var3 = clipPoint(var2, var8, var4, var6, var0.getMinZ(), var0.getMinX(), var0.getMaxX(), var0.getMinY(), var0.getMaxY(), var1.getZ(), var1.getX(), var1.getY());
//        } else if (var8 < -1.0E-7) {
//            var3 = clipPoint(var2, var8, var4, var6, var0.getMaxZ(), var0.getMinX(), var0.getMaxX(), var0.getMinY(), var0.getMaxY(), var1.getZ(), var1.getX(), var1.getY());
//        }
//
//        return var3;
//    }
//
//    private static boolean clipPoint(double[] var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var19, double var21, double var23) {
//        double var25 = (var8 - var19) / var2;
//        double var27 = var21 + var25 * var4;
//        double var29 = var23 + var25 * var6;
//        if (0.0 < var25 && var25 < var0[0] && var10 - 1.0E-7 < var27 && var27 < var12 + 1.0E-7 && var14 - 1.0E-7 < var29 && var29 < var16 + 1.0E-7) {
//            var0[0] = var25;
//            return true;
//        } else {
//            return false;
//        }
//    }

    private static Object entityGetLevel(Object nmsEntity) {
        return ReflectionUtil.getField(levelClass, NMS_ENTITY_LEVEL_FIELD, nmsEntity);
    }
    private static Object newClipContext(Object vec3, Object vec3d, Enum<?> raytrace_blockcollisionoption, Enum<?> raytrace_fluidcollisionoption, Object nmsEntity) {
        return ReflectionUtil.newInstance(clipContextConstructor, vec3, vec3d, raytrace_blockcollisionoption, raytrace_fluidcollisionoption, nmsEntity);
    }
    private static Object getLevelClip(Object nmsEntity, Object clipContext) {
        return ReflectionUtil.invokeMethod(levelClipMethod, entityGetLevel(nmsEntity), clipContext);
    }
}
